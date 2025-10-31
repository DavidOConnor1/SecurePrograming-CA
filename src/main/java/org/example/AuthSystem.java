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
    private Map<String, User> users; //intialize users
    private Map<String, LoginSession> sessions = new ConcurrentHashMap<>(); //tracks attempts for temp login session
    private UserManager storage; //manage persistent storage
    public static final String DUMMY_HASH = AuthUtils.hash("dummy_password"); //dummy account set for protection against timing attacks
    private static final int max_login_attempts =3; //sets max limit for logins
    private static final long lock_out_time = 60*1000; //1minute lock
    private static final Map<String, SessionInfo> activeSessions = new HashMap<>(); // active session tokens mapped to session information
    private static final long session_length = 30*60*1000; //a session length is 30 mins

    //constructs auth system with given UserManager
    public AuthSystem(UserManager storage) {
        this.storage = storage; //intializes the storage

        // Load existing users at startup
        try {//open try
            this.users = storage.loadUsers();// pulls the users from storage
            System.out.println("Users have been successfully loaded"); //gives a message to the user to let them know users are loaded
        }//end try
        catch (IOException | ClassNotFoundException e) {
            this.users = new HashMap<>(); //creates a new hashmap if there is no .dat file
            System.out.println("No existing users found. Starting fresh."); //notifies the user it is creating a new .dat file to store users
        }//end catch
    }//end method

    //represents an registered account
    static class User implements Serializable {
        private static final long serialVersionUID = 1L; //unique id for serialization for storing the accounts in the .dat file
        String password; //formats the user class to have a password (which is encrypted)
        int loginAttempts = 0; //tracking user login attempts
        long lockUntil =0; //timestamp until which the account is locked 0 = not locked

        User(String password) {
            this.password = password;
        }
    }

    //this method is creating a session token for the registration part of the program
    //generates and stores temp session token to be validated
    public String startRegistrationSession(String username)
    {//open
        String sessionToken = SessionGenerator.generateSessionToken(); //generates session token
        activeSessions.put(sessionToken, new SessionInfo(username)); //starts the session when the user enters their name
        return sessionToken; //returns the new encrypted session token
    }//close

    /**
     * Registers a new user.
     * @return true if successful, false if user exists.
     */
    public boolean register(String username, String password, String sessionToken) {
        if (username == null || username.isEmpty() || users.containsKey(username)) {
            return false;
        }

        //checks if session is valid before creating account
        if (!isSessionValid(sessionToken))
        {
            System.out.println("Invalid or Expired Session Token. Registration Failed");
            return false; //ends session if token is invalid
        }

        users.put(username, new User(password)); //creates the user with the details provided (password is hashed in app)

        // Save the updated user database
        try {
            storage.storeUsers(users);
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage()); //notifies user it could not save their account
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
        LoginSession logsesh = sessions.computeIfAbsent(sessionId, k -> new LoginSession()); //creates a session token for login

        // Check if session is locked (for brute-force prevention)
        if (logsesh.lockedOut()) {
            System.out.println("Too many failed attempts, try again later");
            return null; //won't allow the user to login
        }

        // Fetch the user (maybe null)
        User user = users.get(username); //fetches the user by name

        // Compute hash (even for invalid accounts) to mitigate against timing attacks
        String hashToCheck = (user != null) ? user.password : DUMMY_HASH; //creates a dummby account to be pulled at the same time
        boolean authenticated = AuthUtils.verifyPassword(password, hashToCheck); //checks the password and the fake password against the verification

        // if account is temporarily locked deny login
        if (user != null && System.currentTimeMillis() < user.lockUntil) {
            System.out.println("Account temporarily locked. Come back later");
            return null;
        }

        // Successful login
        if (authenticated && user != null) {

            logsesh.resetAttempts();        // reset session attempts
            user.loginAttempts = 0;         // reset user attempts

            //generate valid user session token
            String sessionToken = SessionGenerator.generateSessionToken();
            activeSessions.put(sessionToken, new SessionInfo(username));

            //clarifies successful token
            if (isSessionValid(sessionToken))
            {
                System.out.println("Session created and Token is valid");

            }

            return sessionToken;
        } else {
            // Failed login attempt
            logsesh.incrementFailedAttempts();

            // Lock session if max attempts reached
            if (logsesh.getAttempts() >= max_login_attempts) {
                logsesh.lock(lock_out_time);
                System.out.println("Too many failed attempts, try again later");
            }

            // Lock the real user if max attempts reached prevents timing attacks since it will lock the user out too if even was not an account
            if (user != null) {//open if
                user.loginAttempts++; //increment the counter
                //when login attempts reach max attempts, lock out user
                if (user.loginAttempts >= max_login_attempts) { //open if
                    user.lockUntil = System.currentTimeMillis() + lock_out_time; //creates the lock time
                    user.loginAttempts = 0; // reset after locking
                    System.out.println("Too many failed attempts. Account locked for 1 minute");
                } //ends if
            } //ends if

            return null; // general failure
        }//ends else
    }//end login

    //returns all users currently loaded in memory
    public Map<String, User> getUsers() {
        return users;
    }



    /**
     * Checks if a session token is valid.
     */
    public boolean isSessionValid(String sessionToken) {//opens isSessionValid
        SessionInfo sessionInfo = activeSessions.get(sessionToken); //takes session token
        if(sessionInfo == null) return false; // if there is no token return null

        long current_session = System.currentTimeMillis() - sessionInfo.getCreatedAt(); //tracks the session since when it is created
        //ends session after certain time
        if (current_session > session_length)
        {
            activeSessions.remove(sessionToken); //removes the token after session length is reached
            return false;
        }

        return true; //if session token is valid allow the user to move forward

    }//closes session is valid
}//close class
