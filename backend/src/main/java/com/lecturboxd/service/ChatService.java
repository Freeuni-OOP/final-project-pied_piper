package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.ChatMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get or create a conversation between two users
     */
    @Transactional
    public Conversation getOrCreateConversation(UUID userId1, UUID userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId2));

        return conversationRepository.findBetweenUsers(userId1, userId2)
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setUser1(user1);
                    conversation.setUser2(user2);
                    return conversationRepository.save(conversation);
                });
    }

    /**
     * Send a message from sender to receiver
     */
    @Transactional
    public ChatMessageResponse sendMessage(UUID senderId, ChatMessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender user not found with id " + senderId));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found with id " + request.getReceiverId()));

        // Get or create conversation
        Conversation conversation = getOrCreateConversation(senderId, request.getReceiverId());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        // Create and save message
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMapper.toMessageResponse(savedMessage);
    }

    /**
     * Get paginated message history for a conversation
     */
    @Transactional
    public Page<ChatMessageResponse> getChatHistory(UUID userId, Long conversationId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id " + conversationId));

        // Verify user is part of this conversation
        if (!conversation.getUser1().getId().equals(userId) && !conversation.getUser2().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not part of this conversation");
        }

        // Opening the thread means the user has seen the messages.
        chatMessageRepository.markConversationAsRead(conversationId, userId);

        return chatMessageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable)
                .map(ChatMapper::toMessageResponse);
    }

    /**
     * Mark messages as read
     */
    @Transactional
    public void markMessagesAsRead(UUID userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id " + conversationId));

        // Verify user is part of this conversation
        if (!conversation.getUser1().getId().equals(userId) && !conversation.getUser2().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not part of this conversation");
        }

        chatMessageRepository.markConversationAsRead(conversationId, userId);
    }

    /**
     * Mark a single message as read
     */
    @Transactional
    public void markMessageAsRead(UUID userId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id " + messageId));

        if (!message.getReceiver().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not the receiver of this message");
        }

        message.setRead(true);
        chatMessageRepository.save(message);
    }

    /**
     * Get all conversations for a user with unread counts
     */
    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        List<Conversation> conversations = conversationRepository.findAllForUser(userId);

        return conversations.stream()
                .map(conversation -> {
                    long unreadCount = chatMessageRepository
                            .countByConversationIdAndReceiverIdAndReadFalse(conversation.getId(), userId);
                    return ChatMapper.toConversationResponse(conversation, userId, unreadCount);
                })
                .toList();
    }

    /**
     * Start or open a conversation with another user
     */
    @Transactional
    public ConversationResponse startConversation(UUID currentUserId, UUID receiverId) {
        if (currentUserId.equals(receiverId)) {
            throw new ResourceNotFoundException("Cannot start a conversation with yourself");
        }
        Conversation conversation = getOrCreateConversation(currentUserId, receiverId);
        long unreadCount = chatMessageRepository
                .countByConversationIdAndReceiverIdAndReadFalse(conversation.getId(), currentUserId);
        return ChatMapper.toConversationResponse(conversation, currentUserId, unreadCount);
    }

    /**
     * Get conversation by ID
     */
    @Transactional(readOnly = true)
    public Conversation getConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id " + conversationId));
    }

    /**
     * Get conversation between two specific users
     */
    @Transactional(readOnly = true)
    public Conversation getConversationBetweenUsers(UUID userId1, UUID userId2) {
        return conversationRepository.findBetweenUsers(userId1, userId2)
                .orElseThrow(() -> new ResourceNotFoundException("No conversation found between these users"));
    }
}
