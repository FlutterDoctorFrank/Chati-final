package controller.network;

import model.MessageBundle;
import model.context.ContextID;
import net.bytebuddy.utility.RandomString;
import org.jetbrains.annotations.NotNull;
import java.util.Random;
import java.util.UUID;

public abstract class RandomTest {

    private static final Random RANDOM = new Random();

    /*
     * Statische Methoden für die zufällige Erzeugung von Netzwerkpaketen.
     */

    public static  <E extends Enum<E>> @NotNull E randomEnum(@NotNull final Class<E> clazz, @NotNull final E without) {
        if (clazz.getEnumConstants().length <= 0) {
            throw new IllegalArgumentException("Non-existent enum elements cannot be randomized");
        }

        E random = null;

        while (random == null || random == without) {
            random = clazz.getEnumConstants()[RANDOM.nextInt(clazz.getEnumConstants().length)];
        }

        return random;
    }

    public static  <E extends Enum<E>> @NotNull E randomEnum(@NotNull final Class<E> clazz) {
        if (clazz.getEnumConstants().length <= 0) {
            throw new IllegalArgumentException("Non-existent enum elements cannot be randomized");
        }

        return clazz.getEnumConstants()[RANDOM.nextInt(clazz.getEnumConstants().length)];
    }

    public static @NotNull MessageBundle randomBundle() {
        final Object[] arguments = new Object[randomInt(3)];

        for (int index = 0; index < arguments.length; index++) {
            arguments[index] = randomString();
        }

        return new MessageBundle(randomString(), arguments);
    }

    public static @NotNull ContextID randomContextId() {
        return new ContextID(randomString());
    }

    public static @NotNull UUID randomUniqueId() {
        return UUID.randomUUID();
    }

    public static @NotNull String randomString() {
        return RandomString.make(randomInt(16) + 1);
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static byte[] randomBytes() {
        final byte[] bytes = new byte[randomInt(64)];

        for (int index = 0; index < bytes.length; index++) {
            bytes[index] = (byte) randomInt();
        }

        return bytes;
    }

    public static float randomFloat() {
        return RANDOM.nextFloat();
    }

    public static int randomInt(final int bound) {
        return RANDOM.nextInt(bound);
    }

    public static int randomInt() {
        return RANDOM.nextInt();
    }
}
