
package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    // Method to create a new account, ensuring the username is unique
    public Account createAccount(Account account) {
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null;  // Return null if the username already exists
        }
        return accountDAO.insertAccount(account); // Calls DAO to insert the account into the database
    }

    // Method to authenticate an account by username and password
    public Account authenticateAccount(Account account) {
        Account authenticatedAccount = accountDAO.getAccountByCredentials(account);
        return authenticatedAccount;
    }

    // Method to retrieve an account by its unique ID
    public Account getAccountById(int account_id) {
        Account validatedAccount = accountDAO.getAccountById(account_id);
        return validatedAccount;
    }

    // Method to retrieve an account by its username
    public Account getAccountByUsername(String username) {
        return accountDAO.getAccountByUsername(username);
    }
}