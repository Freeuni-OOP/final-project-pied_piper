// Main chat window layout combining message list, input, and conversation header.

import React, { useEffect, useRef } from 'react';
import { ChatMessage } from '../../../types/chat';
import useAuth from '../../../auth/useAuth';

interface ChatWindowProps {
  messages: ChatMessage[];
  loading: boolean;
  onSendMessage: (content: string) => void;
  onMarkAsRead?: (messageId: number) => void;
  wsConnected: boolean;
}

export default function ChatWindow({
  messages,
  loading,
  onSendMessage,
  onMarkAsRead,
  wsConnected,
}: ChatWindowProps) {
  const auth = useAuth();
  const [content, setContent] = React.useState('');
  const [submitting, setSubmitting] = React.useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Mark unread messages as read when viewing
  useEffect(() => {
    messages.forEach((msg) => {
      if (!msg.read && msg.receiver.id === auth.userId) {
        onMarkAsRead?.(msg.id);
      }
    });
  }, [messages, auth.userId, onMarkAsRead]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim() || !wsConnected) return;

    setSubmitting(true);
    try {
      onSendMessage(content);
      setContent('');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex flex-col h-full bg-white rounded-lg border border-gray-200">
      {/* Messages area */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {loading && <p className="text-center text-gray-500">Loading messages…</p>}
        {!loading && messages.length === 0 && (
          <p className="text-center text-gray-500">No messages yet. Start the conversation!</p>
        )}
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex ${msg.sender.id === auth.userId ? 'justify-end' : 'justify-start'}`}
          >
            <div
              className={`max-w-xs px-4 py-2 rounded-lg ${
                msg.sender.id === auth.userId
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-200 text-gray-900'
              }`}
            >
              <p className="text-sm">{msg.content}</p>
              <p className="text-xs opacity-70 mt-1">
                {new Date(msg.sentAt).toLocaleTimeString()}
                {msg.read && ' ✓'}
              </p>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input area */}
      <form
        onSubmit={handleSubmit}
        className="border-t border-gray-200 p-4 flex gap-2"
      >
        <input
          type="text"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Type a message…"
          disabled={submitting || !wsConnected}
          className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
        />
        <button
          type="submit"
          disabled={submitting || !wsConnected || !content.trim()}
          className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50 cursor-pointer"
        >
          {submitting ? 'Sending…' : 'Send'}
        </button>
      </form>

      {!wsConnected && (
        <div className="bg-yellow-50 border-t border-yellow-200 px-4 py-2 text-yellow-800 text-sm">
          Connecting to chat…
        </div>
      )}
    </div>
  );
}

