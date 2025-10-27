package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.example.AuthUtils;

public class AuthSystem {
    // In-memory "database" of users
    private Map<String, User> users = new HashMap<>();
    private final AuthUtils utility = new AuthUtils();

    static class User {
        String password; // Stored in plain text
        int loginAttempts = 0;

        User(String password) {
            this.password = password;
        }
    }


    /**
     * Registers a new user.
     * @return true if successful, false if user exists.
     */
    public boolean register(String username, String password) {
        if (username == null || username.isEmpty() || users.containsKey(username)) {
            return false;
        }

        users.put(username, new User(password));
        return true;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> loadedUsers) {
        this.users = loadedUsers;
    }

    /**
     * Authenticates a user.
     * @return Session ID on success, null on failure.
     */
    public String login(String username, String password)
    {//open method
        // Check if user exists
        User user = users.get(username);

        if(user == null)
        {
            try{Thread.sleep(200);} catch (InterruptedException ignored) {}
            return null;
        }

        boolean authenticated = AuthUtils.verifyPassword(password, user.password);

        try { Thread.sleep(200);} catch (InterruptedException ignored) {}

        if(authenticated)
        { //open if
            user.loginAttempts = 0;
            return "session_" + username + "_"+ System.currentTimeMillis();
        }//close if
        else
        {//open else
            user.loginAttempts++;
            return null;
       }//close else
    }//close method

    /**
     * Checks if a session token is valid.
     */
    public boolean isSessionValid(String sessionToken) {
        return sessionToken != null && sessionToken.startsWith("session_");
    }
}
