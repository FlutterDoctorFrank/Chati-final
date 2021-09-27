package controller.network;

import controller.network.protocol.PacketOutMenuAction;
import model.context.spatial.ContextMenu;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketOutMenuActionTest extends PacketClientTest {

    public PacketOutMenuActionTest() {

    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketOutMenuAction packet = Mockito.mock(PacketOutMenuAction.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive menu action while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive menu action while user is not in a world"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketOutMenuAction packet = Mockito.mock(PacketOutMenuAction.class);

        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.getMenu()).thenReturn(randomEnum(ContextMenu.class));
        Mockito.when(packet.isOpen()).thenReturn(true);

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("set-movable"));
        Assert.assertTrue(this.view.called("open-menu"));
        Mockito.when(packet.isOpen()).thenReturn(false);

        this.view.reset();
        this.intern.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("set-movable"));
        Assert.assertTrue(this.view.called("close-menu"));
    }
}
