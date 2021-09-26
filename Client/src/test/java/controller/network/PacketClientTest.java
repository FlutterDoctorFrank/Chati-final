package controller.network;

import com.esotericsoftware.kryonet.Connection;
import controller.network.ServerSender.SendAction;
import controller.network.mock.MockHandler;
import controller.network.protocol.Packet;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.mockito.Mockito;
import util.RandomTest;

public abstract class PacketClientTest extends RandomTest {

    private final SendAction action;

    protected ServerConnection connection;
    protected MockHandler handler;

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
        this.handler = new MockHandler("chati.network");
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
