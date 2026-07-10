// TypeScript interfaces and types for chat messages, conversations, and WebSocket payloads.

export interface UserSummary {
  id: string;
  name: string;
}

export interface ChatMessage {
  id: number;
  content: string;
  sender: UserSummary;
  receiver: UserSummary;
  sentAt: string;
  read: boolean;
}

export interface Conversation {
  id: number;
  otherUser: UserSummary;
  updatedAt: string;
  unreadCount: number;
}

export interface SendMessagePayload {
  receiverId: string;
  content: string;
}

export interface ChatError {
  error: string;
}


