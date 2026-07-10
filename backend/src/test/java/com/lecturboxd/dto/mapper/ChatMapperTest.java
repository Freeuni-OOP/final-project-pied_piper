package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.dto.response.ConversationResponse;
import com.lecturboxd.entity.ChatMessage;
import com.lecturboxd.entity.Conversation;
import com.lecturboxd.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatMapperTest {

    @Test
    void toMessageResponseMapsFields() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        User sender = user(senderId, "Alice", "a@freeuni.edu.ge");
        User receiver = user(receiverId, "Bob", "b@freeuni.edu.ge");

        ChatMessage message = new ChatMessage();
        message.setId(10L);
        message.setContent("hi");
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setSentAt(LocalDateTime.of(2026, 1, 1, 12, 0));
        message.setRead(true);

        ChatMessageResponse response = ChatMapper.toMessageResponse(message);

        assertEquals(10L, response.getId());
        assertEquals("hi", response.getContent());
        assertEquals(senderId, response.getSender().getId());
        assertEquals("Alice", response.getSender().getName());
        assertEquals("a@freeuni.edu.ge", response.getSender().getEmail());
        assertEquals(receiverId, response.getReceiver().getId());
        assertEquals(true, response.isRead());
    }

    @Test
    void toConversationResponseUsesOtherUserAsUser1() {
        UUID me = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        Conversation conversation = new Conversation();
        conversation.setId(5L);
        conversation.setUser1(user(me, "Me", "me@freeuni.edu.ge"));
        conversation.setUser2(user(other, "Other", "o@freeuni.edu.ge"));
        conversation.setUpdatedAt(LocalDateTime.of(2026, 2, 2, 10, 0));

        ConversationResponse response = ChatMapper.toConversationResponse(conversation, me, 3L);

        assertEquals(5L, response.getId());
        assertEquals(other, response.getOtherUser().getId());
        assertEquals("Other", response.getOtherUser().getName());
        assertEquals(3L, response.getUnreadCount());
    }

    @Test
    void toConversationResponseUsesOtherUserAsUser2() {
        UUID me = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        Conversation conversation = new Conversation();
        conversation.setId(6L);
        conversation.setUser1(user(other, "Other", "o@freeuni.edu.ge"));
        conversation.setUser2(user(me, "Me", "me@freeuni.edu.ge"));
        conversation.setUpdatedAt(LocalDateTime.now());

        ConversationResponse response = ChatMapper.toConversationResponse(conversation, me, 0L);

        assertEquals(other, response.getOtherUser().getId());
        assertEquals(0L, response.getUnreadCount());
    }

    private static User user(UUID id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
