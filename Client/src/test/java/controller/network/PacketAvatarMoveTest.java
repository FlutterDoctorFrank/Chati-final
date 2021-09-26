package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import model.context.spatial.Direction;
import org.junit.Assert;
import org.junit.Test;

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
}
