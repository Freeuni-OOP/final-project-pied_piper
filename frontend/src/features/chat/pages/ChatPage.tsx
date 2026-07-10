import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useChat } from '../hooks/useChat';
import ChatWindow from '../components/ChatWindow';
import ConversationList from '../components/ConversationList';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

export default function ChatPage() {
  const auth = useAuth();
  const chat = useChat();
  const [searchParams] = useSearchParams();
  const conversationIdParam = searchParams.get('conversationId');

  useEffect(() => {
    if (!conversationIdParam) return;
    const id = Number(conversationIdParam);
    if (!Number.isFinite(id) || id <= 0) return;

    const open = async () => {
      await chat.loadConversations();
      await chat.loadChatHistory(id);
    };
    open();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [conversationIdParam]);

  const activeConversation = chat.conversations.find(
    (c) => c.id === chat.activeConversationId
  );

  const otherUserId =
    activeConversation?.otherUser?.id ??
    (chat.messages[0]
      ? chat.messages[0].sender.id === auth.userId
        ? chat.messages[0].receiver.id
        : chat.messages[0].sender.id
      : undefined);

  const handleSelectConversation = async (conversationId: number) => {
    await chat.loadChatHistory(conversationId);
  };

  const handleSendMessage = async (content: string) => {
    if (!otherUserId) {
      return;
    }
    await chat.sendMessage(otherUserId, content);
  };

  return (
    <div>
      <div style={{ padding: '0 1rem' }}>
        <BackButton to="/" />
      </div>
      <div className="flex h-[70vh] bg-gray-100 rounded-lg overflow-hidden border border-gray-200">
        <div className="w-80 bg-white border-r border-gray-200 overflow-y-auto">
          <div className="p-4 border-b border-gray-200">
            <h2 className="text-xl font-bold text-gray-900">Messages</h2>
          </div>
          <ConversationList
            conversations={chat.conversations}
            activeConversationId={chat.activeConversationId}
            onSelectConversation={handleSelectConversation}
            loading={chat.loading && chat.conversations.length === 0 && !chat.activeConversationId}
          />
        </div>

        <div className="flex-1 flex flex-col">
          {chat.activeConversationId ? (
            <>
              <div className="px-6 py-4 bg-white border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">
                  {activeConversation?.otherUser?.name ?? 'Conversation'}
                </h3>
              </div>

              <div className="flex-1 overflow-hidden p-6">
                <ChatWindow
                  messages={chat.messages}
                  loading={chat.loading}
                  sending={chat.sending}
                  onSendMessage={handleSendMessage}
                  onMarkAsRead={chat.markAsRead}
                  canSend={!!otherUserId}
                  wsConnected={chat.wsConnected}
                />
              </div>
            </>
          ) : (
            <div className="flex-1 flex items-center justify-center">
              <p className="text-gray-500 text-lg">
                {chat.loading
                  ? 'Loading conversations…'
                  : chat.conversations.length === 0
                    ? 'No conversations yet. Message someone from their profile.'
                    : 'Select a conversation to start'}
              </p>
            </div>
          )}

          {chat.wsError && (
            <div className="px-6 py-3 bg-yellow-50 border-t border-yellow-200 text-yellow-700 text-sm">
              Live updates unavailable: {chat.wsError} (messages still save via API)
            </div>
          )}

          {chat.error && (
            <div className="px-6 py-3 bg-yellow-50 border-t border-yellow-200 text-yellow-700 text-sm">
              {chat.error}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
