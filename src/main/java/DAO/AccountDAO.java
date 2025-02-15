package DAO;

import java.sql.*;
import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {

    public Account insertAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        String username = account.getUsername();
        String password = account.getPassword();

        if (username == null || username.trim().isEmpty() || password.length() < 4) { // Validation: Ensure username is not empty and password length is at least 4 characters
            return null;
        }

        try {
            String sql = "INSERT INTO Account(username, password) VALUES(?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();
            return account;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Account getAccountByCredentials(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        String username = account.getUsername();
        String password = account.getPassword();

        // Validation: Ensure username and password are not empty
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty())
            return null;

        try {
            String sql = "SELECT * FROM Account WHERE username = ? AND password = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Account(resultSet.getInt("account_id"), resultSet.getString("username"), resultSet.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Account getAccountById(int account_id) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM Account WHERE account_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Account(resultSet.getInt("account_id"), resultSet.getString("username"), resultSet.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage()); // Print error message if SQL query fails
        }

        return null;
    }

    public Account getAccountByUsername(String username) {
        Connection connection = ConnectionUtil.getConnection();
    
        try {
            String sql = "SELECT * FROM Account WHERE username = ?;";  //to select account by username
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
    
            ResultSet resultSet = preparedStatement.executeQuery(); // Execute the query and check if account exists
            if (resultSet.next()) {
                return new Account(resultSet.getInt("account_id"), resultSet.getString("username"), resultSet.getString("password")); // Create and return Account object if found
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage()); // Print error message if SQL query fails
        }
    
        return null; // Return null if account not found
    }
}