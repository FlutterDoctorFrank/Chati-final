package controller.network;

import controller.network.mock.MockIUser;
import controller.network.protocol.PacketMenuOption;
import model.context.spatial.IWorld;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketMenuOptionTest extends PacketServerTest {

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketMenuOption packet = new PacketMenuOption(randomContextId(), new String[0], randomInt(3));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not interact with menu while not logged in"));

        final MockIUser user = this.login();

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not interact with menu while not in a world"));
        Assert.assertFalse(user.called("execute-option"));
    }

    @Test
    public void handleIllegalOptionTest() {
        final PacketMenuOption packet = Mockito.mock(PacketMenuOption.class);
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));
        user.executeOption(true, false);
        Mockito.when(packet.getContextId()).thenReturn(randomContextId());
        Mockito.when(packet.getOption()).thenReturn(randomInt(3));
        Mockito.when(packet.getArguments()).thenReturn(new String[0]);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "unknown object"));
        Assert.assertTrue(user.called("execute-option"));

        user.reset();
        user.executeOption(false, true);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "illegal context-interaction"));
        Assert.assertTrue(user.called("execute-option"));
        Mockito.when(packet.getOption()).thenReturn(-randomInt(1, 3));

        user.reset();
        user.executeOption(false, false);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("execute-option"));
    }

    @Test
    public void handleCorrectOptionTest() {
        final PacketMenuOption packet = new PacketMenuOption(randomContextId(), new String[0], randomInt(3));
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("execute-option"));
    }
}
