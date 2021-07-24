package model.communication.message;

import model.user.User;

public class VoiceMessage extends Message implements IVoiceMessage {
    private byte[] voicedata;

    public VoiceMessage(User sender, byte[] voicedata) {
        super(sender);
        this.voicedata = voicedata;
    }

    @Override
    public byte[] getVoiceData() {
        return voicedata;
    }
}
