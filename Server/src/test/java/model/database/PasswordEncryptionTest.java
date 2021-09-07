package model.database;

import model.exception.EncryptionException;
import org.junit.Assert;
import org.junit.Test;
import util.RandomTest;
import java.util.HashSet;
import java.util.Set;

public class PasswordEncryptionTest extends RandomTest {

    private static final int MAX_ATTEMPTS = 128;
    private static final int MIN_ATTEMPTS = 32;

    @Test
    public void generateDifferentSaltTest() {
        final Set<String> salts = new HashSet<>();

        try {
            for (int count = 0; count < randomInt(MIN_ATTEMPTS, MAX_ATTEMPTS); count++) {
                Assert.assertTrue(salts.add(PasswordEncryption.salt()));
            }
        } catch (EncryptionException ex) {
            Assert.fail("Encryption error: " + ex.getMessage());
        }
    }

    @Test
    public void generateDifferentHashTest() {
        final Set<String> hashes = new HashSet<>();
        final String password = randomString();

        try {
            for (int count = 0; count < randomInt(MIN_ATTEMPTS, MAX_ATTEMPTS); count++) {
                final String salt = PasswordEncryption.salt();

                Assert.assertTrue(hashes.add(PasswordEncryption.hash(password, salt)));
            }
        } catch (EncryptionException ex) {
            Assert.fail("Encryption error: " + ex.getMessage());
        }
    }

    @Test
    public void verifyPasswordTest() {
        final String password = randomString();

        try {
            final String salt = PasswordEncryption.salt();
            final String hash = PasswordEncryption.hash(password, salt);

            Assert.assertTrue(PasswordEncryption.verify(password, salt, hash));

            for (int count = 0; count < randomInt(MIN_ATTEMPTS, MAX_ATTEMPTS); count++) {
                Assert.assertFalse(PasswordEncryption.verify(randomString(password), salt, hash));
            }
        } catch (EncryptionException ex) {
            Assert.fail("Encryption error: " + ex.getMessage());
        }
    }
}
