package model.communication.message;

import model.MessageBundle;

public interface ITextMessage extends IMessage {
    public String getTextMessage();
    public MessageBundle getMessageBundle();
    public MessageType getMessageType();
}
