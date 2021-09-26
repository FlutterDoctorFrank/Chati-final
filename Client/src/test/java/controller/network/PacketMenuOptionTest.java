package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketMenuOption;
import model.context.ContextID;
import org.junit.Assert;
import org.junit.Test;

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
}
