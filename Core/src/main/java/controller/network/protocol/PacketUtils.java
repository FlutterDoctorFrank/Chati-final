package controller.network.protocol;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class PacketUtils {

    public static void writeEnum(@NotNull final Output output, @NotNull final Enum<?> object) {
        output.writeInt(object.ordinal(), true);
    }

    public static <T extends Enum<T>> T readEnum(@NotNull final Input input, @NotNull final Class<T> clazz) {
        final T object = clazz.getEnumConstants()[input.readInt(true)];

        if (object == null) {
            throw new IllegalStateException("Encountered unknown Enum<" + clazz.getSimpleName() + "> ordinal during deserialization");
        }

        return object;
    }

    public static void writeUniqueId(@NotNull final Output output, @Nullable final UUID uniqueId) {
        if (uniqueId != null) {
            output.writeBoolean(true);
            output.writeLong(uniqueId.getMostSignificantBits(), false);
            output.writeLong(uniqueId.getLeastSignificantBits(), false);
        } else {
            output.writeBoolean(false);
        }
    }

    public static @Nullable UUID readUniqueId(@NotNull final Input input) {
        if (input.readBoolean()) {
            return new UUID(input.readLong(false), input.readLong(false));
        }

        return null;
    }
}
