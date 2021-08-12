package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import controller.network.RandomTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;

public abstract class PacketTest<T extends Packet<?>> extends RandomTest {

    private static final Kryo KRYO = new Kryo();
    private final Class<T> clazz;

    protected T before;
    protected T after;

    public PacketTest(@NotNull final Class<T> clazz) {
        this.clazz = clazz;
    }

    public PacketTest(@NotNull final Class<T> clazz, @NotNull final Class<?>... registers) {
        this.clazz = clazz;

        for (final Class<?> register : registers) {
            KRYO.register(register);
        }
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
        final Output output = new Output(1024);

        System.out.println(this.before.toString());

        this.before.write(KRYO, output);
        this.after.read(KRYO, new Input(output.getBuffer()));
    }

    public abstract void equals();
}
