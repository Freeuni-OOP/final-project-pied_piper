package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.dto.response.ConversationResponse;
import com.lecturboxd.entity.ChatMessage;
import com.lecturboxd.entity.Conversation;

/**
 * EN: Maps chat entities (messages and conversations) to API response DTOs.
 * KA: ჩატის ერთეულებს (შეტყობინებები და საუბრები) API პასუხის DTO-ებად გარდაქმნის.
 */
public final class ChatMapper {

    private ChatMapper() {
    }

    /**
     * EN: Converts a ChatMessage entity into a ChatMessageResponse including sender and receiver summaries.
     * KA: ChatMessage ერთეულს ChatMessageResponse-ად გარდაქმნის გამგზავნისა და მიმღების შეჯამებით.
     */
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

    /**
     * EN: Converts a Conversation into a ConversationResponse for the current user, including the other participant and unread count.
     * KA: Conversation-ს ConversationResponse-ად გარდაქმნის მიმდინარე მომხმარებლისთვის, მეორე მონაწილისა და წაუკითხავი რაოდენობის ჩათვლით.
     */
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
