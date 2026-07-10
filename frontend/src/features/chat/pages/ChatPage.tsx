// Active chat page for real-time messaging within a selected conversation.

import { useEffect } from 'react';
import { useChat } from '../hooks/useChat';
import ChatWindow from '../components/ChatWindow';
import ConversationList from '../components/ConversationList';
import useAuth from '../../../auth/useAuth';

export default function ChatPage() {
  const auth = useAuth();
  const chat = useChat();

  useEffect(() => {
    if (!auth.userId) return;

    // Load conversations on mount
    chat.loadConversations();
  }, [auth.userId, chat]);

  const handleSelectConversation = async (conversationId: number) => {
    await chat.loadChatHistory(conversationId);
  };

  const handleSendMessage = (content: string) => {
    if (!chat.activeConversationId || !chat.messages.length) return;

    // Get the other user ID from the messages
    const otherUser = chat.messages[0].receiver.id === auth.userId
      ? chat.messages[0].sender.id
      : chat.messages[0].receiver.id;

    chat.sendMessage(otherUser, content);
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Conversations sidebar */}
      <div className="w-80 bg-white border-r border-gray-200 overflow-y-auto">
        <div className="p-4 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">Messages</h2>
        </div>
        <ConversationList
          conversations={chat.conversations}
          activeConversationId={chat.activeConversationId}
          onSelectConversation={handleSelectConversation}
          loading={chat.loading}
        />
      </div>

      {/* Chat window */}
      <div className="flex-1 flex flex-col">
        {chat.activeConversationId ? (
          <>
            {/* Header */}
            {chat.messages.length > 0 && (
              <div className="px-6 py-4 bg-white border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">
                  {chat.messages[0].sender.id === auth.userId
                    ? chat.messages[0].receiver.name
                    : chat.messages[0].sender.name}
                </h3>
              </div>
            )}

            {/* Chat window */}
            <div className="flex-1 overflow-hidden p-6">
              <ChatWindow
                messages={chat.messages}
                loading={chat.loading}
                onSendMessage={handleSendMessage}
                onMarkAsRead={chat.markAsRead}
                wsConnected={chat.wsConnected}
              />
            </div>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center">
            <p className="text-gray-500 text-lg">
              {chat.loading ? 'Loading conversations…' : 'Select a conversation to start'}
            </p>
          </div>
        )}

        {/* WebSocket error */}
        {chat.wsError && (
          <div className="px-6 py-3 bg-red-50 border-t border-red-200 text-red-700 text-sm">
            Connection error: {chat.wsError}
          </div>
        )}

        {/* Regular error */}
        {chat.error && (
          <div className="px-6 py-3 bg-yellow-50 border-t border-yellow-200 text-yellow-700 text-sm">
            {chat.error}
          </div>
        )}
      </div>
    </div>
  );
}

