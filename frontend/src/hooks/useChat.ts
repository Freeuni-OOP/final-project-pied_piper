import { useState, useCallback, useEffect, useRef } from 'react';
import { ChatMessage, Conversation } from '../types/chat';
import {
  getChatHistory,
  getConversations,
  markConversationAsRead,
  markMessageAsRead,
  sendMessage as sendMessageApi,
} from '../api/chatApi';
import { useWebSocket } from './useWebSocket';

interface UseChatOptions {
  autoConnect?: boolean;
}

export function useChat(options?: UseChatOptions) {
  const autoConnect = options?.autoConnect !== false;

  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [activeConversationId, setActiveConversationId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [wsError, setWsError] = useState<string | null>(null);

  const activeConversationIdRef = useRef<number | null>(null);
  activeConversationIdRef.current = activeConversationId;

  const appendMessage = useCallback((incomingMessage: ChatMessage) => {
    setMessages((prev) => {
      if (prev.some((m) => m.id === incomingMessage.id)) {
        return prev;
      }
      return [...prev, incomingMessage];
    });
  }, []);

  const onMessage = useCallback(
    (incomingMessage: ChatMessage) => {
      const openId = activeConversationIdRef.current;

      setConversations((prev) => {
        const open = openId != null ? prev.find((c) => c.id === openId) : undefined;
        const belongsToOpen =
          open != null &&
          (incomingMessage.sender?.id === open.otherUser?.id ||
            incomingMessage.receiver?.id === open.otherUser?.id);

        if (belongsToOpen && openId != null) {
          queueMicrotask(() => {
            appendMessage(incomingMessage);
            markConversationAsRead(openId).catch(() => undefined);
          });
          return prev.map((c) => (c.id === openId ? { ...c, unreadCount: 0 } : c));
        }
        return prev;
      });

      getConversations()
        .then((data) => {
          const list = data ?? [];
          if (openId != null) {
            setConversations(list.map((c) => (c.id === openId ? { ...c, unreadCount: 0 } : c)));
          } else {
            setConversations(list);
          }
        })
        .catch(() => undefined);
    },
    [appendMessage]
  );

  const onError = useCallback((errorMsg: { error: string }) => {
    setWsError(errorMsg.error);
  }, []);

  const ws = useWebSocket({
    onMessage,
    onError,
  });

  const loadConversations = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getConversations();
      setConversations(data ?? []);
    } catch (err: any) {
      const errorMsg = err?.message ?? err?.data?.message ?? 'Failed to load conversations';
      setError(errorMsg);
      setConversations([]);
      console.error(errorMsg, err);
    } finally {
      setLoading(false);
    }
  }, []);

  const loadChatHistory = useCallback(async (conversationId: number, page = 0, size = 50) => {
    setLoading(true);
    setError(null);
    setActiveConversationId(conversationId);
    // Clear badge immediately in the UI.
    setConversations((prev) =>
      prev.map((c) => (c.id === conversationId ? { ...c, unreadCount: 0 } : c))
    );
    try {
      const response = await getChatHistory(conversationId, page, size);
      const items = response?.content ?? [];
      setMessages([...items].reverse());
      // Backend also marks as read on history load; keep an explicit call for older servers.
      await markConversationAsRead(conversationId).catch(() => undefined);
      setConversations((prev) =>
        prev.map((c) => (c.id === conversationId ? { ...c, unreadCount: 0 } : c))
      );
    } catch (err: any) {
      const errorMsg = err?.message ?? err?.data?.message ?? 'Failed to load chat history';
      setError(errorMsg);
      setMessages([]);
      console.error(errorMsg, err);
    } finally {
      setLoading(false);
    }
  }, []);

  const sendMessage = useCallback(
    async (receiverId: string, content: string): Promise<boolean> => {
      const trimmed = content.trim();
      if (!trimmed || !receiverId) return false;

      setSending(true);
      setError(null);
      try {
        // REST is the source of truth — saves to DB and returns the message
        const saved = await sendMessageApi({ receiverId, content: trimmed });
        appendMessage(saved);
        const refreshed = await getConversations();
        setConversations(refreshed ?? []);
        return true;
      } catch (err: any) {
        const errorMsg = err?.message ?? err?.data?.message ?? 'Failed to send message';
        setError(errorMsg);
        console.error(errorMsg, err);
        return false;
      } finally {
        setSending(false);
      }
    },
    [appendMessage]
  );

  const markAsRead = useCallback(async (messageId: number) => {
    try {
      await markMessageAsRead(messageId);
      setMessages((prev) =>
        prev.map((m) => (m.id === messageId ? { ...m, read: true } : m))
      );
    } catch (err: any) {
      console.error('Failed to mark message as read:', err);
    }
  }, []);

  const startConversation = useCallback(
    async (conversationId: number) => {
      await loadChatHistory(conversationId);
    },
    [loadChatHistory]
  );

  const clearChat = useCallback(() => {
    setMessages([]);
    setActiveConversationId(null);
  }, []);

  const didLoadRef = useRef(false);
  useEffect(() => {
    if (autoConnect && !didLoadRef.current) {
      didLoadRef.current = true;
      loadConversations();
    }
  }, [autoConnect, loadConversations]);

  return {
    conversations,
    messages,
    activeConversationId,
    loading,
    sending,
    error,
    wsConnected: ws.connected,
    wsError,

    loadConversations,
    loadChatHistory,
    sendMessage,
    markAsRead,
    startConversation,
    clearChat,
  };
}
