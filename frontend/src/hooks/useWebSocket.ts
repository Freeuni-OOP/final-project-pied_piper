import { useEffect, useRef, useState, useCallback } from 'react';
import { Client, Frame, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getToken } from '../auth/tokenStorage';
import { ChatMessage, ChatError } from '../types/chat';

interface WebSocketState {
  connected: boolean;
  error: string | null;
}

interface WebSocketCallbacks {
  onMessage?: (message: ChatMessage) => void;
  onError?: (error: ChatError) => void;
  onConnect?: () => void;
  onDisconnect?: () => void;
}

/**
 * Hook to manage STOMP WebSocket connection for real-time messaging
 *
 * Usage:
 * const ws = useWebSocket({
 *   onMessage: (msg) => console.log(msg),
 *   onError: (err) => console.error(err),
 * });
 *
 * // Send a message
 * ws.sendMessage({ receiverId: 'uuid', content: 'hello' });
 *
 * // Clean up on unmount is automatic
 */
export function useWebSocket(callbacks?: WebSocketCallbacks) {
  const [state, setState] = useState<WebSocketState>({ connected: false, error: null });
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<StompSubscription[]>([]);
  const reconnectAttemptsRef = useRef(0);
  const maxReconnectAttemptsRef = useRef(5);

  const connect = useCallback(async () => {
    try {
      const token = getToken();
      if (!token) {
        setState({ connected: false, error: 'No authentication token' });
        return;
      }

      const baseUrl = (import.meta as any).env?.VITE_API_BASE_URL || '';
      const wsUrl = `${baseUrl.replace(/^https?:/, 'ws:').replace(/\/$/, '')}/ws`;

      const client = new Client({
        webSocketFactory: () => new SockJS(wsUrl),
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        onConnect: () => {
          setState({ connected: true, error: null });
          reconnectAttemptsRef.current = 0;

          // Subscribe to message queue for this user
          const messageSub = client.subscribe('/user/queue/messages', (frame: Frame) => {
            try {
              const message = JSON.parse(frame.body) as ChatMessage;
              callbacks?.onMessage?.(message);
            } catch (e) {
              console.error('Failed to parse message:', e);
            }
          });

          // Subscribe to error queue
          const errorSub = client.subscribe('/user/queue/errors', (frame: Frame) => {
            try {
              const error = JSON.parse(frame.body) as ChatError;
              callbacks?.onError?.(error);
            } catch (e) {
              console.error('Failed to parse error:', e);
            }
          });

          subscriptionsRef.current = [messageSub, errorSub];
          callbacks?.onConnect?.();
        },
        onDisconnect: () => {
          setState({ connected: false, error: null });
          subscriptionsRef.current = [];
          callbacks?.onDisconnect?.();
        },
        onStompError: (frame: Frame) => {
          const errorMsg = `WebSocket error: ${frame.body}`;
          setState({ connected: false, error: errorMsg });
          console.error(errorMsg);
        },
        onWebSocketError: (event: Event) => {
          const errorMsg = 'WebSocket connection error';
          setState({ connected: false, error: errorMsg });
          console.error(errorMsg, event);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 30000,
        heartbeatOutgoing: 30000,
      });

      clientRef.current = client;
      client.activate();
    } catch (err: any) {
      const errorMsg = err?.message || 'Failed to connect to WebSocket';
      setState({ connected: false, error: errorMsg });
      console.error(errorMsg, err);
    }
  }, [callbacks]);

  const disconnect = useCallback(() => {
    if (clientRef.current && clientRef.current.connected) {
      clientRef.current.deactivate();
    }
    subscriptionsRef.current.forEach((sub) => {
      try {
        sub.unsubscribe();
      } catch (e) {
        console.error('Failed to unsubscribe:', e);
      }
    });
    subscriptionsRef.current = [];
  }, []);

  const sendMessage = useCallback(
    (payload: { receiverId: string; content: string }) => {
      if (!clientRef.current || !clientRef.current.connected) {
        setState({ ...state, error: 'WebSocket not connected' });
        return false;
      }

      try {
        clientRef.current.publish({
          destination: '/app/chat.send',
          body: JSON.stringify(payload),
        });
        return true;
      } catch (err: any) {
        const errorMsg = err?.message || 'Failed to send message';
        setState({ ...state, error: errorMsg });
        console.error(errorMsg, err);
        return false;
      }
    },
    [state]
  );

  // Auto-connect on mount, disconnect on unmount
  useEffect(() => {
    connect();

    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  return {
    ...state,
    sendMessage,
    disconnect,
  };
}

