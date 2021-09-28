package controller.network.mock;

import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.ViewControllerInterface;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MockViewController implements ViewControllerInterface {

    private final Set<String> calls;

    public MockViewController() {
        this.calls = new HashSet<>();
    }

    @Override
    public void setSender(@Nullable final ServerSender sender) {
        this.calls.add("set-sender");
    }

    @Override
    public void registrationResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("registration-response");
    }

    @Override
    public void loginResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("login-response");
    }

    @Override
    public void passwordChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("password-change-response");
    }

    @Override
    public void deleteAccountResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("delete-account-response");
    }

    @Override
    public void avatarChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("avatar-change-response");
    }

    @Override
    public void createWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("create-world-response");
    }

    @Override
    public void deleteWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("delete-world-response");
    }

    @Override
    public void joinWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("join-world-response");
    }

    @Override
    public void showTypingUser(@NotNull final UUID userId) {
        this.calls.add("show-typing-user");
    }

    private boolean unknownSender;

    @Override
    public void showChatMessage(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                                @NotNull final MessageType messageType, @NotNull final String message,
                                final byte[] imageData, @Nullable final String imageName) throws UserNotFoundException {
        this.calls.add("show-chat-message");

        if (this.unknownSender) {
            throw new UserNotFoundException("Mocked UserNotFoundException", userId);
        }
    }

    public void showChatMessage(final boolean throwUnknown) {
        this.unknownSender = throwUnknown;
    }

    @Override
    public void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle) {
        this.calls.add("show-info-message");
    }

    @Override
    public void playVoiceData(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                              final byte[] voiceData) throws UserNotFoundException {
        this.calls.add("play-voice-data");

        if (this.unknownSender) {
            throw new UserNotFoundException("Mocked UserNotFoundException", userId);
        }
    }

    public void playVoiceData(final boolean throwUnknown) {
        this.unknownSender = throwUnknown;
    }

    @Override
    public void playMusicData(@NotNull final LocalDateTime timestamp, final byte[] musicData,
                              final float position, final int seconds) {
        this.calls.add("play-music-data");
    }

    @Override
    public void openMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu menu) {
        this.calls.add("open-menu");
    }

    @Override
    public void closeMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu menu) {
        this.calls.add("close-menu");
    }

    @Override
    public void menuActionResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        this.calls.add("menu-action-response");
    }

    @Override
    public void logout() {
        this.calls.add("logout");
    }

    @Override
    public void leaveWorld() {
        this.calls.add("leave-world");
    }

    public boolean called(@NotNull final String method) {
        return this.calls.contains(method);
    }

    public void reset() {
        this.calls.clear();
    }
}
