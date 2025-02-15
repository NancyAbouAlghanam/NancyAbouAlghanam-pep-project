package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.*;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;

public class SocialMediaController {
    // Declare service variables
    AccountService accountService;
    MessageService messageService;

    // Constructor which instantiates service variables
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::createNewAccountHandler);
        app.post("login", this::authenticateAccountHandler);
        app.post("/messages", this::createNewMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByUserHandler);
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    // Handler to create an account POST /register

    private void createNewAccountHandler(Context ctx) {
        try {
            Account account = ctx.bodyAsClass(Account.class);
    
            if (account == null || account.getUsername().trim().isEmpty()) {
                ctx.status(400).result("");  // Empty username case
                return;
            }
    
            Account existingAccount = accountService.getAccountByUsername(account.getUsername()); // Check if the username already exists
            if (existingAccount != null) {
                ctx.status(400).result("");  // Duplicate username case
                return;
            }
    
            Account newAccount = accountService.createAccount(account); // Attempt to create the account
            if (newAccount == null) {
                ctx.status(400).result("");  // If account creation fails, return 400
                return;
            }
    
            ctx.json(accountService.authenticateAccount(newAccount)).status(200); // Send a successful response with the account
    
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error"); // Handle exceptions in case of an error
        }
    }

    // Handler to authenticate an account POST /login

    private void authenticateAccountHandler(Context ctx) {
        try {
            Account account = ctx.bodyAsClass(Account.class);
            
            Account authenticatedAccount = accountService.authenticateAccount(account); // Use the function verifyAccountCredentials
    
            if (authenticatedAccount == null) {
                ctx.status(401).json(""); // Return error 401 if the account is not found
                return;
            }
    
            ctx.json(authenticatedAccount).status(200);  // Return the account if authentication is successful
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error"); // Handle errors
        }
    }

    // Handler to create a message POST /messages

    private void createNewMessageHandler(Context ctx) {
        try {
            Message message = ctx.bodyAsClass(Message.class);

            if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
                ctx.status(400).result("");
                return;
            }

            if (message.getMessage_text().length() > 255) {
                ctx.status(400).result("");
                return;
            }    

            if (accountService.getAccountById(message.getPosted_by()) == null) {
                ctx.status(400).result("");
                return;
            }

            Message newMessage = messageService.addMessage(
                message.getMessage_text(),
                message.getPosted_by(),
                message.getTime_posted_epoch()
            );
        
            if (newMessage == null) {
                ctx.status(400).result("");
                return;
            }
    
            ctx.json(newMessage).status(200);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to get all messages GET /messages

    private void getAllMessagesHandler(Context ctx) {
        try {
            List<Message> messagesList = messageService.getAllMessages();
            ctx.json(messagesList).status(200);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to get a message by ID GET /messages/{message_id}

    private void getMessageByIdHandler(Context ctx) {
        try {
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            Message returnedMessage = messageService.getMessageById(message_id);

            if (returnedMessage == null) {
                ctx.json("").status(200);
                return;
            }

            ctx.json(returnedMessage).status(200);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to delete a message by ID DELETE /messages/{message_id}

    private void deleteMessageByIdHandler(Context ctx) {
        try {
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            Message returnedMessage = messageService.removeMessageById(message_id);
    
            if (returnedMessage != null) {  // If the message exists and is deleted, return the deleted message with status 200
                ctx.json(returnedMessage).status(200);
            } else {
                ctx.json("").status(200); // If the message doesn't exist, return a 200 response with an empty body
            }
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to update a message by message ID PATCH /messages/{message_id}

    private void updateMessageByIdHandler(Context ctx) {
        try {  
            int message_id = Integer.parseInt(ctx.pathParam("message_id")); // Retrieve the message ID from the path
            
            ObjectMapper objectMapper = new ObjectMapper(); // Read the body of the request (the new message)
            Map<String, Object> requestBody = objectMapper.readValue(ctx.body(), new TypeReference<Map<String, Object>>() {});

            String updatedMessageText = (String) requestBody.get("message_text"); // Extract the new message tex

            if (updatedMessageText == null || updatedMessageText.trim().isEmpty()) {
                ctx.status(400).json(""); // If the text is empty or contains only spaces, return 400
                return;
            }

            if (updatedMessageText.length() > 255) { // If the text is too long, return 400
                ctx.status(400).json("");
                return;
            }
            // Attempt to update the message using the modifyMessage function
            Message updatedMessage = messageService.modifyMessage(message_id, updatedMessageText);
    
            if (updatedMessage == null) {
                ctx.status(400).json(""); // If the message is not found in the database, return 400
                return;
            }

            ctx.json(updatedMessage).status(200);  // Return the updated message with status 200
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error"); // Handle general errors
        }
    }

    // Handler to get all messages by a particular user GET /accounts/{account_id}/messages
    
    private void getAllMessagesByUserHandler(Context ctx) {
        try {
            int account_id = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messagesList = messageService.getMessagesByUser(account_id);
            ctx.json(messagesList).status(200);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }
}