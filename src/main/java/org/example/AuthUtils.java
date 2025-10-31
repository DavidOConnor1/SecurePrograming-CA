package org.example;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthUtils
{//open class

    //hashes the password using the Argon2id algorithm with a salt
    public static String hash(String password)
    {//open method
        Argon2BytesGenerator generator = new Argon2BytesGenerator();

        //converts the password into bytes using UTF-8
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        //SaltGeneration
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16-byte salt
        random.nextBytes(salt);

        //generates a 32-byte array to hold result hash
        byte[] hash = new byte[32]; // 32-byte hash

        // Setting Argon2 Parameters
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id);
        builder.withIterations(3)
                .withMemoryAsKB(16 * 1024)
                .withParallelism(4)
                .withSalt(salt);


        Argon2Parameters parameters = builder.build();

        //intialize and generate hash
        generator.init(parameters);
        generator.generateBytes(passwordBytes, hash);

        //returns the salt and hash as Base64 strings
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }//close method


    //verifies a plaintext password against the hash
    public static boolean verifyPassword(String password, String storedHash)
    {//open method
        try
        {//open
            //divides the stored hash into salt and expected hash
            String [] parts = storedHash.split(":");
            if(parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte [] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte [] computedHash = new byte[expectedHash.length];

            //rebuild the parameters with the same settings and the stored salt
            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withIterations(3)
                    .withMemoryAsKB(16 * 1024)
                    .withParallelism(4)
                    .withSalt(salt)
                    .build();

            //generates the hash and salt of the entered plain text password
            generator.init(params);
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), computedHash);
            return constantTimeEquals(expectedHash, computedHash); //compares computed hash and stored hash using constant-time comparision
        }//close try
        catch (Exception e)
        {//open catch
            throw new RuntimeException(e); //wrap errors into runtime exception
        }//close catch
    }//close method

    //performs a constant-time comparison to prevent timing attacks
    public static boolean constantTimeEquals(byte[] a, byte[] b)
    {//open method
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;

        int result = 0;
        for (int i = 0; i < a.length; i++)
        {//open loop
            result |= a[i] ^ b[i]; //XOR each byte and accumalate differences
        } //close loop

        return result == 0; //if result stays 0, bytes matched
    }//close method

} //close class
