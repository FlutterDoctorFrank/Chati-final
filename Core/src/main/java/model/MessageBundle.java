package model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Eine Klasse, welche eine übersetzbare Nachricht repräsentiert.
 */
public class MessageBundle {

    private final String messageKey;
    private Object[] arguments;

    public MessageBundle(@NotNull final String messageKey) {
        this.messageKey = messageKey;
        this.arguments = new Object[0];
    }

    public MessageBundle(@NotNull final String messageKey, @NotNull final Object... arguments) {
        this.messageKey = messageKey;
        this.arguments = arguments;
    }

    /**
     * Gibt den Nachrichten-Schlüssel der übersetzbaren Nachricht zurück.
     * @return Der Nachrichten-Schlüssel.
     */
    public @NotNull String getMessageKey() {
        return messageKey;
    }

    /**
     * Gibt die Argumente zum Formatieren der Nachricht zurück.
     * @return Die Argumente der Nachricht.
     */
    public @NotNull Object[] getArguments() {
        return arguments;
    }

    /**
     * Sets die Argumente zum Formatieren der Nachricht.
     * @param arguments die neuen Argumente.
     */
    public void setArguments(@NotNull final Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{key='" + this.messageKey +
                "', arguments=" + Arrays.toString(this.arguments) + "}";
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final MessageBundle other = (MessageBundle) object;
        return this.messageKey.equals(other.messageKey) && Arrays.equals(this.arguments, other.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(this.messageKey);

        result = 31 * result + Arrays.hashCode(this.arguments);

        return result;
    }
}
