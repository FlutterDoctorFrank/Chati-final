package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketMenuOption;
import model.context.ContextID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketMenuOptionTest extends PacketClientTest {

    public PacketMenuOptionTest() {
        super(SendAction.MENU_OPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsPackagingTest() {
        this.getPacket(PacketMenuOption.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTypesPackagingTest() {
        this.getPacket(PacketMenuOption.class, new Object(), new Object(), new Object());
    }

    @Test
    public void messagePackagingTest() {
        final ContextID objectId = randomContextId();
        final String[] arguments = new String[]{randomString()};
        final int option = randomInt(3);

        final PacketMenuOption packet = this.getPacket(PacketMenuOption.class, objectId, arguments, option);

        Assert.assertEquals(objectId, packet.getContextId());
        Assert.assertEquals(arguments.length, packet.getArguments().length);
        Assert.assertArrayEquals(arguments, packet.getArguments());
        Assert.assertEquals(option, packet.getOption());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketMenuOption packet = Mockito.mock(PacketMenuOption.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive menu option while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive menu option while user is not in a world"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketMenuOption packet = Mockito.mock(PacketMenuOption.class);

        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.getArguments()).thenReturn(new String[]{randomString()});
        Mockito.when(packet.getOption()).thenReturn(randomInt(5));

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.view.called("menu-action-response"));
    }
}
