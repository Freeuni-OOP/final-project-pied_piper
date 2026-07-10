// Sidebar list of chat conversations with preview of the latest message.

import { Conversation } from '../../../types/chat';

interface ConversationListProps {
  conversations: Conversation[];
  activeConversationId: number | null;
  onSelectConversation: (conversationId: number) => void;
  loading: boolean;
}

export default function ConversationList({
  conversations,
  activeConversationId,
  onSelectConversation,
  loading,
}: ConversationListProps) {
  if (loading) {
    return (
      <div className="p-4 text-gray-500 text-sm">
        Loading conversations…
      </div>
    );
  }

  if (conversations.length === 0) {
    return (
      <div className="p-4 text-gray-500 text-center text-sm">
        No conversations yet
      </div>
    );
  }

  return (
    <ul className="space-y-2 p-2">
      {conversations.map((conv) => (
        <li key={conv.id}>
          <button
            onClick={() => onSelectConversation(conv.id)}
            className={`w-full text-left px-4 py-3 rounded-lg border transition-colors ${
              activeConversationId === conv.id
                ? 'bg-blue-100 border-blue-300'
                : 'bg-white border-gray-200 hover:bg-gray-50'
            }`}
          >
            <div className="flex justify-between items-start gap-2">
              <div className="flex-1 min-w-0">
                <p className="font-medium text-gray-900 truncate">
                  {conv.otherUser.name}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  {new Date(conv.updatedAt).toLocaleDateString()}
                </p>
              </div>
              {conv.unreadCount > 0 && (
                <span className="inline-flex items-center justify-center w-6 h-6 text-xs font-bold text-white bg-red-500 rounded-full flex-shrink-0">
                  {conv.unreadCount}
                </span>
              )}
            </div>
          </button>
        </li>
      ))}
    </ul>
  );
}

