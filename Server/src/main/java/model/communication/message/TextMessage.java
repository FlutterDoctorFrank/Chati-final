package model.communication.message;

import model.MessageBundle;
import model.user.User;

public class TextMessage extends Message implements ITextMessage {
    private String textMessage;
    private MessageBundle messageBundle;
    private MessageType messageType;

    public TextMessage(User sender, String textMessage, MessageType messageType) {
        super(sender);
        this.textMessage = textMessage;
        this.messageBundle = null;
        this.messageType = messageType;
    }

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
