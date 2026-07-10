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
  peerName?: string;
}

function formatBubbleTime(iso: string) {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '';
  return d.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' });
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
  const [photoHint, setPhotoHint] = React.useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const markedRef = useRef<Set<number>>(new Set());
  const fileInputRef = useRef<HTMLInputElement>(null);

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
    <div className="flex h-full min-h-0 flex-col bg-[#FAFAFA]">
      <div className="flex-1 space-y-1 overflow-y-auto px-4 py-4 sm:px-6">
        {loading && (
          <p className="py-8 text-center text-sm text-[#8a8a8a]">Loading messages…</p>
        )}
        {!loading && messages.length === 0 && (
          <div className="flex h-full min-h-[200px] flex-col items-center justify-center gap-2 px-6 text-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-[#FFF3C4] text-2xl font-bold text-[#1a1a1a]">
              ✦
            </div>
            <p className="text-[15px] font-semibold text-[#2d2d2d]">No messages yet</p>
            <p className="text-sm text-[#8a8a8a]">Say hello and start the conversation.</p>
          </div>
        )}

        {messages.map((msg, index) => {
          const mine = msg.sender.id === auth.userId;
          const prev = messages[index - 1];
          const stacked = prev && prev.sender.id === msg.sender.id;

          return (
            <div
              key={msg.id}
              className={`flex ${mine ? 'justify-end' : 'justify-start'} ${stacked ? 'mt-0.5' : 'mt-3'}`}
            >
              <div
                className={`max-w-[78%] px-3.5 py-2 shadow-sm sm:max-w-[65%] ${
                  mine
                    ? 'rounded-[18px] rounded-br-md bg-[#F5B800] text-[#1a1a1a]'
                    : 'rounded-[18px] rounded-bl-md bg-white text-[#1a1a1a] ring-1 ring-[#e8e8e8]'
                }`}
              >
                <p className="whitespace-pre-wrap break-words text-[15px] leading-snug">
                  {msg.content}
                </p>
                <p
                  className={`mt-1 text-right text-[10px] ${
                    mine ? 'text-[#5c4a00]' : 'text-[#8a8a8a]'
                  }`}
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

      {!wsConnected && (
        <div className="border-t border-[#e8e8e8] bg-[#FFF8E1] px-4 py-2 text-center text-xs text-[#5c4a00]">
          Live updates offline — messages still save; reopen the chat to sync.
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="flex items-end gap-2 border-t border-[#e8e8e8] bg-white px-3 py-3 sm:px-4"
      >
        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          className="hidden"
          onChange={() => {
            if (fileInputRef.current) fileInputRef.current.value = '';
            setPhotoHint(true);
            window.setTimeout(() => setPhotoHint(false), 2500);
          }}
        />
        <button
          type="button"
          title="Add a photo"
          onClick={() => fileInputRef.current?.click()}
          className="mb-0.5 flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-[#558B2F] transition-colors hover:bg-[#E8F5E9] cursor-pointer"
          aria-label="Add a photo"
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" aria-hidden>
            <path
              d="M4 7.5A2.5 2.5 0 0 1 6.5 5h2.1l.7-1.4A1.5 1.5 0 0 1 10.6 3h2.8a1.5 1.5 0 0 1 1.3.7L15.4 5h2.1A2.5 2.5 0 0 1 20 7.5v9A2.5 2.5 0 0 1 17.5 19h-11A2.5 2.5 0 0 1 4 16.5v-9Z"
              stroke="currentColor"
              strokeWidth="1.8"
            />
            <circle cx="12" cy="12.5" r="3.2" stroke="currentColor" strokeWidth="1.8" />
          </svg>
        </button>

        <div className="flex min-w-0 flex-1 items-end rounded-[22px] bg-[#FAFAFA] ring-1 ring-[#e0e0e0] focus-within:ring-2 focus-within:ring-[#7CB342]">
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                if (!content.trim() || !canSend || sending) return;
                const text = content;
                setContent('');
                void onSendMessage(text);
              }
            }}
            rows={1}
            placeholder={canSend ? 'Aa' : 'Open a conversation to send'}
            disabled={sending || !canSend}
            className="max-h-28 min-h-[40px] w-full resize-none bg-transparent px-4 py-2.5 text-[15px] text-[#1a1a1a] outline-none placeholder:text-[#9a9a9a] disabled:opacity-50"
          />
        </div>

        <button
          type="submit"
          disabled={sending || !canSend || !content.trim()}
          title="Send"
          aria-label="Send message"
          className="mb-0.5 flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-[#F5B800] text-[#1a1a1a] transition-opacity hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-40 cursor-pointer"
        >
          {sending ? (
            <span className="text-xs font-bold">…</span>
          ) : (
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" aria-hidden>
              <path d="M3.4 20.6 21 12 3.4 3.4l.1 6.8L15 12 3.5 13.8l-.1 6.8Z" />
            </svg>
          )}
        </button>
      </form>
      {photoHint && (
        <p className="bg-[#E8F5E9] px-4 py-1.5 text-center text-xs text-[#33691E]">
          Photo sharing isn’t available yet — text messages work as usual.
        </p>
      )}
    </div>
  );
}
