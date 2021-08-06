package controller.network.protocol;

import controller.network.protocol.PacketOutContextList.ContextInfo;
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
    public void emptySerializationTest() {
        this.before = new PacketOutContextList(randomContextId(), Collections.emptySet());

        this.serialize();
        this.equals();
    }

    @Test
    public void singleSerializationTest() {
        this.before = new PacketOutContextList(randomContextId(),
                Collections.singleton(new ContextInfo(randomContextId(), randomString())));

        this.serialize();
        this.equals();
    }

    @Test
    public void multipleSerializationTest() {
        final Set<ContextInfo> infos = new HashSet<>();
        final int size = randomInt(8) + 1;

        while (infos.size() < size) {
            infos.add(new ContextInfo(randomContextId(), randomString()));
        }

        this.before = new PacketOutContextList(randomContextId(), infos);

        this.serialize();
        this.equals();
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
