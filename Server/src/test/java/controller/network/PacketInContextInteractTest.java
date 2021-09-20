package controller.network;

import controller.network.mock.MockIUser;
import controller.network.protocol.PacketInContextInteract;
import model.context.spatial.IWorld;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketInContextInteractTest extends PacketServerTest {

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketInContextInteract packet = new PacketInContextInteract(randomContextId());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not interact with context while not logged in"));

        final MockIUser user = this.login();

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not interact with context while not in a world"));
        Assert.assertFalse(user.called("interact"));
    }

    @Test
    public void handleIllegalInteractTest() {
        final PacketInContextInteract packet = new PacketInContextInteract(randomContextId());
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));
        user.interact(true, false);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "unknown context"));
        Assert.assertTrue(user.called("interact"));

        user.reset();
        user.interact(false, true);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "illegal interaction"));
        Assert.assertTrue(user.called("interact"));
    }

    @Test
    public void handleCorrectOptionTest() {
        final PacketInContextInteract packet = new PacketInContextInteract(randomContextId());
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(user.called("interact"));
    }
}
