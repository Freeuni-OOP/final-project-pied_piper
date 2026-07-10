import { useEffect, useState } from 'react';
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

  const handleBackToList = () => {
    setMobileShowThread(false);
  };

  return (
    <div className="mx-auto max-w-6xl px-3 pb-6 sm:px-4">
      <div className="mb-3">
        <BackButton to="/" />
      </div>

      <div className="flex h-[min(78vh,720px)] overflow-hidden rounded-2xl border border-[#e8e8e8] bg-white shadow-[0_8px_30px_rgba(0,0,0,0.06)]">
        {/* Conversation list */}
        <aside
          className={`w-full flex-col border-r border-[#e8e8e8] bg-white md:flex md:w-[340px] md:shrink-0 ${
            mobileShowThread ? 'hidden' : 'flex'
          }`}
        >
          <div className="flex items-center justify-between border-b border-[#e8e8e8] bg-gradient-to-r from-[#FFF8E1] to-white px-5 py-4">
            <div>
              <h2 className="text-xl font-bold tracking-tight text-[#1a1a1a]">Chats</h2>
              <p className="text-xs text-[#8a8a8a]">
                {chat.wsConnected ? (
                  <span className="inline-flex items-center gap-1.5">
                    <span className="h-2 w-2 rounded-full bg-[#7CB342]" />
                    Live
                  </span>
                ) : (
                  <span className="inline-flex items-center gap-1.5">
                    <span className="h-2 w-2 rounded-full bg-[#c4c4c4]" />
                    Connecting…
                  </span>
                )}
              </p>
            </div>
          </div>

          <div className="min-h-0 flex-1 overflow-y-auto">
            <ConversationList
              conversations={chat.conversations}
              activeConversationId={chat.activeConversationId}
              onSelectConversation={handleSelectConversation}
              loading={chat.loading && chat.conversations.length === 0 && !chat.activeConversationId}
            />
          </div>
        </aside>

        {/* Active thread */}
        <section
          className={`min-w-0 flex-1 flex-col bg-[#FAFAFA] ${
            mobileShowThread ? 'flex' : 'hidden md:flex'
          }`}
        >
          {chat.activeConversationId ? (
            <>
              <header className="flex items-center gap-3 border-b border-[#e8e8e8] bg-white px-3 py-3 sm:px-5">
                <button
                  type="button"
                  onClick={handleBackToList}
                  className="flex h-9 w-9 items-center justify-center rounded-full text-[#558B2F] hover:bg-[#E8F5E9] md:hidden cursor-pointer"
                  aria-label="Back to chats"
                >
                  ←
                </button>

                <div
                  className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-[#7CB342] text-sm font-bold text-white"
                  aria-hidden
                >
                  {initials(peerName)}
                </div>

                <div className="min-w-0 flex-1">
                  <h3 className="truncate text-[16px] font-bold text-[#1a1a1a]">{peerName}</h3>
                  <p className="text-xs text-[#558B2F]">
                    {chat.wsConnected ? 'Active now' : 'Saved messages'}
                  </p>
                </div>
              </header>

              <div className="min-h-0 flex-1">
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
            <div className="flex flex-1 flex-col items-center justify-center gap-3 bg-[#FAFAFA] px-8 text-center">
              <div className="flex h-20 w-20 items-center justify-center rounded-full bg-[#FFF3C4] text-3xl font-bold text-[#1a1a1a]">
                ✉
              </div>
              <p className="text-lg font-bold text-[#1a1a1a]">Your messages</p>
              <p className="max-w-sm text-sm text-[#8a8a8a]">
                {chat.loading
                  ? 'Loading conversations…'
                  : chat.conversations.length === 0
                    ? 'No conversations yet. Message someone from their profile to get started.'
                    : 'Select a chat from the left to start messaging.'}
              </p>
            </div>
          )}

          {(chat.wsError || chat.error) && (
            <div className="border-t border-[#e8e8e8] bg-[#FFF8E1] px-4 py-2 text-sm text-[#5c4a00]">
              {chat.wsError
                ? `Live updates unavailable: ${chat.wsError}`
                : chat.error}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
