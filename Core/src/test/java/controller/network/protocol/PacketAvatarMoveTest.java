package controller.network.protocol;

import controller.network.protocol.PacketAvatarMove.AvatarAction;
import org.junit.Assert;
import org.junit.Test;

public class PacketAvatarMoveTest extends PacketTest<PacketAvatarMove> {

    public PacketAvatarMoveTest() {
        super(PacketAvatarMove.class);
    }

    @Test
    public void clientSerializationTest() {
        this.before = new PacketAvatarMove(randomInt(), randomInt());

        this.serialize();
        this.equals();
    }

    @Test
    public void serverSerializationTest() {
        this.before = new PacketAvatarMove(randomEnum(AvatarAction.class), randomUniqueId(), randomInt(), randomInt());

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

        // Vergleiche Position:
        Assert.assertEquals(this.before.getPosX(), this.after.getPosX());
        Assert.assertEquals(this.before.getPosY(), this.after.getPosY());
    }
}
