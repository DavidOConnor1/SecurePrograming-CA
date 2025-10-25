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

public class AuthSystem {
    // In-memory "database" of users
    private Map<String, User> users = new HashMap<>();


    static class User {
        String password; // Stored in plain text
        int loginAttempts = 0;

        User(String password) {
            this.password = password;
        }
    }

    public String hash(String password) {
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        //SaltGeneration
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16-byte salt
        random.nextBytes(salt);

        byte[] hash = new byte[32]; // 32-byte hash

        // Setting Argon2 Parameters
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder();
        builder.withIterations(3)
                .withMemoryAsKB(16 * 1024)
                .withParallelism(4)
                .withSalt(salt); // ✅ corrected casing

        Argon2Parameters parameters = builder.build();

        generator.init(parameters);
        generator.generateBytes(passwordBytes, hash);

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
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

    /**
     * Authenticates a user.
     * @return Session ID on success, null on failure.
     */
    public String login(String username, String password) {
        // Check if user exists
        if (!users.containsKey(username)) {
            return null; // Early return reveals valid users
        }

        User user = users.get(username);

        // Check password (vulnerable to timing attack)
        if (user.password.equals(password)) {
            user.loginAttempts = 0; // Reset on success
            // Generate a simple session token
            return "session_" + username + "_" + System.currentTimeMillis();
        } else {
            user.loginAttempts++;
            return null;
        }
        if(user.loginAttempts == 3){
            System.out.println("Reached Max Attempts");
            system.exit(0);
        }
    }

    /**
     * Checks if a session token is valid.
     */
    public boolean isSessionValid(String sessionToken) {
        return sessionToken != null && sessionToken.startsWith("session_");
    }
}
