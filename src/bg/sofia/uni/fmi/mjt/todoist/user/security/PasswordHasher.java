package bg.sofia.uni.fmi.mjt.todoist.user.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class PasswordHasher {
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 77;
    private static final int SALT_SIZE = 8;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final Random RANDOM = new SecureRandom();

    public static byte[] getHashedPassword(String password, byte[] saltBytes) {

        char[] passwordChars = password.toCharArray();

        return hashPassword(passwordChars, saltBytes);
    }

    public static byte[] getSalt() {
        byte[] salt = new byte[SALT_SIZE];
        RANDOM.nextBytes(salt);

        return salt;
    }

    private static byte[] hashPassword(char[] password, byte[] salt) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
