package controller.network.protocol;

import model.context.spatial.SpatialMap;
import org.junit.Assert;
import org.junit.Test;

public class PacketWorldActionTest extends PacketTest<PacketWorldAction> {

    public PacketWorldActionTest() {
        super(PacketWorldAction.class);
    }

    @Test
    public void createSerializationTest() {
        this.before = new PacketWorldAction(randomEnum(SpatialMap.class), randomString());

        this.serialize();
        this.equals();

        this.before = new PacketWorldAction(randomEnum(SpatialMap.class), randomString(), randomString(), randomBoolean());

        this.serialize();
        this.equals();
    }

    @Test
    public void otherSerializationTest() {
        this.before = new PacketWorldAction(randomEnum(PacketWorldAction.Action.class), randomContextId());

        this.serialize();
        this.equals();

        this.before = new PacketWorldAction(randomEnum(PacketWorldAction.Action.class), randomContextId(), randomString(), randomBoolean());

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Welt-Aktion
        Assert.assertEquals(this.before.getAction(), this.after.getAction());

        // Vergleiche Kontext-ID
        if (this.before.getContextId() != null) {
            Assert.assertNotNull(this.after.getContextId());
            Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
        } else {
            Assert.assertNull(this.after.getContextId());
        }

        // Vergleiche Weltkarte
        if (this.before.getMap() != null) {
            Assert.assertNotNull(this.after.getMap());
            Assert.assertEquals(this.before.getMap(), this.after.getMap());
        } else {
            Assert.assertNull(this.after.getMap());
        }

        // Vergleiche Weltname
        if (this.before.getName() != null) {
            Assert.assertNotNull(this.after.getName());
            Assert.assertEquals(this.before.getName(), this.after.getName());
        } else {
            Assert.assertNull(this.after.getName());
        }

        // Vergleiche Fehlermeldung
        if (this.before.getMessage() != null) {
            Assert.assertNotNull(this.after.getMessage());
            Assert.assertEquals(this.before.getMessage(), this.after.getMessage());
        } else {
            Assert.assertNull(this.after.getMessage());
        }

        Assert.assertEquals(this.before.isSuccess(), this.after.isSuccess());
    }
}