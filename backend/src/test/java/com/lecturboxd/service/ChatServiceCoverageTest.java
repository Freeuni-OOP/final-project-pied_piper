package com.lecturboxd.service;

import com.lecturboxd.dto.request.ChatMessageRequest;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceCoverageTest {

    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private ConversationRepository conversationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void getOrCreateAndSendMissingUsers() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(userRepository.findById(a)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chatService.getOrCreateConversation(a, b));

        when(userRepository.findById(a)).thenReturn(Optional.of(user(a)));
        when(userRepository.findById(b)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chatService.getOrCreateConversation(a, b));

        ChatMessageRequest request = new ChatMessageRequest();
        request.setReceiverId(b);
        request.setContent("hi");
        when(userRepository.findById(a)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chatService.sendMessage(a, request));

        when(userRepository.findById(a)).thenReturn(Optional.of(user(a)));
        when(userRepository.findById(b)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chatService.sendMessage(a, request));
    }

    @Test
    void getChatHistoryAllowsUser2AndRejectsMissing() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        Conversation conversation = conversation(1L, user(user1), user(user2));

        when(conversationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> chatService.getChatHistory(user1, 1L, Pageable.unpaged()));

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(chatMessageRepository.findByConversationIdOrderBySentAtDesc(1L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of()));
        assertEquals(0, chatService.getChatHistory(user2, 1L, Pageable.unpaged()).getTotalElements());
        verify(chatMessageRepository).markConversationAsRead(1L, user2);
    }

    @Test
    void markMessagesAsReadPaths() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        UUID stranger = UUID.randomUUID();
        Conversation conversation = conversation(1L, user(user1), user(user2));

        when(conversationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chatService.markMessagesAsRead(user1, 1L));

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        assertThrows(ResourceNotFoundException.class, () -> chatService.markMessagesAsRead(stranger, 1L));

        chatService.markMessagesAsRead(user1, 1L);
        verify(chatMessageRepository).markConversationAsRead(1L, user1);
    }

    @Test
    void markMessageAsReadMissing() {
        when(chatMessageRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> chatService.markMessageAsRead(UUID.randomUUID(), 9L));
    }

    @Test
    void getUserConversationsMissingUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> chatService.getUserConversations(userId));
    }

    @Test
    void startConversationReturnsExisting() {
        UUID me = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        User meUser = user(me);
        User otherUser = user(other);
        Conversation conversation = conversation(3L, meUser, otherUser);

        when(userRepository.findById(me)).thenReturn(Optional.of(meUser));
        when(userRepository.findById(other)).thenReturn(Optional.of(otherUser));
        when(conversationRepository.findBetweenUsers(me, other)).thenReturn(Optional.of(conversation));
        when(chatMessageRepository.countByConversationIdAndReceiverIdAndReadFalse(3L, me)).thenReturn(2L);

        ConversationResponse response = chatService.startConversation(me, other);
        assertEquals(3L, response.getId());
        assertEquals(2L, response.getUnreadCount());
        assertEquals(other, response.getOtherUser().getId());
    }

    @Test
    void getConversationByIdAndBetweenUsers() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        Conversation conversation = conversation(4L, user(a), user(b));

        when(conversationRepository.findById(4L)).thenReturn(Optional.of(conversation));
        assertEquals(4L, chatService.getConversationById(4L).getId());

        when(conversationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chatService.getConversationById(99L));

        when(conversationRepository.findBetweenUsers(a, b)).thenReturn(Optional.of(conversation));
        assertEquals(4L, chatService.getConversationBetweenUsers(a, b).getId());

        when(conversationRepository.findBetweenUsers(a, b)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chatService.getConversationBetweenUsers(a, b));
    }

    private static User user(UUID id) {
        User user = new User();
        user.setId(id);
        user.setName("U" + id.toString().substring(0, 4));
        user.setEmail(id + "@freeuni.edu.ge");
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
