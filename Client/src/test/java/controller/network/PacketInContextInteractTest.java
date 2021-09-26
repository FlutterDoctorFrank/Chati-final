package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketInContextInteract;
import model.context.ContextID;
import org.junit.Assert;
import org.junit.Test;

public class PacketInContextInteractTest extends PacketClientTest {

    public PacketInContextInteractTest() {
        super(SendAction.CONTEXT_INTERACT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsPackagingTest() {
        this.getPacket(PacketInContextInteract.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTypesPackagingTest() {
        this.getPacket(PacketInContextInteract.class, new Object());
    }

    @Test
    public void interactPackagingTest() {
        final ContextID objectId = randomContextId();

        final PacketInContextInteract packet = this.getPacket(PacketInContextInteract.class, objectId);

        Assert.assertEquals(objectId, packet.getContextId());
    }
}
