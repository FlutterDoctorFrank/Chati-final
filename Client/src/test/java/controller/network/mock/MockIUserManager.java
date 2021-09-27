package controller.network.mock;

import model.context.ContextID;
import model.exception.ContextNotFoundException;
import model.exception.UserNotFoundException;
import model.user.Avatar;
import model.user.IUserController;
import model.user.IUserManagerController;
import model.user.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;
import util.RandomTest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MockIUserManager implements IUserManagerController {

    private final Set<String> calls;

    private MockIInternUser intern;

    public MockIUserManager() {
        this.calls = new HashSet<>();
    }

    @Override
    public void login(@NotNull final UUID userId, @NotNull final String username,
                      @NotNull final Status status, @NotNull final Avatar avatar) {
        this.calls.add("login");
        this.intern = new MockIInternUser(userId);
    }

    @Override
    public void logout() {
        this.calls.add("logout");
        this.intern = null;
    }

    @Override
    public void updateWorlds(@NotNull final Map<ContextID, String> worlds) {
        this.calls.add("update-worlds");
    }

    private boolean unknownWorld;
    private boolean alreadyPublic;

    @Override
    public void updatePublicRoom(@NotNull final ContextID worldId, @NotNull final ContextID roomId,
                                 @NotNull final String roomName) {
        this.calls.add("update-public-room");

        if (this.alreadyPublic) {
            throw new IllegalStateException("Mocked IllegalStateException");
        }
    }

    @Override
    public void updatePrivateRooms(@NotNull final ContextID worldId, @NotNull final Map<ContextID, String> privateRooms) throws ContextNotFoundException {
        this.calls.add("update-private-rooms");

        if (this.unknownWorld) {
            throw new ContextNotFoundException("Mocked ContextNotFoundException", worldId);
        }
    }

    public void updateRooms(final boolean throwAlready, final boolean throwUnknown) {
        this.alreadyPublic = throwAlready;
        this.unknownWorld = throwUnknown;
    }

    @Override
    public void addExternUser(@NotNull final UUID userId, @NotNull final String username,
                              @NotNull final Status status, @Nullable final Avatar avatar) {
        this.calls.add("add-extern-user");
        this.unknownExtern = false;
    }

    @Override
    public void removeExternUser(@NotNull final UUID userId) throws UserNotFoundException {
        this.calls.add("remove-extern-user");

        if (this.unknownExtern) {
            throw new UserNotFoundException("Mocked UserNotFoundException", userId);
        }
    }

    @Override
    public @NotNull MockIInternUser getInternUserController() {
        return this.intern;
    }

    private boolean unknownExtern;

    @Override
    public @NotNull IUserController getExternUserController(@NotNull final UUID userId) throws UserNotFoundException {
        this.calls.add("get-extern-user-controller");

        if (this.unknownExtern) {
            throw new UserNotFoundException("Mocked UserNotFoundException", userId);
        }

        return Mockito.mock(IUserController.class);
    }

    public void getExternUserController(final boolean throwUnknown) {
        this.unknownExtern = throwUnknown;
    }

    @Override
    public @NotNull Map<UUID, IUserController> getExternUsers() {
        return Collections.singletonMap(RandomTest.randomUniqueId(), Mockito.mock(IUserController.class));
    }

    public boolean called(@NotNull final String method) {
        return this.calls.contains(method);
    }

    public void reset() {
        this.calls.clear();
    }
}
