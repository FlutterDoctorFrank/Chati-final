package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutContextInfo;
import model.context.spatial.IArea;
import model.context.spatial.Music;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketOutContextInfoTest extends PacketServerTest {

    public PacketOutContextInfoTest() {
        super(SendAction.CONTEXT_INFO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketOutContextInfo.class, new Object());
    }

    @Test
    public void correctPackagingTest() {
        final IArea area = Mockito.mock(IArea.class);
        final Map<UUID, IUser> mutes = new HashMap<>();
        final int size = randomInt(2) + 1;

        while (mutes.size() < size) {
            final UUID uniqueId = randomUniqueId();
            final IUser user = Mockito.mock(IUser.class);

            Mockito.when(user.getUserId()).thenReturn(uniqueId);

            mutes.put(uniqueId, user);
        }

        Mockito.when(area.getMutedUsers()).thenReturn(mutes);
        Mockito.when(area.getContextId()).thenReturn(randomContextId());
        Mockito.when(area.getMusic()).thenReturn(randomEnum(Music.class));

        final PacketOutContextInfo packet = this.getPacket(PacketOutContextInfo.class, area);

        Assert.assertEquals(area.getContextId(), packet.getContextId());
        Assert.assertNotNull(packet.getMusic());
        Assert.assertEquals(area.getMusic(), packet.getMusic());
        Assert.assertArrayEquals(mutes.keySet().toArray(), packet.getMutes());
    }
}
