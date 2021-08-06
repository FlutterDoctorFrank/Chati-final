package controller.network.protocol;

import model.context.spatial.Music;
import model.role.Role;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PacketOutContextInfoTest extends PacketTest<PacketOutContextInfo> {

    public PacketOutContextInfoTest() {
        super(PacketOutContextInfo.class);
    }

    @Test
    public void setSerializationTest() {
        this.before = new PacketOutContextInfo(randomContextId(), randomEnum(Music.class), Collections.emptySet());

        this.serialize();
        this.equals();
    }

    @Test
    public void unsetSerializationTest() {
        this.before = new PacketOutContextInfo(randomContextId(), null, Collections.emptySet());

        this.serialize();
        this.equals();
    }

    @Test
    public void mutesSerializationTest() {
        final Set<UUID> mutes = new HashSet<>();
        final int size = randomInt(Role.values().length);

        while (mutes.size() < size) {
            mutes.add(randomUniqueId());
        }

        this.before = new PacketOutContextInfo(randomContextId(), null, mutes);

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID und Stumm geschaltete Benutzer
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
        Assert.assertArrayEquals(this.before.getMutes(), this.after.getMutes());

        // Vergleiche Musik, fall gesetzt.
        if (this.before.getMusic() != null) {
            Assert.assertNotNull(this.after.getMusic());
            Assert.assertEquals(this.before.getMusic(), this.after.getMusic());
        } else {
            Assert.assertNull(this.after.getMusic());
        }
    }
}
