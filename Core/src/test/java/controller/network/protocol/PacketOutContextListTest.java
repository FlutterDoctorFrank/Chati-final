package controller.network.protocol;

import controller.network.protocol.PacketOutContextList.ContextInfo;
import controller.network.protocol.mock.MockPacketListenerOut;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PacketOutContextListTest extends PacketTest<PacketOutContextList> {

    public PacketOutContextListTest() {
        super(PacketOutContextList.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerOut listener = new MockPacketListenerOut();

        this.before = new PacketOutContextList(randomContextId(), Collections.emptySet());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketOutContextList.class));
    }

    @Test
    public void emptySerializationTest() {
        this.before = new PacketOutContextList(randomContextId(), Collections.emptySet());

        this.serialize();
        this.equals();
    }

    @Test
    public void singleSerializationTest() {
        this.before = new PacketOutContextList(randomContextId(),
                Collections.singleton(new ContextInfo(randomContextId(), randomString(), randomBoolean())));

        this.serialize();
        this.equals();
    }

    @Test
    public void multipleSerializationTest() {
        final Set<ContextInfo> infos = new HashSet<>();
        final int size = randomInt(8) + 1;

        while (infos.size() < size) {
            infos.add(new ContextInfo(randomContextId(), randomString(), randomBoolean()));
        }

        this.before = new PacketOutContextList(randomContextId(), infos);

        this.serialize();
        this.equals();
    }

    @Test
    public void equalContextInfoTest() {
        final ContextInfo first = new ContextInfo(randomContextId(), randomString(), randomBoolean());
        final ContextInfo second = new ContextInfo(first.getContextId(), first.getName(), first.isPrivate());

        Assert.assertEquals(first, first);
        Assert.assertEquals(first, second);
        Assert.assertEquals(first.hashCode(), second.hashCode());
        Assert.assertNotEquals(first, new Object());
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());

        // Vergleiche Infos
        Assert.assertEquals(this.before.getInfos().length, this.after.getInfos().length);
        Assert.assertArrayEquals(this.before.getInfos(), this.after.getInfos());
    }
}
