package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.example.AuthUtils;

public class AuthSystem {
    // In-memory "database" of users
    private Map<String, User> users = new HashMap<>();
    private UserManager storage;
    public static final String DUMMY_HASH = AuthUtils.hash("dummy_password");
    private static final int max_login_attempts =3;
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

    static class User implements java.io.Serializable {
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
    public String login(String username, String password)
    {//open method
        // Check if user exists
        User user = users.get(username);
        String hashToCheck = (user != null)? user.password : DUMMY_HASH;

        //Check if the account is locked
        if (user != null && System.currentTimeMillis() < user.lockUntil)
        {//open if
            System.out.println("Account Temporarily locked. Come back Later");
            return null;
        } //close if

        boolean authenticated = AuthUtils.verifyPassword(password, hashToCheck);

        if(authenticated && user !=null)
        { //open if
            user.loginAttempts = 0;
            return "session_" + username + "_"+ System.currentTimeMillis();
        }//close if
        else
        {//open else
            if (user != null) { //open if
                user.loginAttempts++;
                if (user.loginAttempts >= max_login_attempts)
                {//open if
                    user.lockUntil = System.currentTimeMillis() + lock_out_time;
                    user.loginAttempts =0; //resets after locking
                    System.out.println("too many failed attempts. Locked out of system for 1minute");
                }//close if
            }//close if
            return null; //general failure
       }//close else
    }//close method

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
