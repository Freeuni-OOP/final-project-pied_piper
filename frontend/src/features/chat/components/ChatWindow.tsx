import React, { useEffect, useRef, CSSProperties } from 'react';
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
  peerName?: string;
}

function formatBubbleTime(iso: string) {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '';
  return d.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' });
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '?';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

const primaryButtonStyle: CSSProperties = {
  padding: '0.75rem 1.1rem',
  background: '#2563eb',
  color: '#fff',
  border: 'none',
  borderRadius: 8,
  cursor: 'pointer',
  fontWeight: 600,
};

export default function ChatWindow({
  messages,
  loading,
  sending = false,
  onSendMessage,
  onMarkAsRead,
  canSend = true,
  peerName = '',
}: ChatWindowProps) {
  const auth = useAuth();
  const [content, setContent] = React.useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const markedRef = useRef<Set<number>>(new Set());
  const inputRef = useRef<HTMLTextAreaElement>(null);

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

  const send = async () => {
    if (!content.trim() || !canSend || sending) return;
    const text = content;
    setContent('');
    await onSendMessage(text);
    inputRef.current?.focus();
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', minHeight: 0, background: '#f8fafc' }}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '16px 16px 8px' }}>
        {loading && <p style={{ textAlign: 'center', color: '#6b7280', fontSize: 14 }}>Loading messages…</p>}

        {!loading && messages.length === 0 && (
          <div style={{ textAlign: 'center', padding: '48px 16px', color: '#6b7280' }}>
            <div
              style={{
                width: 56,
                height: 56,
                borderRadius: '50%',
                background: '#2563eb',
                color: '#fff',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 700,
                marginBottom: 12,
              }}
            >
              {initials(peerName || '?')}
            </div>
            <p style={{ margin: 0, color: '#111827', fontWeight: 700 }}>{peerName || 'No messages yet'}</p>
            <p style={{ margin: '8px 0 0', fontSize: 14 }}>Say hello and start the conversation.</p>
          </div>
        )}

        {messages.map((msg, index) => {
          const mine = msg.sender.id === auth.userId;
          const prev = messages[index - 1];
          const stacked = prev && prev.sender.id === msg.sender.id;

          return (
            <div
              key={msg.id}
              style={{
                display: 'flex',
                justifyContent: mine ? 'flex-end' : 'flex-start',
                marginTop: stacked ? 4 : 12,
              }}
            >
              <div
                style={{
                  maxWidth: '75%',
                  padding: '10px 14px',
                  borderRadius: 12,
                  background: mine ? '#2563eb' : '#fff',
                  color: mine ? '#fff' : '#111827',
                  border: mine ? 'none' : '1px solid #e5e7eb',
                }}
              >
                <p style={{ margin: 0, whiteSpace: 'pre-wrap', wordBreak: 'break-word', fontSize: 15 }}>
                  {msg.content}
                </p>
                <p
                  style={{
                    margin: '6px 0 0',
                    textAlign: 'right',
                    fontSize: 11,
                    opacity: mine ? 0.85 : 1,
                    color: mine ? '#fff' : '#6b7280',
                  }}
                >
                  {formatBubbleTime(msg.sentAt)}
                  {mine && msg.read ? ' · Seen' : ''}
                </p>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          void send();
        }}
        style={{
          display: 'flex',
          gap: 8,
          alignItems: 'flex-end',
          padding: 12,
          borderTop: '1px solid #e5e7eb',
          background: '#fff',
        }}
      >
        <textarea
          ref={inputRef}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault();
              void send();
            }
          }}
          rows={1}
          placeholder={canSend ? 'Type a message…' : 'Select a chat to send'}
          disabled={sending || !canSend}
          style={{
            flex: 1,
            minHeight: 42,
            maxHeight: 120,
            resize: 'none',
            padding: '10px 12px',
            borderRadius: 8,
            border: '1px solid #e5e7eb',
            background: '#f8fafc',
            color: '#111827',
            fontSize: 15,
            outline: 'none',
            font: 'inherit',
          }}
        />
        <button
          type="submit"
          disabled={sending || !canSend || !content.trim()}
          style={{
            ...primaryButtonStyle,
            opacity: sending || !canSend || !content.trim() ? 0.5 : 1,
            cursor: sending || !canSend || !content.trim() ? 'not-allowed' : 'pointer',
          }}
        >
          {sending ? '…' : 'Send'}
        </button>
      </form>
    </div>
  );
}
