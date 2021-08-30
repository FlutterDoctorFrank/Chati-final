package model.communication.message;

import model.MessageBundle;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Eine Klasse, welche Textnachrichten von Benutzern repräsentiert.
 */
public class TextMessage extends Message implements ITextMessage {

    /** Der Text der Nachricht. */
    private final String textMessage;

    /** Der Schlüssel einer übersetzbaren Nachricht. Wird nur für TextNachrichten verwendet, die vom System generiert
     * werden und den Typ {@link MessageType#INFO} besitzen. */
    private final MessageBundle messageBundle;

    /** Der Typ dieser Nachricht. Bestimmt, wie die Nachricht beim Benutzer angezeigt werden soll. */
    private final MessageType messageType;

    /**
     * Erzeugt eine neue Instanz der Textnachricht. Wird verwendet, um von Benutzern gesendete Nachrichten zu erzeugen.
     * @param sender Der Sender dieser Nachricht.
     * @param textMessage Der Text dieser Nachricht.
     * @param messageType Der Typ dieser Nachricht.
     */
    public TextMessage(@NotNull final User sender, @NotNull final String textMessage, @NotNull final MessageType messageType) {
        super(sender);
        this.textMessage = textMessage;
        this.messageBundle = null;
        this.messageType = messageType;
    }

    /**
     * Erzeugt eine neue Instanz der Textnachricht. Wird verwendet, um vom System generierte Nachrichten vom Typ
     * {@link MessageType#INFO} zu erzeugen.
     * @param messageBundle Der Nachrichtenschlüssel der übersetzbaren Nachricht und deren Argumente.
     */
    public TextMessage(@NotNull final MessageBundle messageBundle) {
        super(null);
        this.textMessage = null;
        this.messageBundle = messageBundle;
        this.messageType = MessageType.INFO;
    }

    public TextMessage(@NotNull final String key, @NotNull final Object... arguments) {
        super(null);
        this.textMessage = null;
        this.messageBundle = new MessageBundle(key, arguments);
        this.messageType = MessageType.INFO;
    }

    @Override
    public @Nullable String getTextMessage() {
        return textMessage;
    }

    @Override
    public @Nullable MessageBundle getMessageBundle() {
        return messageBundle;
    }

    @Override
    public @NotNull MessageType getMessageType() {
        return messageType;
    }
}