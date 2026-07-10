package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.dto.response.ConversationResponse;
import com.lecturboxd.entity.ChatMessage;
import com.lecturboxd.entity.Conversation;

public final class ChatMapper {

    private ChatMapper() {
    }

    public static ChatMessageResponse toMessageResponse(ChatMessage message) {
        ChatMessageResponse.UserSummary sender = new ChatMessageResponse.UserSummary(
                message.getSender().getId(),
                message.getSender().getName(),
                message.getSender().getEmail()
        );
        ChatMessageResponse.UserSummary receiver = new ChatMessageResponse.UserSummary(
                message.getReceiver().getId(),
                message.getReceiver().getName(),
                message.getReceiver().getEmail()
        );

        return new ChatMessageResponse(
                message.getId(),
                message.getContent(),
                sender,
                receiver,
                message.getSentAt(),
                message.isRead()
        );
    }

    public static ConversationResponse toConversationResponse(Conversation conversation, java.util.UUID currentUserId, Long unreadCount) {
        var otherUser = conversation.getOtherUser(currentUserId);

        ConversationResponse.UserSummary userSummary = new ConversationResponse.UserSummary(
                otherUser.getId(),
                otherUser.getName()
        );

        return new ConversationResponse(
                conversation.getId(),
                userSummary,
                conversation.getUpdatedAt(),
                unreadCount
        );
    }
}
