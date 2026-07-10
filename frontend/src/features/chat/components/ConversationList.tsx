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
  const sameDay =
    d.getFullYear() === now.getFullYear() &&
    d.getMonth() === now.getMonth() &&
    d.getDate() === now.getDate();
  if (sameDay) {
    return d.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' });
  }
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  if (
    d.getFullYear() === yesterday.getFullYear() &&
    d.getMonth() === yesterday.getMonth() &&
    d.getDate() === yesterday.getDate()
  ) {
    return 'Yesterday';
  }
  return d.toLocaleDateString([], { month: 'short', day: 'numeric' });
}

export default function ConversationList({
  conversations,
  activeConversationId,
  onSelectConversation,
  loading,
}: ConversationListProps) {
  if (loading) {
    return (
      <div className="px-5 py-8 text-sm text-[#5c5c5c]">Loading conversations…</div>
    );
  }

  if (conversations.length === 0) {
    return (
      <div className="px-5 py-10 text-center text-sm text-[#5c5c5c]">
        No conversations yet.
        <p className="mt-2 text-xs text-[#8a8a8a]">
          Message someone from their profile to start chatting.
        </p>
      </div>
    );
  }

  return (
    <ul className="py-1">
      {conversations.map((conv) => {
        const active = activeConversationId === conv.id;
        const unread = conv.unreadCount > 0;

        return (
          <li key={conv.id}>
            <button
              type="button"
              onClick={() => onSelectConversation(conv.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 text-left transition-colors cursor-pointer ${
                active
                  ? 'bg-[#E8F5E9]'
                  : 'bg-transparent hover:bg-[#FFF8E1]'
              }`}
            >
              <div
                className={`relative flex h-12 w-12 shrink-0 items-center justify-center rounded-full text-sm font-bold ${
                  active
                    ? 'bg-[#7CB342] text-white'
                    : 'bg-[#F5B800] text-[#1a1a1a]'
                }`}
                aria-hidden
              >
                {initials(conv.otherUser.name)}
                {unread && (
                  <span className="absolute -right-0.5 -top-0.5 h-3.5 w-3.5 rounded-full border-2 border-white bg-[#558B2F]" />
                )}
              </div>

              <div className="min-w-0 flex-1">
                <div className="flex items-baseline justify-between gap-2">
                  <p
                    className={`truncate text-[15px] ${
                      unread ? 'font-bold text-[#1a1a1a]' : 'font-semibold text-[#2d2d2d]'
                    }`}
                  >
                    {conv.otherUser.name}
                  </p>
                  <span className="shrink-0 text-[11px] text-[#8a8a8a]">
                    {formatListTime(conv.updatedAt)}
                  </span>
                </div>
                <div className="mt-0.5 flex items-center justify-between gap-2">
                  <p
                    className={`truncate text-[13px] ${
                      unread ? 'font-medium text-[#3d3d3d]' : 'text-[#8a8a8a]'
                    }`}
                  >
                    {unread ? 'New message' : 'Tap to open chat'}
                  </p>
                  {unread && (
                    <span className="inline-flex h-5 min-w-5 shrink-0 items-center justify-center rounded-full bg-[#558B2F] px-1.5 text-[11px] font-bold text-white">
                      {conv.unreadCount > 99 ? '99+' : conv.unreadCount}
                    </span>
                  )}
                </div>
              </div>
            </button>
          </li>
        );
      })}
    </ul>
  );
}
