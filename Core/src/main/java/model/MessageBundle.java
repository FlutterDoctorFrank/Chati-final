package model;

import org.jetbrains.annotations.NotNull;

/**
 * Eine Klasse, welche eine übersetzbare Nachricht repräsentiert.
 */
public class MessageBundle {

    private final String messageKey;
    private final Object[] arguments;

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
}
