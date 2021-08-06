package controller.network.protocol;

import controller.network.protocol.PacketOutUserInfo.Action;
import controller.network.protocol.PacketOutUserInfo.UserInfo;
import model.user.Avatar;
import model.user.Status;
import org.junit.Assert;
import org.junit.Test;

public class PacketOutUserInfoTest extends PacketTest<PacketOutUserInfo> {

    public PacketOutUserInfoTest() {
        super(PacketOutUserInfo.class);
    }

    @Test
    public void serializationTest() {
        this.before = new PacketOutUserInfo(randomContextId(), randomEnum(Action.class), new UserInfo(randomUniqueId(), randomString()));
        this.before.getInfo().setTeleportTo(randomBoolean());

        // Hier werden zufällige Werte in die Benutzerinformation geschrieben
        if (randomBoolean()) {
            this.before.getInfo().setAvatar(randomEnum(Avatar.class));
        }

        if (randomBoolean()) {
            this.before.getInfo().setStatus(randomEnum(Status.class));
        }

        if (randomBoolean()) {
            for (final UserInfo.Flag flag : UserInfo.Flag.values()) {
                if (randomBoolean()) {
                    this.before.getInfo().addFlag(flag);
                }
            }
        }

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID und Aktion
        if (this.before.getContextId() != null) {
            Assert.assertNotNull(this.after.getContextId());
            Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
        } else {
            Assert.assertNull(this.after.getContextId());
        }

        Assert.assertEquals(this.before.getAction(), this.after.getAction());
        Assert.assertEquals(this.before.getInfo(), this.after.getInfo());

        // Vergleichen der genauen Werte der Benutzerinformation
        if (this.before.getInfo().getName() != null) {
            Assert.assertNotNull(this.after.getInfo().getName());
            Assert.assertEquals(this.before.getInfo().getName(), this.after.getInfo().getName());
        } else {
            Assert.assertNull(this.after.getInfo().getName());
        }

        if (this.before.getInfo().getAvatar() != null) {
            Assert.assertNotNull(this.after.getInfo().getAvatar());
            Assert.assertEquals(this.before.getInfo().getAvatar(), this.after.getInfo().getAvatar());
        } else {
            Assert.assertNull(this.after.getInfo().getAvatar());
        }

        if (this.before.getInfo().getStatus() != null) {
            Assert.assertNotNull(this.after.getInfo().getStatus());
            Assert.assertEquals(this.before.getInfo().getStatus(), this.after.getInfo().getStatus());
        } else {
            Assert.assertNull(this.after.getInfo().getStatus());
        }

        Assert.assertEquals(this.before.getInfo().getTeleportTo(), this.after.getInfo().getTeleportTo());
        Assert.assertEquals(this.before.getInfo().getFlags(), this.after.getInfo().getFlags());
    }
}
