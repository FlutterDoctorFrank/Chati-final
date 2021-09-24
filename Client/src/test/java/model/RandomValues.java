package model;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RandomValues {

    private static final SecureRandom random = new SecureRandom();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static String randomString(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for(int i = 0; i < length; i++)
            stringBuilder.append(chars.charAt(rnd.nextInt(chars.length())));
        return stringBuilder.toString();
    }

    public static String random8LengthString() {
        return randomString(8);
    }

    public static UUID randomUUID() {
        return UUID.randomUUID();
    }

    public static boolean randomBoolean() {
        return new Random().nextBoolean();
    }
}
