package controller.network.protocol;

import model.context.spatial.Music;
import org.junit.Assert;
import org.junit.Test;

public class PacketOutContextMusicTest extends PacketTest<PacketOutContextMusic> {

    public PacketOutContextMusicTest() {
        super(PacketOutContextMusic.class);
    }

    @Test
    public void setSerializationTest() {
        this.before = new PacketOutContextMusic(randomContextId(), randomEnum(Music.class));

        this.serialize();
        this.equals();
    }

    @Test
    public void unsetSerializationTest() {
        this.before = new PacketOutContextMusic(randomContextId(), null);

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());

        // Vergleiche Musik, fall gesetzt.
        if (this.before.getMusic() != null) {
            Assert.assertNotNull(this.after.getMusic());
            Assert.assertEquals(this.before.getMusic(), this.after.getMusic());
        } else {
            Assert.assertNull(this.after.getMusic());
        }
    }
}
