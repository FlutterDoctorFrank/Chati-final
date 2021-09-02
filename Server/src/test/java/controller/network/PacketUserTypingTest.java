package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketUserTyping;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class PacketUserTypingTest extends PacketServerTest {

    public PacketUserTypingTest() {
        super(SendAction.TYPING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketUserTyping.class, new Object());
    }

    @Test
    public void correctPackagingTest() {
        final IUser user = Mockito.mock(IUser.class);

        Mockito.when(user.getUserId()).thenReturn(randomUniqueId());

        final PacketUserTyping packet = this.getPacket(PacketUserTyping.class, user);

        Assert.assertEquals(user.getUserId(), packet.getSenderId());
    }
}
