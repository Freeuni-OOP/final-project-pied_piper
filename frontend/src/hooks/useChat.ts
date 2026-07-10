import { useState, useCallback, useEffect } from 'react';
import { ChatMessage, Conversation } from '../types/chat';
import { getChatHistory, getConversations, markMessageAsRead } from '../api/chatApi';
import { useWebSocket } from './useWebSocket';

interface UseChatOptions {
  autoConnect?: boolean;
}

/**
 * Combined hook for chat functionality:
 * - Manages REST API calls for history and conversations
 * - Integrates real-time WebSocket messages
 * - Tracks unread status
 *
 * Usage:
 * const chat = useChat();
 * await chat.loadConversations();
 * await chat.loadChatHistory(conversationId);
 * chat.sendMessage(receiverId, content);
 */
export function useChat(options?: UseChatOptions) {
  const autoConnect = options?.autoConnect !== false;

  // State for conversations and messages
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [activeConversationId, setActiveConversationId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [wsError, setWsError] = useState<string | null>(null);

  // WebSocket integration
  const ws = useWebSocket({
    onMessage: (incomingMessage) => {
      // Add incoming message to the list if it's for the active conversation
      setMessages((prev) => {
        // Check if message is already in the list to avoid duplicates
        if (prev.some((m) => m.id === incomingMessage.id)) {
          return prev;
        }
        return [...prev, incomingMessage];
      });
    },
    onError: (errorMsg) => {
      setWsError(errorMsg.error);
    },
  });

  /**
   * Load all conversations for the current user
   */
  const loadConversations = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getConversations();
      setConversations(data);
    } catch (err: any) {
      const errorMsg = err?.message ?? 'Failed to load conversations';
      setError(errorMsg);
      console.error(errorMsg, err);
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Load chat history for a specific conversation
   */
  const loadChatHistory = useCallback(
    async (conversationId: number, page = 0, size = 50) => {
      setLoading(true);
      setError(null);
      setActiveConversationId(conversationId);
      try {
        const response = await getChatHistory(conversationId, page, size);
        // Reverse to show oldest first
        setMessages(response.content.reverse());
      } catch (err: any) {
        const errorMsg = err?.message ?? 'Failed to load chat history';
        setError(errorMsg);
        console.error(errorMsg, err);
      } finally {
        setLoading(false);
      }
    },
    []
  );

  /**
   * Send a message via WebSocket
   */
  const sendMessage = useCallback(
    (receiverId: string, content: string): boolean => {
      if (!ws.connected) {
        setWsError('WebSocket not connected');
        return false;
      }

      const success = ws.sendMessage({ receiverId, content });
      if (!success) {
        setWsError('Failed to send message');
      }
      return success;
    },
    [ws]
  );

  /**
   * Mark a message as read
   */
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

  /**
   * Start a new conversation (by loading history with a user)
   */
  const startConversation = useCallback(async (conversationId: number) => {
    await loadChatHistory(conversationId);
  }, [loadChatHistory]);

  /**
   * Clear messages and active conversation
   */
  const clearChat = useCallback(() => {
    setMessages([]);
    setActiveConversationId(null);
  }, []);

  // Auto-connect WebSocket and load conversations on mount
  useEffect(() => {
    if (autoConnect) {
      loadConversations();
    }
  }, [autoConnect, loadConversations]);

  return {
    // State
    conversations,
    messages,
    activeConversationId,
    loading,
    error,
    wsConnected: ws.connected,
    wsError,

    // Actions
    loadConversations,
    loadChatHistory,
    sendMessage,
    markAsRead,
    startConversation,
    clearChat,
  };
}

