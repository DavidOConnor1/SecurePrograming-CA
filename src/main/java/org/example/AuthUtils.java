package org.example;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AuthUtils
{//open class

    public static String hash(String password)
    {//open method
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
    }//close method

    public static boolean verifyPassword(String password, String storedHash)
    {//open method
        try
        {//open
            String [] parts = storedHash.split(":");
            if(parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte [] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte [] computedHash = new byte[expectedHash.length];

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withIterations(3)
                    .withMemoryAsKB(16 * 1024)
                    .withParallelism(4)
                    .withSalt(salt)
                    .build();

            generator.init(params);
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), computedHash);
            return constantTimeEquals(expectedHash, computedHash); //bytes constant time equals
        }//close try
        catch (Exception e)
        {//open catch
            throw new RuntimeException(e);
        }//close catch
    }//close method


    public static boolean constantTimeEquals(String a, String b)
    {
        if(a == null || b == null) {
            return false;
        }
        if(a.length() != b.length()) return false;

        int result =0;
        for (int i=0; i<a.length(); i++)
        {//open for
            result |= a.charAt(i) ^ b.charAt(i);
        }//close for
        return result == 0;
    }//close method

    public static boolean constantTimeEquals(byte[] a, byte[] b)
    {//open method
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;

        int result = 0;
        for (int i = 0; i < a.length; i++)
        {//open loop
            result |= a[i] ^ b[i];
        } //close loop

        return result == 0;
    }//close method

} //close class
