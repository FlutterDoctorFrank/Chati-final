package controller.network;

import com.esotericsoftware.kryonet.Connection;
import controller.network.ServerSender.SendAction;
import controller.network.mock.MockHandler;
import controller.network.mock.MockIInternUser;
import controller.network.mock.MockIUserManager;
import controller.network.mock.MockViewController;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketProfileAction.Action;
import controller.network.protocol.PacketWorldAction;
import model.context.ContextID;
import model.user.Avatar;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import util.RandomTest;

public abstract class PacketClientTest extends RandomTest {

    private final SendAction action;

    protected ServerConnection connection;
    protected MockViewController view;
    protected MockIUserManager manager;
    protected MockIInternUser intern;
    protected MockHandler handler;

    protected ContextID world;

    public PacketClientTest() {
        this.action = null;
    }

    public PacketClientTest(@NotNull final SendAction action) {
        this.action = action;
    }

    @Before
    public void setup() {
        final ClientNetworkManager client = Mockito.mock(ClientNetworkManager.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.when(connection.getID()).thenReturn(randomInt(100));
        Mockito.when(connection.isConnected()).thenReturn(true);
        Mockito.when(connection.toString()).thenReturn("Mock Connection");

        this.connection = new ServerConnection(client);
        this.manager = new MockIUserManager();
        this.view = new MockViewController();
        this.handler = new MockHandler("chati.network");

        Mockito.when(client.getUserManager()).thenReturn(this.manager);
        Mockito.when(client.getView()).thenReturn(this.view);
    }

    public void login() {
        final PacketProfileAction packet = Mockito.mock(PacketProfileAction.class);

        Mockito.when(packet.getAction()).thenReturn(Action.LOGIN);
        Mockito.when(packet.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(packet.getName()).thenReturn(randomString());
        Mockito.when(packet.getAvatar()).thenReturn(randomEnum(Avatar.class));
        Mockito.when(packet.isSuccess()).thenReturn(true);

        try {
            this.connection.handle(packet);

            Assert.assertTrue(this.manager.called("login"));
            Assert.assertTrue(this.view.called("login-response"));

            this.intern = this.manager.getInternUserController();
            this.manager.reset();
        } catch (IllegalStateException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    public void joinWorld() {
        final PacketWorldAction packet = Mockito.mock(PacketWorldAction.class);

        Mockito.when(packet.getAction()).thenReturn(PacketWorldAction.Action.JOIN);
        Mockito.when(packet.getContextId()).thenReturn(this.world = randomContextId());
        Mockito.when(packet.isSuccess()).thenReturn(true);

        this.connection.handle(packet);

        Assert.assertTrue(this.intern.called("join-world"));
    }

    public <T extends Packet<?>> @NotNull T getPacket(@NotNull final Class<T> clazz, @NotNull final Object... objects) {
        if (this.action == null) {
            throw new IllegalStateException("Missing SendAction: Can not get packet from non-existing SendAction");
        }

        final Packet<?> packet = this.action.getPacket(objects);

        if (clazz.isInstance(packet)) {
            return clazz.cast(packet);
        }

        throw new IllegalStateException(String.format("Illegal SendAction: %s does not return class of type %s", this.action, clazz.getSimpleName()));
    }

    public <T extends Packet<?>> @NotNull T getPacket(@NotNull final SendAction action, @NotNull final Class<T> clazz,
                                                      @NotNull final Object... objects) {
        final Packet<?> packet = action.getPacket(objects);

        if (clazz.isInstance(packet)) {
            return clazz.cast(packet);
        }

        throw new IllegalStateException(String.format("Illegal SendAction: %s does not return class of type %s", action, clazz.getSimpleName()));
    }
}
