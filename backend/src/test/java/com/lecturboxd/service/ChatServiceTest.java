package com.lecturboxd.service;

import com.lecturboxd.dto.request.ChatMessageRequest;
import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.dto.response.ConversationResponse;
import com.lecturboxd.entity.ChatMessage;
import com.lecturboxd.entity.Conversation;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.ChatMessageRepository;
import com.lecturboxd.repository.ConversationRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void getOrCreateConversationReturnsExisting() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        User userA = user(a, "A", "a@freeuni.edu.ge");
        User userB = user(b, "B", "b@freeuni.edu.ge");
        Conversation existing = conversation(1L, userA, userB);

        when(userRepository.findById(a)).thenReturn(Optional.of(userA));
        when(userRepository.findById(b)).thenReturn(Optional.of(userB));
        when(conversationRepository.findBetweenUsers(a, b)).thenReturn(Optional.of(existing));

        Conversation result = chatService.getOrCreateConversation(a, b);

        assertEquals(1L, result.getId());
        verify(conversationRepository, never()).save(any());
    }

    @Test
    void getOrCreateConversationCreatesWhenMissing() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        User userA = user(a, "A", "a@freeuni.edu.ge");
        User userB = user(b, "B", "b@freeuni.edu.ge");

        when(userRepository.findById(a)).thenReturn(Optional.of(userA));
        when(userRepository.findById(b)).thenReturn(Optional.of(userB));
        when(conversationRepository.findBetweenUsers(a, b)).thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(inv -> {
            Conversation c = inv.getArgument(0);
            c.setId(99L);
            return c;
        });

        Conversation result = chatService.getOrCreateConversation(a, b);

        assertEquals(99L, result.getId());
        assertEquals(userA, result.getUser1());
        assertEquals(userB, result.getUser2());
    }

    @Test
    void sendMessagePersistsUnreadMessage() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        User sender = user(senderId, "Sender", "s@freeuni.edu.ge");
        User receiver = user(receiverId, "Receiver", "r@freeuni.edu.ge");
        Conversation conversation = conversation(5L, sender, receiver);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(conversationRepository.findBetweenUsers(senderId, receiverId)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(conversation)).thenReturn(conversation);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(inv -> {
            ChatMessage m = inv.getArgument(0);
            m.setId(10L);
            return m;
        });

        ChatMessageRequest request = new ChatMessageRequest();
        request.setReceiverId(receiverId);
        request.setContent("hello");

        ChatMessageResponse response = chatService.sendMessage(senderId, request);

        assertEquals(10L, response.getId());
        assertEquals("hello", response.getContent());
        assertFalse(response.isRead());

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        assertEquals(sender, captor.getValue().getSender());
        assertEquals(receiver, captor.getValue().getReceiver());
        assertFalse(captor.getValue().isRead());
    }

    @Test
    void getChatHistoryMarksConversationAsRead() {
        UUID userId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        User me = user(userId, "Me", "me@freeuni.edu.ge");
        User other = user(otherId, "Other", "o@freeuni.edu.ge");
        Conversation conversation = conversation(7L, me, other);

        when(conversationRepository.findById(7L)).thenReturn(Optional.of(conversation));
        when(chatMessageRepository.findByConversationIdOrderBySentAtDesc(7L, PageRequest.of(0, 20)))
                .thenReturn(Page.empty());

        chatService.getChatHistory(userId, 7L, PageRequest.of(0, 20));

        verify(chatMessageRepository).markConversationAsRead(7L, userId);
    }

    @Test
    void getChatHistoryRejectsNonParticipant() {
        UUID userId = UUID.randomUUID();
        User u1 = user(UUID.randomUUID(), "A", "a@freeuni.edu.ge");
        User u2 = user(UUID.randomUUID(), "B", "b@freeuni.edu.ge");
        Conversation conversation = conversation(7L, u1, u2);

        when(conversationRepository.findById(7L)).thenReturn(Optional.of(conversation));

        assertThrows(ResourceNotFoundException.class,
                () -> chatService.getChatHistory(userId, 7L, PageRequest.of(0, 20)));
        verify(chatMessageRepository, never()).markConversationAsRead(any(), any());
    }

    @Test
    void startConversationRejectsSelf() {
        UUID me = UUID.randomUUID();
        assertThrows(ResourceNotFoundException.class,
                () -> chatService.startConversation(me, me));
    }

    @Test
    void getUserConversationsUsesPerConversationUnreadCount() {
        UUID me = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        User userMe = user(me, "Me", "me@freeuni.edu.ge");
        User other = user(otherId, "Other", "o@freeuni.edu.ge");
        Conversation conversation = conversation(3L, userMe, other);

        when(userRepository.existsById(me)).thenReturn(true);
        when(conversationRepository.findAllForUser(me)).thenReturn(List.of(conversation));
        when(chatMessageRepository.countByConversationIdAndReceiverIdAndReadFalse(3L, me)).thenReturn(2L);

        List<ConversationResponse> result = chatService.getUserConversations(me);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getUnreadCount());
        assertEquals(otherId, result.get(0).getOtherUser().getId());
    }

    @Test
    void markMessageAsReadRequiresReceiver() {
        UUID receiverId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        ChatMessage message = new ChatMessage();
        message.setId(1L);
        message.setReceiver(user(otherId, "Other", "o@freeuni.edu.ge"));

        when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertThrows(ResourceNotFoundException.class,
                () -> chatService.markMessageAsRead(receiverId, 1L));
        verify(chatMessageRepository, never()).save(any());
    }

    @Test
    void markMessageAsReadUpdatesFlag() {
        UUID receiverId = UUID.randomUUID();
        ChatMessage message = new ChatMessage();
        message.setId(1L);
        message.setRead(false);
        message.setReceiver(user(receiverId, "Me", "me@freeuni.edu.ge"));

        when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(chatMessageRepository.save(message)).thenReturn(message);

        chatService.markMessageAsRead(receiverId, 1L);

        assertTrue(message.isRead());
        verify(chatMessageRepository).save(message);
    }

    private static User user(UUID id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private static Conversation conversation(Long id, User u1, User u2) {
        Conversation conversation = new Conversation();
        conversation.setId(id);
        conversation.setUser1(u1);
        conversation.setUser2(u2);
        return conversation;
    }
}
