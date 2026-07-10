import { useEffect, useState, CSSProperties } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useChat } from '../hooks/useChat';
import ChatWindow from '../components/ChatWindow';
import ConversationList from '../components/ConversationList';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

function initials(name: string) {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '?';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

const panelStyle: CSSProperties = {
  background: '#fff',
  border: '1px solid #e5e7eb',
  borderRadius: 12,
  overflow: 'hidden',
};

export default function ChatPage() {
  const auth = useAuth();
  const chat = useChat();
  const [searchParams] = useSearchParams();
  const conversationIdParam = searchParams.get('conversationId');
  const [mobileShowThread, setMobileShowThread] = useState(false);

  useEffect(() => {
    if (!conversationIdParam) return;
    const id = Number(conversationIdParam);
    if (!Number.isFinite(id) || id <= 0) return;

    const open = async () => {
      await chat.loadConversations();
      await chat.loadChatHistory(id);
      setMobileShowThread(true);
    };
    open();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [conversationIdParam]);

  const activeConversation = chat.conversations.find(
    (c) => c.id === chat.activeConversationId
  );

  const peerName = activeConversation?.otherUser?.name ?? 'Conversation';

  const otherUserId =
    activeConversation?.otherUser?.id ??
    (chat.messages[0]
      ? chat.messages[0].sender.id === auth.userId
        ? chat.messages[0].receiver.id
        : chat.messages[0].sender.id
      : undefined);

  const handleSelectConversation = async (conversationId: number) => {
    await chat.loadChatHistory(conversationId);
    setMobileShowThread(true);
  };

  const handleSendMessage = async (content: string) => {
    if (!otherUserId) return;
    await chat.sendMessage(otherUserId, content);
  };

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto' }}>
      <BackButton />

      <div style={{ ...panelStyle, marginBottom: 16, padding: 24 }}>
        <h1 style={{ marginBottom: 8 }}>Chats</h1>
        <p style={{ color: '#6b7280', margin: 0 }}>
          Search people you’ve already messaged, then open a conversation.
        </p>
      </div>

      <div
        style={{
          ...panelStyle,
          display: 'flex',
          height: 'min(78vh, 720px)',
        }}
      >
        <aside
          style={{
            width: '100%',
            maxWidth: 360,
            borderRight: '1px solid #e5e7eb',
            display: mobileShowThread ? 'none' : 'flex',
            flexDirection: 'column',
            background: '#fff',
          }}
          className="chat-aside"
        >
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '16px 16px 8px',
              borderBottom: '1px solid #e5e7eb',
            }}
          >
            <strong style={{ fontSize: 18, color: '#111827' }}>Conversations</strong>
            <span
              style={{
                background: '#eff6ff',
                color: '#1d4ed8',
                borderRadius: 8,
                padding: '4px 10px',
                fontSize: 12,
                fontWeight: 700,
              }}
            >
              {chat.conversations.length}
            </span>
          </div>

          <div style={{ minHeight: 0, flex: 1 }}>
            <ConversationList
              conversations={chat.conversations}
              activeConversationId={chat.activeConversationId}
              onSelectConversation={handleSelectConversation}
              loading={chat.loading && chat.conversations.length === 0 && !chat.activeConversationId}
            />
          </div>
        </aside>

        <section
          style={{
            minWidth: 0,
            flex: 1,
            display: mobileShowThread ? 'flex' : undefined,
            flexDirection: 'column',
            background: '#f8fafc',
          }}
          className="chat-thread"
        >
          {chat.activeConversationId ? (
            <>
              <header
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 12,
                  padding: '12px 16px',
                  borderBottom: '1px solid #e5e7eb',
                  background: '#fff',
                }}
              >
                <button
                  type="button"
                  onClick={() => setMobileShowThread(false)}
                  style={{
                    display: 'none',
                    width: 36,
                    height: 36,
                    borderRadius: 8,
                    border: '1px solid #e5e7eb',
                    background: '#fff',
                    color: '#2563eb',
                    cursor: 'pointer',
                    fontWeight: 700,
                  }}
                  className="chat-mobile-back"
                  aria-label="Back to chats"
                >
                  ←
                </button>

                <div
                  style={{
                    width: 40,
                    height: 40,
                    borderRadius: '50%',
                    background: '#2563eb',
                    color: '#fff',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 700,
                    fontSize: 14,
                    flexShrink: 0,
                  }}
                >
                  {initials(peerName)}
                </div>

                <div style={{ minWidth: 0, flex: 1 }}>
                  <h3 style={{ margin: 0, fontSize: 16, color: '#111827' }}>{peerName}</h3>
                  <p style={{ margin: 0, fontSize: 12, color: '#6b7280' }}>
                    {chat.wsConnected ? 'Active now' : 'Messages saved'}
                  </p>
                </div>
              </header>

              <div style={{ minHeight: 0, flex: 1 }}>
                <ChatWindow
                  messages={chat.messages}
                  loading={chat.loading}
                  sending={chat.sending}
                  onSendMessage={handleSendMessage}
                  onMarkAsRead={chat.markAsRead}
                  canSend={!!otherUserId}
                  wsConnected={chat.wsConnected}
                  peerName={peerName}
                />
              </div>
            </>
          ) : (
            <div
              style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 12,
                padding: 32,
                textAlign: 'center',
              }}
            >
              <div
                style={{
                  width: 72,
                  height: 72,
                  borderRadius: '50%',
                  background: '#eff6ff',
                  color: '#2563eb',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: 28,
                  fontWeight: 700,
                }}
              >
                ✉
              </div>
              <p style={{ margin: 0, fontSize: 18, fontWeight: 700, color: '#111827' }}>
                Your messages
              </p>
              <p style={{ margin: 0, maxWidth: 320, color: '#6b7280', fontSize: 14 }}>
                {chat.loading
                  ? 'Loading conversations…'
                  : chat.conversations.length === 0
                    ? 'No conversations yet. Message someone from their profile to get started.'
                    : 'Select a person on the left to open the chat.'}
              </p>
            </div>
          )}

          {(chat.wsError || chat.error) && (
            <div
              style={{
                borderTop: '1px solid #e5e7eb',
                background: '#fffbeb',
                color: '#92400e',
                padding: '10px 16px',
                fontSize: 13,
              }}
            >
              {chat.wsError
                ? `Live updates unavailable: ${chat.wsError}`
                : chat.error}
            </div>
          )}
        </section>
      </div>

      <style>{`
        @media (min-width: 768px) {
          .chat-aside { display: flex !important; }
          .chat-thread { display: flex !important; }
          .chat-mobile-back { display: none !important; }
        }
        @media (max-width: 767px) {
          .chat-aside { max-width: 100% !important; }
          .chat-thread { display: ${mobileShowThread ? 'flex' : 'none'} !important; }
          .chat-mobile-back { display: inline-flex !important; align-items: center; justify-content: center; }
        }
      `}</style>
    </div>
  );
}
