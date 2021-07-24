package model.communication.message;

import model.user.IUser;

import java.time.LocalDateTime;

public interface IMessage {
    public IUser getSender();
    public LocalDateTime getTimestamp();
}
