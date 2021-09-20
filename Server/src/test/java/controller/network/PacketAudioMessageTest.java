package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.mock.MockIUser;
import controller.network.protocol.PacketAudioMessage;
import model.communication.message.IAudioMessage;
import model.context.spatial.IWorld;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.logging.Level;

public class PacketAudioMessageTest extends PacketServerTest {

    public PacketAudioMessageTest() {
        super(SendAction.AUDIO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketAudioMessage.class, new Object());
    }

    @Test
    public void correctAudioPackagingTest() {
        final IAudioMessage message = Mockito.mock(IAudioMessage.class);

        Mockito.when(message.getSender()).thenReturn(null);
        Mockito.when(message.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(message.getAudioData()).thenReturn(randomBytes());

        final PacketAudioMessage packet = this.getPacket(PacketAudioMessage.class, message);

        Assert.assertNull(packet.getSenderId());
        Assert.assertNotNull(packet.getTimestamp());
        Assert.assertEquals(message.getTimestamp(), packet.getTimestamp());
        Assert.assertArrayEquals(message.getAudioData(), packet.getAudioData());
    }

    @Test
    public void correctVoicePackagingTest() {
        final IAudioMessage message = Mockito.mock(IAudioMessage.class);
        final IUser sender = Mockito.mock(IUser.class);

        Mockito.when(sender.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(message.getSender()).thenReturn(sender);
        Mockito.when(message.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(message.getAudioData()).thenReturn(randomBytes());

        final PacketAudioMessage packet = this.getPacket(PacketAudioMessage.class, message);

        Assert.assertNotNull(packet.getSenderId());
        Assert.assertEquals(sender.getUserId(), packet.getSenderId());
        Assert.assertNotNull(packet.getTimestamp());
        Assert.assertEquals(message.getTimestamp(), packet.getTimestamp());
        Assert.assertArrayEquals(message.getAudioData(), packet.getAudioData());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketAudioMessage packet = new PacketAudioMessage(randomBytes());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not talk while not logged in"));

        final MockIUser user = this.login();

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not talk while not in a world"));
        Assert.assertFalse(user.called("talk"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final MockIUser user = this.login();
        final PacketAudioMessage packet = Mockito.mock(PacketAudioMessage.class);

        user.setWorld(Mockito.mock(IWorld.class));
        Mockito.when(packet.getSenderId()).thenReturn(randomUniqueId());
        Mockito.when(packet.getAudioData()).thenReturn(randomBytes());

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "User-ID must be the own or null"));
        Assert.assertFalse(user.called("talk"));
    }

    @Test
    public void handleCorrectTalkTest() {
        final PacketAudioMessage packet = new PacketAudioMessage(randomBytes());
        final MockIUser user = this.login();

        user.setWorld(Mockito.mock(IWorld.class));

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(user.called("talk"));
    }
}
