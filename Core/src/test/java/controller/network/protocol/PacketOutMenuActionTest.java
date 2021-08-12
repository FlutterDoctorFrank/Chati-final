package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListenerOut;
import model.context.spatial.Menu;
import org.junit.Assert;
import org.junit.Test;

public class PacketOutMenuActionTest extends PacketTest<PacketOutMenuAction> {

    public PacketOutMenuActionTest() {
        super(PacketOutMenuAction.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerOut listener = new MockPacketListenerOut();

        this.before = new PacketOutMenuAction(randomContextId(), randomEnum(Menu.class), randomBoolean());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketOutMenuAction.class));
    }

    @Test
    public void serializationTest() {
        this.before = new PacketOutMenuAction(randomContextId(), randomEnum(Menu.class), randomBoolean());

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID und Menü
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
        Assert.assertEquals(this.before.getMenu(), this.after.getMenu());
        Assert.assertEquals(this.before.isOpen(), this.after.isOpen());
    }
}
