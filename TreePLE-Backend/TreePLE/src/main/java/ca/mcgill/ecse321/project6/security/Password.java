package ca.mcgill.ecse321.project6.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

public class Password {
    // The higher the number of iterations the more 
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int iterations = 200;
    private static final int saltLen = 8;
    private static final int desiredKeyLen = 126;
    
    private static final Encoder base64Encoder = Base64.getEncoder();
    private static final Decoder base64Decoder = Base64.getDecoder();

    /** Computes a salted PBKDF2 hash of given plaintext password
        suitable for storing in a database. 
        Empty passwords are not supported. */
    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        return base64Encoder.encodeToString(salt) + "$" + hash(password, salt);
    }

    /** Checks whether given plaintext password corresponds 
        to a stored salted hash of the password. */
    public static boolean check(String password, String storedHash) throws Exception {
        String[] saltAndPass = storedHash.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException("The stored password hash is formatted incorrectly.");
        }
        String hashOfInput = hash(password, base64Decoder.decode(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
            password.toCharArray(), salt, iterations, desiredKeyLen)
        );
        return base64Encoder.encodeToString(key.getEncoded());
    }
}