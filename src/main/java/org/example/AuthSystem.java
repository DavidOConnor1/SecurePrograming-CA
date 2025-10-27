package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class AuthSystem {
    // In-memory "database" of users
    private Map<String, User> users; //intializing users
    private Map<String, LoginSession> sessions = new ConcurrentHashMap<>(); //intializing login sessions
    private UserManager storage; //intializing storage
    public static final String DUMMY_HASH = AuthUtils.hash("dummy_password"); //intializing and defining dummy account
    private static final int max_login_attempts =3; //sets max limit for logins
    private static final long lock_out_time = 60*1000; //1minute lock


    public AuthSystem(UserManager storage) {
        this.storage = storage;

        // Load existing users at startup
        try {
            this.users = storage.loadUsers();
            System.out.println("Users have been successfully loaded");
        } catch (IOException | ClassNotFoundException e) {
            this.users = new HashMap<>();
            System.out.println("No existing users found. Starting fresh.");
        }
    }

    static class User implements Serializable {
        private static final long serialVersionUID = 1L; //unique id for serialization
        String password;
        int loginAttempts = 0; //tracking user login attempts
        long lockUntil =0;

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

        // Save immediately after adding a user
        try {
            storage.storeUsers(users);
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
            return false;
        }

        return true;
    }//end method



    /**
     * Authenticates a user.
     * @return Session ID on success, null on failure.
     */
    public String login(String username, String password, String sessionId) {
        // Create or retrieve session tracker for this sessionId
        LoginSession logsesh = sessions.computeIfAbsent(sessionId, k -> new LoginSession());

        // Check if session is locked (for brute-force prevention)
        if (logsesh.lockedOut()) {
            System.out.println("Too many failed attempts, try again later");
            return null;
        }

        // Fetch the user (may be null)
        User user = users.get(username);

        // Always use a hash to avoid timing attacks
        String hashToCheck = (user != null) ? user.password : DUMMY_HASH;
        boolean authenticated = AuthUtils.verifyPassword(password, hashToCheck);

        // Check if the real user's account is locked
        if (user != null && System.currentTimeMillis() < user.lockUntil) {
            System.out.println("Account temporarily locked. Come back later");
            return null;
        }

        if (authenticated && user != null) {
            // Successful login for real user
            logsesh.resetAttempts();        // reset session attempts
            user.loginAttempts = 0;         // reset user attempts
            return "session_" + username + "_" + System.currentTimeMillis();
        } else {
            // Failed login (real or dummy)
            logsesh.incrementFailedAttempts();

            // Lock session if max attempts reached
            if (logsesh.getAttempts() >= max_login_attempts) {
                logsesh.lock(lock_out_time);
                System.out.println("Too many failed attempts, try again later");
            }

            // Lock the real user if max attempts reached
            if (user != null) {
                user.loginAttempts++;
                if (user.loginAttempts >= max_login_attempts) {
                    user.lockUntil = System.currentTimeMillis() + lock_out_time;
                    user.loginAttempts = 0; // reset after locking
                    System.out.println("Too many failed attempts. Account locked for 1 minute");
                }
            }

            return null; // general failure
        }
    }


    public Map<String, User> getUsers() {
        return users;
    }



    /**
     * Checks if a session token is valid.
     */
    public boolean isSessionValid(String sessionToken) {
        return sessionToken != null && sessionToken.startsWith("session_");
    }
}
