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

/**
 * EN: Manages direct-message conversations, sending messages, and read state.
 * KA: მართავს პირადი შეტყობინებების საუბრებს, გაგზავნას და წაკითხვის სტატუსს.
 */
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
     * EN: Returns an existing conversation between two users, or creates one if missing.
     * KA: აბრუნებს არსებულ საუბარს ორ მომხმარებელს შორის, ან ქმნის ახალს თუ არ არსებობს.
     */
    @Transactional
    public Conversation getOrCreateConversation(UUID userId1, UUID userId2) {
        // EN: Load both users from DB | KA: ორივე მომხმარებლის ჩატვირთვა ბაზიდან
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId2));

        // EN: Reuse existing conversation or create new | KA: არსებული საუბრის გამოყენება ან ახლის შექმნა
        return conversationRepository.findBetweenUsers(userId1, userId2)
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setUser1(user1);
                    conversation.setUser2(user2);
                    return conversationRepository.save(conversation);
                });
    }

    /**
     * EN: Sends a chat message from the sender to the receiver and updates the conversation timestamp.
     * KA: აგზავნის ჩატის შეტყობინებას გამგზავნიდან მიმღებთან და განაახლებს საუბრის დროის ნიშანს.
     */
    @Transactional
    public ChatMessageResponse sendMessage(UUID senderId, ChatMessageRequest request) {
        // EN: Load sender and receiver from DB | KA: გამგზავნისა და მიმღების ჩატვირთვა ბაზიდან
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender user not found with id " + senderId));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found with id " + request.getReceiverId()));

        // EN: Get or create conversation and bump updatedAt | KA: საუბრის მიღება/შექმნა და updatedAt განახლება
        Conversation conversation = getOrCreateConversation(senderId, request.getReceiverId());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        // EN: Persist new unread message | KA: ახალი წაუკითხავი შეტყობინების შენახვა
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
     * EN: Returns paginated chat history and marks the conversation as read for the viewer.
     * KA: აბრუნებს ჩატის ისტორიას გვერდებად და მონიშნავს საუბარს წაკითხულად მნახველისთვის.
     */
    @Transactional
    public Page<ChatMessageResponse> getChatHistory(UUID userId, Long conversationId, Pageable pageable) {
        // EN: Load conversation from DB | KA: საუბრის ჩატვირთვა ბაზიდან
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id " + conversationId));

        // EN: Validate user is a participant | KA: მომხმარებლის მონაწილეობის შემოწმება
        if (!conversation.getUser1().getId().equals(userId) && !conversation.getUser2().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not part of this conversation");
        }

        // EN: Side effect — mark messages as read when opening the thread | KA: გვერდითი ეფექტი — შეტყობინებების წაკითხულად მონიშვნა
        chatMessageRepository.markConversationAsRead(conversationId, userId);

        return chatMessageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable)
                .map(ChatMapper::toMessageResponse);
    }

    /**
     * EN: Marks all messages in a conversation as read for the given user.
     * KA: მონიშნავს საუბრის ყველა შეტყობინებას წაკითხულად მოცემული მომხმარებლისთვის.
     */
    @Transactional
    public void markMessagesAsRead(UUID userId, Long conversationId) {
        // EN: Load conversation from DB | KA: საუბრის ჩატვირთვა ბაზიდან
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id " + conversationId));

        // EN: Validate user is a participant | KA: მომხმარებლის მონაწილეობის შემოწმება
        if (!conversation.getUser1().getId().equals(userId) && !conversation.getUser2().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not part of this conversation");
        }

        // EN: Side effect — mark conversation messages as read | KA: გვერდითი ეფექტი — საუბრის შეტყობინებების წაკითხულად მონიშვნა
        chatMessageRepository.markConversationAsRead(conversationId, userId);
    }

    /**
     * EN: Marks a single message as read after verifying the user is the receiver.
     * KA: მონიშნავს ერთ შეტყობინებას წაკითხულად, მას შემდეგ რაც ამოწმებს, რომ მომხმარებელი მიმღებია.
     */
    @Transactional
    public void markMessageAsRead(UUID userId, Long messageId) {
        // EN: Load message from DB | KA: შეტყობინების ჩატვირთვა ბაზიდან
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id " + messageId));

        // EN: Only the receiver may mark as read | KA: მხოლოდ მიმღებს შეუძლია წაკითხულად მონიშვნა
        if (!message.getReceiver().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not the receiver of this message");
        }

        // EN: Persist read flag | KA: წაკითხვის დროშის შენახვა
        message.setRead(true);
        chatMessageRepository.save(message);
    }

    /**
     * EN: Lists all conversations for a user including unread message counts.
     * KA: აბრუნებს მომხმარებლის ყველა საუბარს წაუკითხავი შეტყობინებების რაოდენობით.
     */
    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(UUID userId) {
        // EN: Ensure user exists | KA: მომხმარებლის არსებობის შემოწმება
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        // EN: Load conversations and attach unread counts | KA: საუბრების ჩატვირთვა და წაუკითხავების რაოდენობის მიბმა
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
     * EN: Starts or opens a conversation with another user and returns it with unread count.
     * KA: იწყებს ან ხსნის საუბარს სხვა მომხმარებელთან და აბრუნებს წაუკითხავების რაოდენობით.
     */
    @Transactional
    public ConversationResponse startConversation(UUID currentUserId, UUID receiverId) {
        // EN: Reject conversation with self | KA: საკუთარ თავთან საუბრის უარყოფა
        if (currentUserId.equals(receiverId)) {
            throw new ResourceNotFoundException("Cannot start a conversation with yourself");
        }
        // EN: Get or create conversation | KA: საუბრის მიღება ან შექმნა
        Conversation conversation = getOrCreateConversation(currentUserId, receiverId);
        long unreadCount = chatMessageRepository
                .countByConversationIdAndReceiverIdAndReadFalse(conversation.getId(), currentUserId);
        return ChatMapper.toConversationResponse(conversation, currentUserId, unreadCount);
    }

    /**
     * EN: Loads a conversation entity by id or throws if missing.
     * KA: ჩატვირთავს საუბრის ენთითის ID-ით ან აგდებს გამონაკლისს თუ არ არსებობს.
     */
    @Transactional(readOnly = true)
    public Conversation getConversationById(Long conversationId) {
        // EN: Load conversation from DB | KA: საუბრის ჩატვირთვა ბაზიდან
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id " + conversationId));
    }

    /**
     * EN: Loads the conversation between two specific users or throws if none exists.
     * KA: ჩატვირთავს საუბარს ორ კონკრეტულ მომხმარებელს შორის ან აგდებს გამონაკლისს თუ არ არსებობს.
     */
    @Transactional(readOnly = true)
    public Conversation getConversationBetweenUsers(UUID userId1, UUID userId2) {
        // EN: Lookup conversation between users | KA: საუბრის მოძიება მომხმარებლებს შორის
        return conversationRepository.findBetweenUsers(userId1, userId2)
                .orElseThrow(() -> new ResourceNotFoundException("No conversation found between these users"));
    }
}
