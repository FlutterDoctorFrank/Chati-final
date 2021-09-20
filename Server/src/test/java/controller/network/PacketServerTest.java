package controller.network;

import com.esotericsoftware.kryonet.Connection;
import controller.network.ClientSender.SendAction;
import controller.network.mock.MockHandler;
import controller.network.mock.MockIUser;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketProfileAction;
import model.context.global.IGlobalContext;
import model.exception.IllegalAccountActionException;
import model.user.IUser;
import model.user.account.IUserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import util.RandomTest;
import java.util.Collections;

public abstract class PacketServerTest extends RandomTest {

    private final SendAction action;

    protected UserConnection connection;
    protected MockHandler handler;

    protected IUserAccountManager manager;
    protected IGlobalContext global;
    protected IUser user;

    public PacketServerTest() {
        this.action = null;
    }

    public PacketServerTest(@NotNull final SendAction action) {
        this.action = action;
    }

    @Before
    public void setup() {
        final ServerNetworkManager server = Mockito.mock(ServerNetworkManager.class);
        final Connection connection = Mockito.mock(Connection.class);

        this.manager = Mockito.mock(IUserAccountManager.class);
        this.global = Mockito.mock(IGlobalContext.class);
        this.user = Mockito.mock(IUser.class);

        Mockito.when(server.getAccountManager()).thenReturn(this.manager);
        Mockito.when(server.getGlobal()).thenReturn(this.global);
        Mockito.when(connection.getID()).thenReturn(randomInt(100));
        Mockito.when(connection.isConnected()).thenReturn(true);
        Mockito.when(connection.toString()).thenReturn("Mock Connection");

        this.connection = new UserConnection(server, connection);
        this.handler = new MockHandler("chati.network");
    }

    public @NotNull MockIUser login() {
        final MockIUser mock = new MockIUser(this.global);

        try {
            Mockito.when(this.manager.loginUser(Mockito.anyString(), Mockito.anyString(), Mockito.eq(this.connection))).thenReturn(mock);
            Mockito.when(this.global.getIWorlds()).thenReturn(Collections.emptyMap());
            Mockito.when(this.global.getContextId()).thenReturn(randomContextId());

            this.connection.handle(new PacketProfileAction(randomString(), randomString(), false));
        } catch (IllegalAccountActionException ex) {
            Assert.fail("Failed to login mocked user");
        }

        return mock;
    }

    public <T extends Packet<?>> @NotNull T getPacket(@NotNull final Class<T> clazz, @NotNull final Object object) {
        if (this.action == null) {
            throw new IllegalStateException("Missing SendAction: Can not get packet from non-existing SendAction");
        }

        final Packet<?> packet = this.action.getPacket(this.user, object);

        if (clazz.isInstance(packet)) {
            return clazz.cast(packet);
        }

        throw new IllegalStateException(String.format("Illegal SendAction: %s does not return class of type %s", this.action, clazz.getSimpleName()));
    }

    public <T extends Packet<?>> @NotNull T getPacket(@NotNull final SendAction action, @NotNull final Class<T> clazz,
                                                      @NotNull final Object object) {
        final Packet<?> packet = action.getPacket(this.user, object);

        if (clazz.isInstance(packet)) {
            return clazz.cast(packet);
        }

        throw new IllegalStateException(String.format("Illegal SendAction: %s does not return class of type %s", action, clazz.getSimpleName()));
    }
}
