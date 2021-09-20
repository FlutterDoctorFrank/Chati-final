package controller.network;

import controller.network.mock.MockIUser;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketInUserManage.Action;
import model.context.spatial.IWorld;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketInUserManageTest extends PacketServerTest {

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketInUserManage packet = new PacketInUserManage(randomUniqueId(), randomEnum(Action.class), new String[0]);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not manage user while not logged in"));
    }

    @Test
    public void handleIllegalActionTest() {
        final PacketInUserManage packet = new PacketInUserManage(randomUniqueId(), randomEnum(Action.class), new String[0]);
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));
        user.executeAdministrativeAction(true, false, false);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "unknown user"));
        Assert.assertTrue(user.called("execute-administrative-action"));

        user.reset();
        user.executeAdministrativeAction(false, true, false);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "illegal administrative action"));
        Assert.assertTrue(user.called("execute-administrative-action"));

        user.reset();
        user.executeAdministrativeAction(false, false, true);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.INFO, "missing permission"));
        Assert.assertTrue(user.called("execute-administrative-action"));
    }

    @Test
    public void handleCorrectActionTest() {
        final PacketInUserManage packet = new PacketInUserManage(randomUniqueId(), randomEnum(Action.class), new String[0]);
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("execute-administrative-action"));
    }
}
