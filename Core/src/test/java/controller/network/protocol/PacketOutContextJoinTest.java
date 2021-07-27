package controller.network.protocol;

import model.context.spatial.Map;
import org.junit.Assert;
import org.junit.Test;

public class PacketOutContextJoinTest extends PacketTest<PacketOutContextJoin> {

    public PacketOutContextJoinTest() {
        super(PacketOutContextJoin.class);
    }

    @Test
    public void joinSerializationTest() {
        this.before = new PacketOutContextJoin(randomContextId(), randomEnum(Map.class));

        this.serialize();
        this.equals();
    }

    @Test
    public void leaveSerializationTest() {
        this.before = new PacketOutContextJoin(randomContextId(), null);

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());

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
