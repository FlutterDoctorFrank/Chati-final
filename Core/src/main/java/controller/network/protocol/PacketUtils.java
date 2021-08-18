package controller.network.protocol;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.MessageBundle;
import model.context.ContextID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class PacketUtils {

    public static void writeNullableEnum(@NotNull final Output output, @Nullable final Enum<?> object) {
        output.writeVarInt(object != null ? object.ordinal() + 1 : 0, true);
    }

    public static void writeEnum(@NotNull final Output output, @NotNull final Enum<?> object) {
        output.writeInt(object.ordinal(), true);
    }

    public static <T extends Enum<T>> @Nullable T readNullableEnum(@NotNull final Input input, @NotNull final Class<T> clazz) {
        final int ordinal = input.readVarInt(true);

        if (ordinal > 0) {
            final T object = clazz.getEnumConstants()[ordinal - 1];

            if (object == null) {
                throw new IllegalStateException("Encountered unknown Enum<" + clazz.getSimpleName() + "> ordinal during deserialization");
            }

            return object;
        }

        return null;
    }

    public static <T extends Enum<T>> T readEnum(@NotNull final Input input, @NotNull final Class<T> clazz) {
        final T object = clazz.getEnumConstants()[input.readVarInt(true)];

        if (object == null) {
            throw new IllegalStateException("Encountered unknown Enum<" + clazz.getSimpleName() + "> ordinal during deserialization");
        }

        return object;
    }

    public static void writeNullableBundle(@NotNull final Output output, @Nullable final MessageBundle bundle) {
        if (bundle != null) {
            output.writeBoolean(true);
            writeBundle(output, bundle);
        } else {
            output.writeBoolean(false);
        }
    }

    public static void writeBundle(@NotNull final Output output, @NotNull final MessageBundle bundle) {
        output.writeString(bundle.getMessageKey());
        output.writeVarInt(bundle.getArguments().length, true);

        for (final Object argument : bundle.getArguments()) {
            if (argument instanceof UUID) {
                output.writeVarInt(1, true);
                writeUniqueId(output, (UUID) argument);
            } else if (argument instanceof ContextID) {
                output.writeVarInt(2, true);
                writeContextId(output, (ContextID) argument);
            } else {
                output.writeVarInt(0, true);
                output.writeString(argument.toString());
            }
        }
    }

    public static @Nullable MessageBundle readNullableBundle(@NotNull final Input input) {
        if (input.readBoolean()) {
            return readBundle(input);
        }

        return null;
    }

    public static @NotNull MessageBundle readBundle(@NotNull final Input input) {
        final MessageBundle bundle = new MessageBundle(input.readString());
        final Object[] arguments = new Object[input.readVarInt(true)];

        for (int index = 0; index < arguments.length; index++) {
            switch (input.readVarInt(true)) {
                case 1:
                    arguments[index] = readUniqueId(input);
                    break;

                case 2:
                    arguments[index] = readContextId(input);
                    break;

                case 0:
                    arguments[index] = input.readString();
                    break;
            }
        }

        bundle.setArguments(arguments);

        return bundle;
    }

    public static void writeNullableContextId(@NotNull final Output output, @Nullable final ContextID contextId) {
        if (contextId != null) {
            output.writeBoolean(true);
            writeContextId(output, contextId);
        } else {
            output.writeBoolean(false);
        }
    }

    public static void writeContextId(@NotNull final Output output, @NotNull final ContextID contextId) {
        output.writeString(contextId.getId());
    }

    public static @Nullable ContextID readNullableContextId(@NotNull final Input input) {
        if (input.readBoolean()) {
            return readContextId(input);
        }

        return null;
    }

    public static @NotNull ContextID readContextId(@NotNull final Input input) {
        return new ContextID(input.readString());
    }

    public static void writeNullableUniqueId(@NotNull final Output output, @Nullable final UUID uniqueId) {
        if (uniqueId != null) {
            output.writeBoolean(true);
            writeUniqueId(output, uniqueId);
        } else {
            output.writeBoolean(false);
        }
    }

    public static void writeUniqueId(@NotNull final Output output, @NotNull final UUID uniqueId) {
        output.writeLong(uniqueId.getMostSignificantBits(), false);
        output.writeLong(uniqueId.getLeastSignificantBits(), false);
    }

    public static @Nullable UUID readNullableUniqueId(@NotNull final Input input) {
        if (input.readBoolean()) {
            return readUniqueId(input);
        }

        return null;
    }

    public static @NotNull UUID readUniqueId(@NotNull final Input input) {
        return new UUID(input.readLong(false), input.readLong(false));
    }
}
