package controller.network;

import controller.network.protocol.PacketOutCommunicable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.UUID;
import java.util.logging.Level;

public class PacketOutCommunicableTest extends PacketClientTest {

    public PacketOutCommunicableTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutCommunicable packet = Mockito.mock(PacketOutCommunicable.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive communicable users while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive communicable users while user is not in a world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketOutCommunicable packet = Mockito.mock(PacketOutCommunicable.class);

        Mockito.when(packet.getCommunicables()).thenReturn(new UUID[]{randomUniqueId()});

        this.login();
        this.joinWorld();
        this.manager.getExternUserController(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send unknown communicable user"));
    }
}
