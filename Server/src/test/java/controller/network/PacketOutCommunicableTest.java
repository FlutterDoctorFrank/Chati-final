package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutCommunicable;
import model.user.IUser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketOutCommunicableTest extends PacketServerTest {

    public PacketOutCommunicableTest() {
        super(SendAction.COMMUNICABLES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketOutCommunicable.class, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalUserPackagingTest() {
        final IUser user = Mockito.mock(IUser.class);

        Mockito.when(user.getCommunicableIUsers()).thenReturn(Collections.emptyMap());
        Mockito.when(this.user.getCommunicableIUsers()).thenReturn(Collections.emptyMap());

        this.getPacket(PacketOutCommunicable.class, user);
    }

    @Test
    public void correctPackagingTest() {
        final Map<UUID, IUser> communicables = new HashMap<>();
        final int size = randomInt(31) + 1;

        while (communicables.size() < size) {
            final UUID uniqueId = randomUniqueId();
            final IUser user = Mockito.mock(IUser.class);

            Mockito.when(user.getUserId()).thenReturn(uniqueId);

            communicables.put(uniqueId, user);
        }

        Mockito.when(this.user.getCommunicableIUsers()).thenReturn(communicables);

        final PacketOutCommunicable packet = this.getPacket(PacketOutCommunicable.class, this.user);

        Assert.assertEquals(communicables.size(), packet.getCommunicables().length);
        Assert.assertArrayEquals(communicables.keySet().toArray(new UUID[0]), packet.getCommunicables());
    }
}
