package controller.network;

import controller.network.ServerSender.SendAction;
import controller.network.protocol.PacketAudioMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.logging.Level;

public class PacketAudioMessageTest extends PacketClientTest {

    public PacketAudioMessageTest() {
        super(SendAction.VOICE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsPackagingTest() {
        this.getPacket(PacketAudioMessage.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTypesPackagingTest() {
        this.getPacket(PacketAudioMessage.class, new Object());
    }

    @Test
    public void messagePackagingTest() {
        final byte[] voiceData = randomBytes();

        final PacketAudioMessage packet = this.getPacket(PacketAudioMessage.class, voiceData);

        Assert.assertNull(packet.getSenderId());
        Assert.assertArrayEquals(voiceData, packet.getAudioData());
    }

    @Test
    public void handleUnexpectedPacketTest() {
        final PacketAudioMessage packet = Mockito.mock(PacketAudioMessage.class);

        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive audio message while user is not logged in"));

        this.login();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Can not receive audio message while user is not in a world"));
    }

    @Test
    public void handleInvalidPacketTest() {
        final PacketAudioMessage packet = Mockito.mock(PacketAudioMessage.class);

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Timestamp can not be null"));
        Assert.assertFalse(this.view.called("play-music-data"));
        Assert.assertFalse(this.view.called("play-voice-data"));
        Mockito.when(packet.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(packet.getSenderId()).thenReturn(randomUniqueId());
        Mockito.when(packet.getAudioData()).thenReturn(randomBytes());

        this.view.playVoiceData(true);
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Server tried to send voice message from unknown sender"));
        Assert.assertTrue(this.view.called("play-voice-data"));
    }

    @Test
    public void handleCorrectPacketTest() {
        final PacketAudioMessage packet = Mockito.mock(PacketAudioMessage.class);

        Mockito.when(packet.getTimestamp()).thenReturn(LocalDateTime.now());
        Mockito.when(packet.getAudioData()).thenReturn(randomBytes());

        this.login();
        this.joinWorld();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertTrue(this.view.called("play-music-data"));
        Assert.assertFalse(this.view.called("play-voice-data"));
        Mockito.when(packet.getSenderId()).thenReturn(randomUniqueId());

        this.view.reset();
        this.handler.reset();
        this.connection.handle(packet);

        Assert.assertFalse(this.handler.logged());
        Assert.assertFalse(this.view.called("play-music-data"));
        Assert.assertTrue(this.view.called("play-voice-data"));
    }
}
