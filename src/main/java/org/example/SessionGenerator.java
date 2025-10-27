package org.example;

import org.bouncycastle.util.encoders.Base64Encoder;

import java.security.SecureRandom;
import java.util.Base64;

public class SessionGenerator {//open class
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateSessionToken()
    {//open method
        byte[] randomBytes = new byte[32]; //256-bit token
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    } //close

}//close class
