package model;

public class MessageBundle {
    private String messageKey;
    private Object[] arguments;

    public MessageBundle(String messageKey) {
        this.messageKey = messageKey;
        this.arguments = new Object[0];
    }

    public MessageBundle(String messageKey, Object[] arguments) {
        this.messageKey = messageKey;
        this.arguments = arguments;
    }
}
