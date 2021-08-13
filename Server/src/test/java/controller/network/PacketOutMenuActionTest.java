package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutMenuAction;
import model.context.spatial.Menu;
import model.context.spatial.objects.IInteractable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class PacketOutMenuActionTest extends PacketServerTest {

    public PacketOutMenuActionTest() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalOpenPackagingTest() {
        this.getPacket(SendAction.OPEN_MENU, PacketOutMenuAction.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalClosePackagingTest() {
        this.getPacket(SendAction.CLOSE_MENU, PacketOutMenuAction.class, new Object());
    }

    @Test
    public void openPackagingTest() {
        final IInteractable interactable = Mockito.mock(IInteractable.class);

        Mockito.when(interactable.getContextId()).thenReturn(randomContextId());
        Mockito.when(interactable.getMenu()).thenReturn(randomEnum(Menu.class));

        final PacketOutMenuAction packet = this.getPacket(SendAction.OPEN_MENU, PacketOutMenuAction.class, interactable);

        Assert.assertEquals(interactable.getContextId(), packet.getContextId());
        Assert.assertEquals(interactable.getMenu(), packet.getMenu());
        Assert.assertTrue(packet.isOpen());
    }

    @Test
    public void closePackagingTest() {
        final IInteractable interactable = Mockito.mock(IInteractable.class);

        Mockito.when(interactable.getContextId()).thenReturn(randomContextId());
        Mockito.when(interactable.getMenu()).thenReturn(randomEnum(Menu.class));

        final PacketOutMenuAction packet = this.getPacket(SendAction.CLOSE_MENU, PacketOutMenuAction.class, interactable);

        Assert.assertEquals(interactable.getContextId(), packet.getContextId());
        Assert.assertEquals(interactable.getMenu(), packet.getMenu());
        Assert.assertFalse(packet.isOpen());
    }
}
