package controller.network.protocol;

import controller.network.protocol.PacketInUserManage.Action;
import controller.network.protocol.mock.MockPacketListenerIn;
import org.junit.Assert;
import org.junit.Test;

public class PacketInUserManageTest extends PacketTest<PacketInUserManage> {

    public PacketInUserManageTest() {
        super(PacketInUserManage.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerIn listener = new MockPacketListenerIn();

        this.before = new PacketInUserManage(randomUniqueId(), randomEnum(PacketInUserManage.Action.class), null);
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketInUserManage.class));
    }

    @Test
    public void emptySerializationTest() {
        this.before = new PacketInUserManage(randomUniqueId(), randomEnum(Action.class), null);

        this.serialize();
        this.equals();
    }

    @Test
    public void singleSerializationTest() {
        this.before = new PacketInUserManage(randomUniqueId(), randomEnum(Action.class), new String[]{randomString()});

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche User-ID und Aktion
        Assert.assertEquals(this.before.getUserId(), this.after.getUserId());
        Assert.assertEquals(this.before.getAction(), this.after.getAction());

        // Vergleiche optionale Argumente
        if (this.before.getArguments() != null) {
            Assert.assertNotNull(this.after.getArguments());
            Assert.assertArrayEquals(this.before.getArguments(), this.after.getArguments());
        } else {
            Assert.assertNull(this.after.getArguments());
        }
    }
}
