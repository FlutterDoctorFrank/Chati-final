package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import net.bytebuddy.utility.RandomString;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import java.util.Random;
import java.util.UUID;

public abstract class PacketTest<T extends Packet<?>> {

    private static final Random RANDOM = new Random();
    private static final Kryo KRYO = new Kryo();
    private final Class<T> clazz;

    protected T before;
    protected T after;

    public PacketTest(@NotNull final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Before
    public void setup() {
        try {
            this.before = null;
            this.after = this.clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public void serialize() {
        final Output output = new Output(512);

        this.before.write(KRYO, output);
        this.after.read(KRYO, new Input(output.getBuffer()));
    }

    public abstract void equals();


    /*
     * Statische Methoden für die zufällige Erzeugung von Netzwerkpaketen.
     */

    public static  <E extends Enum<E>> @NotNull E randomEnum(@NotNull final Class<E> clazz) {
        if (clazz.getEnumConstants().length <= 0) {
            throw new IllegalArgumentException("Non-existent enum elements cannot be randomized");
        }

        return clazz.getEnumConstants()[RANDOM.nextInt(clazz.getEnumConstants().length)];
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

    public static int randomInt(final int bound) {
        return RANDOM.nextInt(bound);
    }

    public static int randomInt() {
        return RANDOM.nextInt();
    }
}
