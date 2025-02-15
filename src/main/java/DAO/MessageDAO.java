package DAO;

import java.sql.*;
import java.util.*;
import Model.Message;
import Service.AccountService;
import Util.ConnectionUtil;

public class MessageDAO {

    public Message addMessage(String message_text, int account_id, long time_posted_epoch) {
        Connection connection = ConnectionUtil.getConnection();
        AccountService accountService = new AccountService();
        int posted_by = account_id;

        if (message_text == null || message_text.trim().isEmpty() || message_text.length() > 255)
            return null;

        if (accountService.getAccountById(account_id) == null)
            return null;

        try {
            String sql = "INSERT INTO Message(posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, posted_by);
            preparedStatement.setString(2, message_text);
            preparedStatement.setLong(3, time_posted_epoch);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                int newMessageId = getMessageId(message_text);
                return new Message(newMessageId, posted_by, message_text, time_posted_epoch);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public int getMessageId(String message_text) {
        Connection connection = ConnectionUtil.getConnection();

        if (message_text == null || message_text.trim().isEmpty())
            return 0;

        try {
            String sql = "SELECT message_id FROM message WHERE message_text = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, message_text);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // If message is found, create and return a Message object
                return resultSet.getInt("message_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messageList = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int message_id = resultSet.getInt("message_id");
                String message_text = resultSet.getString("message_text");
                int posted_by = resultSet.getInt("posted_by");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");
                messageList.add(new Message(message_id, posted_by, message_text, time_posted_epoch));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return messageList;
    }

    public Message getMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message WHERE message_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Message(message_id, resultSet.getInt("posted_by"), resultSet.getString("message_text"), resultSet.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Message removeMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        Message returnedMessage = getMessageById(message_id);

        try {
            String sql = "DELETE FROM message WHERE message_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            preparedStatement.executeUpdate();

            return returnedMessage;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Message modifyMessage(int message_id, String newMessage) {
        Connection connection = ConnectionUtil.getConnection();

        if (newMessage == null || newMessage.trim().isEmpty() || newMessage.length() > 255) // Validation: Ensure new message is not empty or too long
            return null;

        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newMessage);
            preparedStatement.setInt(2, message_id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected < 1) {
                return null;
            }

            return getMessageById(message_id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<Message> getMessagesByUser(int account_id) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messageList = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Iterate through the result set and add each message to the list
                messageList.add(new Message(resultSet.getInt("message_id"), resultSet.getInt("posted_by"), resultSet.getString("message_text"), resultSet.getLong("time_posted_epoch")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return messageList;
    }
}