package model.communication.message;

import model.user.User;

import java.time.LocalDateTime;

public abstract class Message implements IMessage {
    private User sender;
    private LocalDateTime timestamp;

    public Message(User sender) {
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public User getSender() {
        return sender;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
