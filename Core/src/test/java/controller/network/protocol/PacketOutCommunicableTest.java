package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListenerOut;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PacketOutCommunicableTest extends PacketTest<PacketOutCommunicable> {

    public PacketOutCommunicableTest() {
        super(PacketOutCommunicable.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerOut listener = new MockPacketListenerOut();

        this.before = new PacketOutCommunicable(Collections.emptySet());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketOutCommunicable.class));
    }

    @Test
    public void emptySerializationTest() {
        this.before = new PacketOutCommunicable(Collections.emptySet());

        this.serialize();
        this.equals();
    }

    @Test
    public void singleSerializationTest() {
        this.before = new PacketOutCommunicable(Collections.singleton(randomUniqueId()));

        this.serialize();
        this.equals();
    }

    @Test
    public void multipleSerializationTest() {
        final Set<UUID> communicables = new HashSet<>();
        final int size = randomInt(31) + 1;

        while (communicables.size() < size) {
            communicables.add(randomUniqueId());
        }

        this.before = new PacketOutCommunicable(communicables);

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche die Benutzer mit den kommuniziert werden kann.
        Assert.assertEquals(this.before.getCommunicables().length, this.after.getCommunicables().length);
        Assert.assertArrayEquals(this.before.getCommunicables(), this.after.getCommunicables());
    }
}
