package com.sadetech.websocket_messaging.Service;

import com.sadetech.websocket_messaging.Model.Conversation;
import com.sadetech.websocket_messaging.Model.Message;
import com.sadetech.websocket_messaging.Repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sadetech.websocket_messaging.Repository.MessageRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

    @Transactional(readOnly = true)
    public Optional<Conversation> getConversationByParticipants(Long participantOneId, Long participantTwoId) {
        return conversationRepository.findByParticipants(participantOneId, participantTwoId);
    }

    public Optional<Message> getMessageById(String  id){
        Optional<Message> message = messageRepository.findById(id);
        if(message.isPresent()){
            return message;
        }else {
            throw new IllegalArgumentException("No messages found");
    }
    }

    public List<Conversation> getAllConversation(){
        List<Conversation> conversations = conversationRepository.findAll();
        if(!conversations.isEmpty()){
            return conversations;
        }else {
            throw new IllegalArgumentException("No conversation found");
    }
    }

    @Transactional
    public void deleteMessageForSelf(String id, Long userId) {
        Optional<Message> optionalMessage = messageRepository.findById(id);

        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getSenderId().equals(userId)) {
                message.setDeletedBySender(true);  // Mark as deleted by sender
            } else if (message.getRecipientId().equals(userId)) {
                message.setDeletedByRecipient(true);  // Mark as deleted by recipient
            }

            // Save changes
            messageRepository.save(message);

            // If both sender and recipient deleted the message, remove it from the database
            if (message.isDeletedBySender() && message.isDeletedByRecipient()) {
                messageRepository.delete(message);
            }
        } else {
            throw new IllegalArgumentException("Message not found");
        }
    }

    @Transactional
    public void deleteMessageForEveryone(String id, Long userId) {
        Optional<Message> optionalMessage = messageRepository.findById(id);

        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();

            logger.info("Attempting to delete message with id: {}", id);

            if (message.getSenderId().equals(userId)) {
                logger.info("User {} is authorized to delete message {}", userId, id);
                messageRepository.delete(message);
                logger.info("Message {} deleted successfully", id);
            } else {
                logger.warn("User {} is not authorized to delete message {}", userId, id);
                throw new IllegalArgumentException("Recipient can't delete the message to everyone.");
            }
        } else {
            logger.error("Message with id {} not found", id);
            throw new IllegalArgumentException("Message not found");
        }
    }



}