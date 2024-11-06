package com.sadetech.websocket_messaging.Service;

import com.sadetech.websocket_messaging.Model.Conversation;
import com.sadetech.websocket_messaging.Model.Message;
import com.sadetech.websocket_messaging.Repository.ConversationRepository;
import com.sadetech.websocket_messaging.Repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public Optional<Conversation> getConversationWithMessages(Long participantOneId, Long participantTwoId) {
        Optional<Conversation> conversation = conversationRepository.findByParticipants(participantOneId, participantTwoId);

        conversation.ifPresent(conv -> {
            List<Message> messages = messageRepository.findByConversationId(conv.getId());
            conv.setMessages(messages); // Manually set messages
        });

        return conversation;
    }

    @Transactional(readOnly = true)
    public List<Conversation> getAllConversations() {
        List<Conversation> conversations = conversationRepository.findAll();
        if (conversations.isEmpty()) {
            throw new IllegalArgumentException("No conversations found");
        }
        return conversations;
    }

    @Transactional
    public void deleteMessageForSelf(String id, Long userId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + id));

        if (message.getSenderId().equals(userId)) {
            message.setDeletedBySender(true);
        } else if (message.getRecipientId().equals(userId)) {
            message.setDeletedByRecipient(true);
        } else {
            throw new IllegalArgumentException("User is not authorized to delete this message.");
        }

        messageRepository.save(message);

        if (message.isDeletedBySender() && message.isDeletedByRecipient()) {
            messageRepository.delete(message);
        }
    }

    @Transactional
    public void deleteMessageForEveryone(String id, Long userId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + id));

        logger.info("Attempting to delete message with id: {}", id);

        if (message.getSenderId().equals(userId)) {
            logger.info("User {} is authorized to delete message {}", userId, id);
            messageRepository.delete(message);
            logger.info("Message {} deleted successfully", id);
        } else {
            logger.warn("User {} is not authorized to delete message {}", userId, id);
            throw new IllegalArgumentException("Only the sender can delete the message for everyone.");
        }
    }
}
