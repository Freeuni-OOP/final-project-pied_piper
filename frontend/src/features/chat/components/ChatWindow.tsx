import React, { useEffect, useRef } from 'react';
import { ChatMessage } from '../../../types/chat';
import useAuth from '../../../auth/useAuth';

interface ChatWindowProps {
  messages: ChatMessage[];
  loading: boolean;
  sending?: boolean;
  onSendMessage: (content: string) => void | Promise<void>;
  onMarkAsRead?: (messageId: number) => void;
  canSend?: boolean;
  wsConnected?: boolean;
}

export default function ChatWindow({
  messages,
  loading,
  sending = false,
  onSendMessage,
  onMarkAsRead,
  canSend = true,
  wsConnected = false,
}: ChatWindowProps) {
  const auth = useAuth();
  const [content, setContent] = React.useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const markedRef = useRef<Set<number>>(new Set());

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    messages.forEach((msg) => {
      if (!msg.read && msg.receiver.id === auth.userId && !markedRef.current.has(msg.id)) {
        markedRef.current.add(msg.id);
        onMarkAsRead?.(msg.id);
      }
    });
  }, [messages, auth.userId, onMarkAsRead]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim() || !canSend || sending) return;

    const text = content;
    setContent('');
    await onSendMessage(text);
  };

  return (
    <div className="flex flex-col h-full bg-white rounded-lg border border-gray-200">
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

      <form onSubmit={handleSubmit} className="border-t border-gray-200 p-4 flex gap-2">
        <input
          type="text"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder={canSend ? 'Type a message…' : 'Open a conversation to send'}
          disabled={sending || !canSend}
          className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
        />
        <button
          type="submit"
          disabled={sending || !canSend || !content.trim()}
          className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50 cursor-pointer"
        >
          {sending ? 'Sending…' : 'Send'}
        </button>
      </form>

      {!wsConnected && (
        <div className="bg-gray-50 border-t border-gray-200 px-4 py-2 text-gray-600 text-sm">
          Live socket offline — messages still save and reload from the server.
        </div>
      )}
    </div>
  );
}
