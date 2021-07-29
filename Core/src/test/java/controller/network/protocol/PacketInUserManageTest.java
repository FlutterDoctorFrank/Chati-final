package controller.network.protocol;

import org.junit.Assert;
import org.junit.Test;

public class PacketInUserManageTest extends PacketTest<PacketInUserManage> {

    public PacketInUserManageTest() {
        super(PacketInUserManage.class);
    }

    @Test
    public void serializationTest() {
        this.before = new PacketInUserManage(randomUniqueId(), randomEnum(PacketInUserManage.Action.class),
                randomBoolean() ? randomString() : null);

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche User-ID und Aktion
        Assert.assertEquals(this.before.getUserId(), this.after.getUserId());
        Assert.assertEquals(this.before.getAction(), this.after.getAction());

        // Vergleiche optionale Nachrichten
        if (this.before.getMessage() != null) {
            Assert.assertNotNull(this.after.getMessage());
            Assert.assertEquals(this.before.getMessage(), this.after.getMessage());
        } else {
            Assert.assertNull(this.after.getMessage());
        }
    }
}
