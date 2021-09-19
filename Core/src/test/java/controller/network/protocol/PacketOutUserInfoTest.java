package controller.network.protocol;

import controller.network.protocol.PacketOutUserInfo.Action;
import controller.network.protocol.PacketOutUserInfo.UserInfo;
import controller.network.protocol.mock.MockPacketListenerOut;
import model.user.Avatar;
import model.user.Status;
import org.junit.Assert;
import org.junit.Test;

public class PacketOutUserInfoTest extends PacketTest<PacketOutUserInfo> {

    public PacketOutUserInfoTest() {
        super(PacketOutUserInfo.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerOut listener = new MockPacketListenerOut();

        this.before = new PacketOutUserInfo(randomContextId(), randomEnum(Action.class),
                new UserInfo(randomUniqueId(), randomString()));
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketOutUserInfo.class));
    }

    @Test
    public void serializationTest() {
        this.before = new PacketOutUserInfo(randomContextId(), randomEnum(Action.class),
                new UserInfo(randomUniqueId(), randomString()));

        // Hier werden zufällige Werte in die Benutzerinformation geschrieben
        this.before.getInfo().setWorld(randomContextId());
        this.before.getInfo().setRoom(randomContextId());
        this.before.getInfo().setAvatar(randomEnum(Avatar.class));
        this.before.getInfo().setStatus(randomEnum(Status.class));

        final int flags = randomInt(2) + 1;

        while (this.before.getInfo().getFlags().size() < flags) {
            this.before.getInfo().addFlag(randomEnum(UserInfo.Flag.class));
        }

        this.serialize();
        this.equals();
    }

    @Test
    public void equalUserInfoTest() {
        final UserInfo first = new UserInfo(randomUniqueId(), randomString(), randomEnum(Status.class));

        Assert.assertNotNull(first.getName());
        Assert.assertNotNull(first.getStatus());

        final UserInfo second = new UserInfo(first.getUserId(), first.getName(), first.getStatus());

        Assert.assertEquals(first, first);
        Assert.assertEquals(first, second);
        Assert.assertEquals(first.hashCode(), second.hashCode());
        Assert.assertNotEquals(first, new Object());
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
        Assert.assertEquals(this.before.getInfo().getUserId(), this.after.getInfo().getUserId());

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

        if (this.before.getInfo().getWorld() != null) {
            Assert.assertNotNull(this.after.getInfo().getWorld());
            Assert.assertEquals(this.before.getInfo().getWorld(), this.after.getInfo().getWorld());
        } else {
            Assert.assertNull(this.after.getInfo().getWorld());
        }

        if (this.before.getInfo().getRoom() != null) {
            Assert.assertNotNull(this.after.getInfo().getRoom());
            Assert.assertEquals(this.before.getInfo().getRoom(), this.after.getInfo().getRoom());
        } else {
            Assert.assertNull(this.after.getInfo().getRoom());
        }

        Assert.assertEquals(this.before.getInfo().getStatus(), this.after.getInfo().getStatus());
        Assert.assertEquals(this.before.getInfo().getFlags(), this.after.getInfo().getFlags());
    }
}
