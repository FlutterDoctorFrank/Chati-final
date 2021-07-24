package model.communication;

import model.MessageBundle;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.communication.message.VoiceMessage;
import model.context.Context;
import model.context.spatial.SpatialContext;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommunicationHandler {
    private static final String CHAT_COMMAND = "\\\\\\s.+";
    private static final String WHISPER_MESSAGE = "\\\\\\w\\s.+";
    private static final String ROOM_MESSAGE = "!\\s.+";
    private static final String WORLD_MESSAGE = "!!\\s.+";

    private final User communicator;

    public CommunicationHandler(User user) {
        this.communicator = user;
    }

    public void handleTextMessage(String message) {
        if (message.matches(CHAT_COMMAND)) {
            String[] commandMessage = message.split("\\s");
            switch (commandMessage[0]) {
                case WHISPER_MESSAGE:
                    String username = Pattern.compile("\\w").matcher(commandMessage[0]).group(1);
                    handleWhisperMessage(username, commandMessage[1]);
                    break;
                case ROOM_MESSAGE:
                    handleRoomMessage(commandMessage[1]);
                    break;
                case WORLD_MESSAGE:
                    handleWorldMessage(commandMessage[1]);
                    break;
                default:
                    String errorMessage = "Dieser Chatbefehl existiert nicht.";
                    MessageBundle messageBundle = new MessageBundle("Dieser Chatbefehl existiert nicht.");
                    TextMessage info = new TextMessage(messageBundle);
                    // Send message
            }
        } else {
            handleStandardMessage(message);
        }
    }

    public void handleVoiceMessage(byte[] voicedata) {
        SpatialContext communicationContext = communicator.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.VOICE)
            || communicationContext.isMuted(communicator)) {
            return;
        }
        Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(communicator);
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        receivers.remove(communicator.getUserID());
        VoiceMessage voiceMessage = new VoiceMessage(communicator, voicedata);
        // Send voicepacket
    }

    private void handleStandardMessage(String message) {
        SpatialContext communicationContext = communicator.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.TEXT)) {
            MessageBundle messageBundle = new MessageBundle("In diesem Bereich ist keine Kommunikation in Schriftform möglich.");
            TextMessage info = new TextMessage(messageBundle);
            // Send message
            return;
        }
        if (communicationContext.isMuted(communicator)) {
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
            TextMessage info = new TextMessage(messageBundle);
            // Send message
            return;
        }
        Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(communicator);
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.STANDARD);
        // Send message to receivers
    }

    private void handleWhisperMessage(String username, String message) {
        User receiver;
        try {
            receiver = UserAccountManager.getInstance().getUser(username);
        } catch (UserNotFoundException e) {
            MessageBundle messageBundle = new MessageBundle("Es existiert kein Benutzer mit diesem Namen");
            TextMessage info = new TextMessage(messageBundle);
            // send Info message
            return;
        }
        SpatialContext communicationContext = communicator.getLocation().getArea();
        Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(communicator);
        Context commonContext = communicationContext.lastCommonAncestor(receiver.getLocation().getArea());
        if (!communicableUsers.containsKey(receiver.getUserID())
                && !communicator.hasPermission(commonContext, Permission.CONTACT_USER)
                && !receiver.hasPermission(commonContext, Permission.CONTACT_USER)
                || communicator.isIgnoring(receiver) || receiver.isIgnoring(communicator)) {
            MessageBundle messageBundle = new MessageBundle("Eine Flüsterkommunikation mit diesem Benutzer ist nicht möglich.");
            TextMessage info = new TextMessage(messageBundle);
            // send Info message
            return;
        }
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.WHISPER);
        // send message
    }

    private void handleRoomMessage(String message) {
        SpatialContext communicationRoom = communicator.getLocation().getRoom();
        if (!communicator.hasPermission(communicationRoom, Permission.CONTACT_CONTEXT)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
            TextMessage info = new TextMessage(messageBundle);
            // send Info message
            return;
        }
        Map<UUID, User> communicableUsers = communicationRoom.getContainedUsers();
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.ROOM);
        // Send message
    }

    private void handleWorldMessage(String message) {
        SpatialContext communicationWorld = communicator.getWorld();
        if (!communicator.hasPermission(communicationWorld, Permission.CONTACT_CONTEXT)) {
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
            TextMessage info = new TextMessage(messageBundle);
            // send Info message
            return;
        }
        Map<UUID, User> communicableUsers = communicationWorld.getContainedUsers();
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.WORLD);
        // Send message
    }

    private Map<UUID, User> filterIgnoredUsers(User communicator, Map<UUID, User> communicableUsers) {
        return communicableUsers.entrySet().stream().filter(entry -> {
            User communicableUser = entry.getValue();
            return !communicator.isIgnoring(communicableUser) && !communicableUser.isIgnoring(communicator);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}