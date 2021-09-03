package controller.network.protocol;

import controller.network.protocol.PacketAvatarMove.AvatarAction;
import controller.network.protocol.mock.MockPacketListener;
import model.context.spatial.Direction;
import org.junit.Assert;
import org.junit.Test;

public class PacketAvatarMoveTest extends PacketTest<PacketAvatarMove> {

    public PacketAvatarMoveTest() {
        super(PacketAvatarMove.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListener listener = new MockPacketListener();

        this.before = new PacketAvatarMove(randomUniqueId());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketAvatarMove.class));
    }

    @Test
    public void clientSerializationTest() {
        this.before = new PacketAvatarMove(randomEnum(Direction.class), randomFloat(), randomFloat(), randomBoolean());

        this.serialize();
        this.equals();
    }

    @Test
    public void serverSerializationTest() {
        this.before = new PacketAvatarMove(randomEnum(AvatarAction.class), randomUniqueId(), randomEnum(Direction.class),
                randomFloat(), randomFloat(), randomBoolean(), randomBoolean());

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche AvatarAction:
        Assert.assertEquals(this.before.getAction(), this.after.getAction());

        // Vergleiche UserId:
        if (this.before.getUserId() != null) {
            Assert.assertNotNull(this.after.getUserId());
            Assert.assertEquals(this.before.getUserId(), this.after.getUserId());
        } else {
            Assert.assertNull(this.after.getUserId());
        }

        // Vergleiche Direction:
        if (this.before.getDirection() != null) {
            Assert.assertNotNull(this.after.getDirection());
            Assert.assertEquals(this.before.getDirection(), this.after.getDirection());
        } else {
            Assert.assertNull(this.after.getDirection());
        }

        // Vergleiche Position:
        Assert.assertEquals(this.before.getPosX(), this.after.getPosX(), 0.0f);
        Assert.assertEquals(this.before.getPosY(), this.after.getPosY(), 0.0f);
        Assert.assertEquals(this.before.isSprinting(), this.after.isSprinting());
        Assert.assertEquals(this.before.isMovable(), this.after.isMovable());
    }
}
