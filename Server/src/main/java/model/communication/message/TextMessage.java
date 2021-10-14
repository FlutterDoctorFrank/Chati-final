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

    /** Der Bildanhang der Nachricht. */
    private final byte[] imageData;

    /** Der Name des Bildanhangs. */
    private final String imageName;

    /** Der Schlüssel einer übersetzbaren Nachricht. Wird nur für TextNachrichten verwendet, die vom System generiert
     * werden und den Typ {@link MessageType#INFO} besitzen. */
    private final MessageBundle messageBundle;

    /** Der Typ dieser Nachricht. Bestimmt, wie die Nachricht beim Benutzer angezeigt werden soll. */
    private final MessageType messageType;

    /**
     * Erzeugt eine neue Instanz der Textnachricht.
     * @param sender Der Sender dieser Nachricht.
     * @param textMessage Der Text dieser Nachricht.
     * @param imageData Die Daten des Bildanhangs dieser Nachricht.
     * @param imageName Der Name des Bildanhangs dieser Nachricht.
     * @param messageBundle Die übersetzbare Nachricht und deren Argumente.
     * @param messageType Der Typ dieser Nachricht.
     */
    public TextMessage(@Nullable final User sender, @Nullable final String textMessage, final byte[] imageData,
                       @Nullable final String imageName, @Nullable MessageBundle messageBundle,
                       @NotNull final MessageType messageType) {
        super(sender);
        this.textMessage = textMessage;
        this.imageData = imageData;
        this.imageName = imageName;
        this.messageBundle = messageBundle;
        this.messageType = messageType;
    }

    /**
     * Erzeugt eine neue Instanz der Textnachricht. Wird verwendet, um von Benutzern gesendete Nachrichten zu erzeugen.
     * @param sender Der Sender dieser Nachricht.
     * @param textMessage Der Text dieser Nachricht.
     * @param imageData Daten des Bildanhangs.
     * @param imageName Name des Bildanhangs.
     * @param messageType Der Typ dieser Nachricht.
     */
    public TextMessage(@NotNull final User sender, @NotNull final String textMessage, final byte[] imageData,
                       @Nullable final String imageName, @NotNull final MessageType messageType) {
        this(sender, textMessage, imageData, imageName, null, messageType);
    }

    /**
     * Erzeugt eine neue Instanz der Textnachricht. Wird verwendet, um vom System generierte Nachrichten vom Typ
     * {@link MessageType#INFO} zu erzeugen.
     * @param messageBundle Der Nachrichtenschlüssel der übersetzbaren Nachricht und deren Argumente.
     */
    public TextMessage(@NotNull final MessageBundle messageBundle) {
        this(null, null, new byte[0], null, messageBundle, MessageType.INFO);
    }

    /**
     * Erzeugt eine neue Instanz der Textnachricht. Wird verwendet, um vom System generierte Nachrichten vom Typ
     * {@link MessageType#INFO} zu erzeugen.
     * @param key Der Nachrichtenschlüssel der übersetzbaren Nachricht.
     * @param arguments Die Argumente der übersetzbaren Nachricht.
     */
    public TextMessage(@NotNull final String key, @NotNull final Object... arguments) {
        this(new MessageBundle(key, arguments));
    }

    @Override
    public @Nullable String getTextMessage() {
        return textMessage;
    }

    @Override
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public @Nullable String getImageName() {
        return imageName;
    }

    @Override
    public @Nullable MessageBundle getMessageBundle() {
        return messageBundle;
    }

    @Override
    public @NotNull MessageType getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return textMessage;
    }
}