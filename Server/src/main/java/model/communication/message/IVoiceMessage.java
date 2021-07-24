package model.communication.message;

public interface IVoiceMessage extends IMessage {
    public byte[] getVoiceData();
}
