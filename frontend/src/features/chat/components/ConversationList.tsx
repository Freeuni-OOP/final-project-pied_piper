import { useMemo, useState, CSSProperties } from 'react';
import { Conversation } from '../../../types/chat';

interface ConversationListProps {
  conversations: Conversation[];
  activeConversationId: number | null;
  onSelectConversation: (conversationId: number) => void;
  loading: boolean;
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '?';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

function formatListTime(iso: string) {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '';
  const now = new Date();
  const diffMs = now.getTime() - d.getTime();
  const diffMin = Math.floor(diffMs / 60000);
  if (diffMin < 1) return 'now';
  if (diffMin < 60) return `${diffMin}m`;
  const diffHr = Math.floor(diffMin / 60);
  if (diffHr < 24) return `${diffHr}h`;
  const diffDay = Math.floor(diffHr / 24);
  if (diffDay < 7) return `${diffDay}d`;
  return d.toLocaleDateString([], { month: 'short', day: 'numeric' });
}

const rowStyle = (active: boolean): CSSProperties => ({
  width: '100%',
  display: 'flex',
  alignItems: 'center',
  gap: 12,
  padding: 12,
  textAlign: 'left',
  cursor: 'pointer',
  border: '1px solid #e5e7eb',
  borderRadius: 10,
  background: active ? '#eff6ff' : '#f8fafc',
  color: '#111827',
  font: 'inherit',
});

export default function ConversationList({
  conversations,
  activeConversationId,
  onSelectConversation,
  loading,
}: ConversationListProps) {
  const [query, setQuery] = useState('');

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return conversations;
    return conversations.filter((c) => (c.otherUser?.name ?? '').toLowerCase().includes(q));
  }, [conversations, query]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', minHeight: 0 }}>
      <div style={{ padding: 12 }}>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search chats"
          aria-label="Search chats"
          style={{
            width: '100%',
            padding: '10px 12px',
            borderRadius: 8,
            border: '1px solid #e5e7eb',
            background: '#fff',
            color: '#111827',
            fontSize: 14,
            outline: 'none',
            boxSizing: 'border-box',
          }}
        />
      </div>

      <div style={{ minHeight: 0, flex: 1, overflowY: 'auto', padding: '0 12px 12px' }}>
        {loading && <p style={{ color: '#6b7280', fontSize: 14 }}>Loading conversations…</p>}

        {!loading && conversations.length === 0 && (
          <p style={{ color: '#6b7280', fontSize: 14, textAlign: 'center', paddingTop: 24 }}>
            No conversations yet.
          </p>
        )}

        {!loading && conversations.length > 0 && filtered.length === 0 && (
          <p style={{ color: '#6b7280', fontSize: 14, textAlign: 'center', paddingTop: 24 }}>
            No chats match “{query.trim()}”.
          </p>
        )}

        <div style={{ display: 'grid', gap: 8 }}>
          {filtered.map((conv) => {
            const active = activeConversationId === conv.id;
            const unread = conv.unreadCount > 0;

            return (
              <button
                key={conv.id}
                type="button"
                onClick={() => onSelectConversation(conv.id)}
                style={rowStyle(active)}
              >
                <div
                  style={{
                    width: 44,
                    height: 44,
                    borderRadius: '50%',
                    background: active ? '#2563eb' : '#dbeafe',
                    color: active ? '#fff' : '#1d4ed8',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 700,
                    fontSize: 13,
                    flexShrink: 0,
                  }}
                >
                  {initials(conv.otherUser.name)}
                </div>

                <div style={{ minWidth: 0, flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8 }}>
                    <strong
                      style={{
                        fontSize: 14,
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap',
                      }}
                    >
                      {conv.otherUser.name}
                    </strong>
                    <span style={{ fontSize: 12, color: '#6b7280', flexShrink: 0 }}>
                      {formatListTime(conv.updatedAt)}
                    </span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8, marginTop: 2 }}>
                    <span style={{ fontSize: 13, color: '#6b7280' }}>
                      {unread ? 'New message' : 'Open conversation'}
                    </span>
                    {unread && (
                      <span
                        style={{
                          minWidth: 20,
                          height: 20,
                          borderRadius: 999,
                          background: '#2563eb',
                          color: '#fff',
                          fontSize: 11,
                          fontWeight: 700,
                          display: 'inline-flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          padding: '0 6px',
                        }}
                      >
                        {conv.unreadCount > 99 ? '99+' : conv.unreadCount}
                      </span>
                    )}
                  </div>
                </div>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
