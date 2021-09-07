package model.database;

import model.exception.EncryptionException;
import org.jetbrains.annotations.NotNull;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class PasswordEncryption {

    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final String RANDOM_ALGORITHM = "SHA1PRNG";

    private static final int KEY_ITERATIONS = 65536;
    private static final int KEY_LENGTH = 24 * 8;

    private PasswordEncryption() {

    }

    public static @NotNull String salt() throws EncryptionException {
        final byte[] salt = new byte[24];

        try {
            final SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);

            random.nextBytes(salt);
        } catch (NoSuchAlgorithmException ex) {
            throw new EncryptionException("Failed to find algorithmen for salt generating", ex);
        }

        return encode(salt);
    }

    public static @NotNull String hash(@NotNull final String password, @NotNull final String salt) throws EncryptionException {
        final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), decode(salt), KEY_ITERATIONS, KEY_LENGTH);

        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);

            return encode(factory.generateSecret(spec).getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            throw new EncryptionException("Failed to find algorithmen for hashing", ex);
        } catch (InvalidKeySpecException ex) {
            throw new EncryptionException("Failed to deliver valid key spec for the hashing algorithmen", ex);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean verify(@NotNull final String password, @NotNull final String salt,
                                 @NotNull final String hash) throws EncryptionException {
        return Arrays.equals(decode(hash), decode(hash(password, salt)));
    }

    private static @NotNull String encode(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] decode(@NotNull final String string) {
        return Base64.getDecoder().decode(string);
    }
}
