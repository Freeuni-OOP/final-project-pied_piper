// HTTP client functions for chat conversation history and REST-based chat operations.

import client from './axiosClient';
import { ChatMessage, Conversation, SendMessagePayload } from '../types/chat';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

/**
 * Get paginated chat history for a conversation
 */
export async function getChatHistory(
  conversationId: number,
  page = 0,
  size = 50
): Promise<PageResponse<ChatMessage>> {
  const res = await client.get(`/api/chat/conversations/${conversationId}/messages`, {
    params: { page, size },
  });
  return res.data;
}

/**
 * Get all conversations for the current user
 */
export async function getConversations(): Promise<Conversation[]> {
  const res = await client.get('/api/chat/conversations');
  return Array.isArray(res.data) ? res.data : [];
}

/**
 * Mark a single message as read
 */
export async function markMessageAsRead(messageId: number): Promise<void> {
  await client.put(`/api/chat/${messageId}/read`);
}

/**
 * Send a message to a user (REST endpoint for initial send)
 * The WebSocket will handle real-time delivery
 */
export async function sendMessage(payload: SendMessagePayload): Promise<ChatMessage> {
  const res = await client.post('/api/chat/send', payload);
  return res.data;
}

/**
 * Start or open a conversation with another user
 */
export async function startConversation(receiverId: string): Promise<Conversation> {
  const res = await client.post('/api/chat/conversations', { receiverId });
  return res.data;
}


