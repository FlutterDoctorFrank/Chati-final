package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListenerOut;
import model.context.spatial.ContextMap;
import org.junit.Assert;
import org.junit.Test;

public class PacketOutContextJoinTest extends PacketTest<PacketOutContextJoin> {

    public PacketOutContextJoinTest() {
        super(PacketOutContextJoin.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerOut listener = new MockPacketListenerOut();

        this.before = new PacketOutContextJoin(randomContextId(), randomString(), randomEnum(ContextMap.class));
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketOutContextJoin.class));
    }

    @Test
    public void joinSerializationTest() {
        this.before = new PacketOutContextJoin(randomContextId(), randomString(), randomEnum(ContextMap.class));

        this.serialize();
        this.equals();
    }

    @Test
    public void leaveSerializationTest() {
        this.before = new PacketOutContextJoin(randomContextId(), null, null);

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID und Name
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
        if (this.before.getName() != null) {
            Assert.assertNotNull(this.after.getName());
            Assert.assertEquals(this.before.getName(), this.after.getName());
        } else {
            Assert.assertNull(this.after.getName());
        }

        // Vergleiche die Karte
        if (this.before.getMap() != null) {
            Assert.assertTrue(this.before.isJoin());
            Assert.assertTrue(this.after.isJoin());
            Assert.assertNotNull(this.after.getMap());
            Assert.assertEquals(this.before.getMap(), this.after.getMap());
        } else {
            Assert.assertFalse(this.before.isJoin());
            Assert.assertFalse(this.after.isJoin());
            Assert.assertNull(this.after.getMap());
        }
    }
}
