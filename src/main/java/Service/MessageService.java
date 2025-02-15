package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.*;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    // تم تغيير اسم الدالة
    public Message addMessage(String message_text, int account_id, long time_posted_epoch) {
        Message newMessage = messageDAO.addMessage(message_text, account_id, time_posted_epoch);
        return newMessage;
    }

    public List<Message> getAllMessages() {
        List<Message> messagesList = messageDAO.getAllMessages();
        return messagesList;
    }

    public Message getMessageById(int message_id) {
        Message returnedMessage = messageDAO.getMessageById(message_id);
        return returnedMessage;
    }

    public Message removeMessageById(int message_id) {
        Message deletedMessage = messageDAO.removeMessageById(message_id);
        return deletedMessage;
    }

    public Message modifyMessage(int message_id, String newMessage) {
        Message returnedMessage = messageDAO.modifyMessage(message_id, newMessage);
        return returnedMessage;
    }

    // Method to retrieve all messages posted by a specific user
    public List<Message> getMessagesByUser(int account_id) {
        List<Message> messagesList = messageDAO.getMessagesByUser(account_id);
        return messagesList;
    }
}