package model.communication.message;

import model.MessageBundle;
import model.user.User;

/**
 * Eine Klasse, welche Textnachrichten von Benutzern repräsentiert.
 */
public class TextMessage extends Message implements ITextMessage {

    /** Der Text der Nachricht. */
    private String textMessage;

    /** Der Schlüssel einer übersetzbaren Nachricht. Wird nur für TextNachrichten verwendet, die vom System generiert
     * werden und den Typ {@link MessageType#INFO} besitzen. */
    private MessageBundle messageBundle;

    /** Der Typ dieser Nachricht. Bestimmt, wie die Nachricht beim Benutzer angezeigt werden soll. */
    private MessageType messageType;

    /**
     * Erzeugt eine neue Instanz der Textnachricht. Wird verwendet, um von Benutzern gesendete Nachrichten zu erzeugen.
     * @param sender Der Sender dieser Nachricht.
     * @param textMessage Der Text dieser Nachricht.
     * @param messageType Der Typ dieser Nachricht.
     */
    public TextMessage(User sender, String textMessage, MessageType messageType) {
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
    public TextMessage(MessageBundle messageBundle) {
        super(null);
        this.textMessage = null;
        this.messageBundle = messageBundle;
        this.messageType = MessageType.INFO;
    }

    @Override
    public String getTextMessage() {
        return textMessage;
    }

    @Override
    public MessageBundle getMessageBundle() {
        return messageBundle;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }
}
