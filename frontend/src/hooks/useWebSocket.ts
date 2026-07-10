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
 * Hook to manage STOMP WebSocket connection for real-time messaging.
 * SockJS requires an http(s) URL (not ws://).
 */
export function useWebSocket(callbacks?: WebSocketCallbacks) {
  const [state, setState] = useState<WebSocketState>({ connected: false, error: null });
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<StompSubscription[]>([]);
  const retryRef = useRef<number | null>(null);
  // Invalidate in-flight onConnect after disconnect (Strict Mode remount / teardown races).
  const connectGenerationRef = useRef(0);
  // Always call the latest handlers — never close over `callbacks` from connect().
  const callbacksRef = useRef(callbacks);
  callbacksRef.current = callbacks;

  const connect = useCallback(async () => {
    try {
      const token = getToken();
      if (!token) {
        // No token yet (user not logged in). Retry shortly so connection is established
        // automatically after login without requiring a page refresh.
        setState({ connected: false, error: 'No authentication token' });
        try {
          if (retryRef.current) {
            window.clearTimeout(retryRef.current);
          }
        } catch (e) {
          /* ignore in non-browser env */
        }
        retryRef.current = window.setTimeout(() => {
          connect();
        }, 2000) as unknown as number;
        return;
      }

      if (clientRef.current?.active) {
        return;
      }

      const baseUrl = ((import.meta as any).env?.VITE_API_BASE_URL || '').replace(/\/$/, '');
      // SockJS handshake uses HTTP, not ws://
      const sockJsUrl = `${baseUrl}/ws`;

      const generation = ++connectGenerationRef.current;

      const client = new Client({
        webSocketFactory: () => new SockJS(sockJsUrl),
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        onConnect: () => {
          // Ignore connect callbacks from a client that was already torn down.
          if (generation !== connectGenerationRef.current) {
            return;
          }

          setState({ connected: true, error: null });

          // Drop prior subs before re-subscribing (STOMP reconnects re-run onConnect).
          subscriptionsRef.current.forEach((sub) => {
            try {
              sub.unsubscribe();
            } catch {
              /* ignore */
            }
          });

          const messageSub = client.subscribe('/user/queue/messages', (frame: Frame) => {
            try {
              const message = JSON.parse(frame.body) as ChatMessage;
              callbacksRef.current?.onMessage?.(message);
            } catch (e) {
              console.error('Failed to parse message:', e);
            }
          });

          const errorSub = client.subscribe('/user/queue/errors', (frame: Frame) => {
            try {
              const error = JSON.parse(frame.body) as ChatError;
              callbacksRef.current?.onError?.(error);
            } catch (e) {
              console.error('Failed to parse error:', e);
            }
          });

          subscriptionsRef.current = [messageSub, errorSub];
          callbacksRef.current?.onConnect?.();
        },
        onDisconnect: () => {
          if (generation !== connectGenerationRef.current) {
            return;
          }
          setState({ connected: false, error: null });
          subscriptionsRef.current = [];
          callbacksRef.current?.onDisconnect?.();
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
  }, []);

  const disconnect = useCallback(() => {
    // Bump generation so any late onConnect from the old client is ignored.
    connectGenerationRef.current += 1;
    try {
      if (retryRef.current) {
        window.clearTimeout(retryRef.current);
        retryRef.current = null;
      }
    } catch (e) {
      /* ignore in non-browser env */
    }
    subscriptionsRef.current.forEach((sub) => {
      try {
        sub.unsubscribe();
      } catch (e) {
        console.error('Failed to unsubscribe:', e);
      }
    });
    subscriptionsRef.current = [];

    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
    }
  }, []);

  const sendMessage = useCallback((payload: { receiverId: string; content: string }) => {
    if (!clientRef.current || !clientRef.current.connected) {
      setState((prev) => ({ ...prev, error: 'WebSocket not connected' }));
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
      setState((prev) => ({ ...prev, error: errorMsg }));
      console.error(errorMsg, err);
      return false;
    }
  }, []);

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
