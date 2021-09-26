package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketInUserManage.Action;
import model.user.AdministrativeAction;
import org.junit.Assert;
import org.junit.Test;
import java.util.UUID;

public class PacketInUserManageTest extends PacketClientTest {

    public PacketInUserManageTest() {
        super(SendAction.USER_MANAGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsPackagingTest() {
        this.getPacket(PacketInUserManage.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTypesPackagingTest() {
        this.getPacket(PacketInUserManage.class, new Object(), new Object(), new Object());
    }

    @Test
    public void interactPackagingTest() {
        final UUID userId = randomUniqueId();
        final AdministrativeAction action = randomEnum(AdministrativeAction.class);
        final String[] arguments = new String[]{randomString()};

        final PacketInUserManage packet = this.getPacket(PacketInUserManage.class, userId, action, arguments[0]);

        Assert.assertEquals(userId, packet.getUserId());
        Assert.assertEquals(arguments.length, packet.getArguments().length);
        Assert.assertArrayEquals(arguments, packet.getArguments());

        try {
            final Action correspond = Action.valueOf(action.name());

            Assert.assertEquals(correspond, packet.getAction());
        } catch (IllegalArgumentException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
