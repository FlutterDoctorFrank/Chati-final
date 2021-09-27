package controller.network;

import controller.network.protocol.PacketOutContextRole;
import model.role.Role;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketOutContextRoleTest extends PacketClientTest {

    public PacketOutContextRoleTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutContextRole packet = Mockito.mock(PacketOutContextRole.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive context role while user is not logged in"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketOutContextRole packet = Mockito.mock(PacketOutContextRole.class);

        Mockito.when(packet.getUserId()).thenReturn(randomUniqueId());

        this.login();
        this.manager.getExternUserController(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send context-role of unknown user"));
        Mockito.when(packet.getUserId()).thenReturn(this.intern.getUserId());
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.getRoles()).thenReturn(new Role[0]);

        this.intern.setContext(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send context role for unknown context"));
        Assert.assertTrue(this.intern.called("set-roles"));
    }
}
