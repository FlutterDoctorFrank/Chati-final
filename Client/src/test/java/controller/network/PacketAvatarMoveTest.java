package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import model.context.spatial.Direction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.logging.Level;

public class PacketAvatarMoveTest extends PacketClientTest {

    public PacketAvatarMoveTest() {
        super(SendAction.AVATAR_MOVE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsPackagingTest() {
        this.getPacket(PacketAvatarMove.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTypesPackagingTest() {
        this.getPacket(PacketAvatarMove.class, new Object(), new Object(), new Object(), new Object());
    }

    @Test
    public void movePackagingTest() {
        final float posX = randomFloat();
        final float posY = randomFloat();
        final boolean sprinting = randomBoolean();
        final Direction direction = randomEnum(Direction.class);

        final PacketAvatarMove packet = this.getPacket(PacketAvatarMove.class, posX, posY, sprinting, direction);

        Assert.assertEquals(AvatarAction.MOVE_AVATAR, packet.getAction());
        Assert.assertEquals(posX, packet.getPosX(), 0.0f);
        Assert.assertEquals(posY, packet.getPosY(), 0.0f);
        Assert.assertEquals(sprinting, packet.isSprinting());
        Assert.assertNotNull(packet.getDirection());
        Assert.assertEquals(direction, packet.getDirection());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketAvatarMove packet = Mockito.mock(PacketAvatarMove.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not move avatar while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not move avatar while user is not in a world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketAvatarMove packet = Mockito.mock(PacketAvatarMove.class);

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "User-ID can not be null"));
        Mockito.when(packet.getUserId()).thenReturn(this.intern.getUserId());
        Mockito.when(packet.getAction()).thenReturn(AvatarAction.SPAWN_AVATAR);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing direction for avatar spawn"));
        Mockito.when(packet.getAction()).thenReturn(AvatarAction.MOVE_AVATAR);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Missing direction for avatar move"));
        Mockito.when(packet.getAction()).thenReturn(AvatarAction.REMOVE_AVATAR);
        Mockito.when(packet.getUserId()).thenReturn(randomUniqueId());

        this.handler.reset();
        this.manager.getExternUserController(true);
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to move unknown user"));
    }

    @Test
    public void handleCorrectActionTest() {
        final PacketAvatarMove packet = Mockito.mock(PacketAvatarMove.class);

        this.login();
        this.joinWorld();

        Mockito.when(packet.getUserId()).thenReturn(this.intern.getUserId());
        Mockito.when(packet.getDirection()).thenReturn(randomEnum(Direction.class));
        Mockito.when(packet.getAction()).thenReturn(AvatarAction.SPAWN_AVATAR);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("set-movable"));
        Assert.assertTrue(this.intern.called("set-location"));
        Mockito.when(packet.getAction()).thenReturn(AvatarAction.MOVE_AVATAR);

        this.intern.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("set-movable"));
        Assert.assertTrue(this.intern.called("set-location"));
        Mockito.when(packet.getAction()).thenReturn(AvatarAction.REMOVE_AVATAR);

        this.intern.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.intern.called("leave-room"));
    }
}
