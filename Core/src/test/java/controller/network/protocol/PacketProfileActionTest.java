package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListener;
import model.user.Avatar;
import model.user.Status;
import org.junit.Assert;
import org.junit.Test;

public class PacketProfileActionTest extends PacketTest<PacketProfileAction> {

    public PacketProfileActionTest() {
        super(PacketProfileAction.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListener listener = new MockPacketListener();

        this.before = new PacketProfileAction(randomString(), randomString(), randomBoolean());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketProfileAction.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalCreationTest() {
        new PacketProfileAction(null, true);
    }

    @Test
    public void loginSerializationTest() {
        this.before = new PacketProfileAction(randomString(), randomString(), randomBoolean());

        this.serialize();
        this.equals();
    }

    @Test
    public void logoutSerializationTest() {
        this.before = new PacketProfileAction(randomString(), randomBoolean());

        this.serialize();
        this.equals();
    }

    @Test
    public void changePasswordSerializationTest() {
        this.before = new PacketProfileAction(randomString(), randomString());

        this.serialize();
        this.equals();
    }

    @Test
    public void changeAvatarSerializationTest() {
        this.before = new PacketProfileAction(randomEnum(Avatar.class));

        this.serialize();
        this.equals();
    }

    @Test
    public void changeStatusSerializationTest() {
        this.before = new PacketProfileAction(randomEnum(Status.class));

        this.serialize();
        this.equals();
    }

    @Test
    public void responseLoginSerializationTest() {
        this.before = new PacketProfileAction(new PacketProfileAction(randomString(), randomString(), randomBoolean()),
                randomUniqueId(), randomEnum(Avatar.class), randomBundle(), randomBoolean());

        this.serialize();
        this.equals();

        // Vergleiche Fehlermeldung und Wahrheitswert der Antwort.
        if (this.before.getMessage() != null) {
            Assert.assertNotNull(this.after.getMessage());
            Assert.assertEquals(this.before.getMessage(), this.after.getMessage());
        } else {
            Assert.assertNull(this.after.getMessage());
        }

        Assert.assertEquals(this.before.isSuccess(), this.after.isSuccess());
    }

    @Test
    public void responseSerializationTest() {
        this.before = new PacketProfileAction(new PacketProfileAction(randomEnum(Avatar.class)), randomBundle(), randomBoolean());

        this.serialize();
        this.equals();

        // Vergleiche Fehlermeldung und Wahrheitswert der Antwort.
        if (this.before.getMessage() != null) {
            Assert.assertNotNull(this.after.getMessage());
            Assert.assertEquals(this.before.getMessage(), this.after.getMessage());
        } else {
            Assert.assertNull(this.after.getMessage());
        }

        Assert.assertEquals(this.before.isSuccess(), this.after.isSuccess());
    }

    @Test
    public void responseCreationTest() {
        this.before = new PacketProfileAction(randomString(), randomString(), randomBoolean());
        this.after = new PacketProfileAction(this.before, randomBundle(), randomBoolean());

        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Profil-Aktion
        Assert.assertEquals(this.before.getAction(), this.after.getAction());

        // Vergleiche Benutzer-ID
        if (this.before.getUserId() != null) {
            Assert.assertNotNull(this.after.getUserId());
            Assert.assertEquals(this.before.getUserId(), this.after.getUserId());
        } else {
            Assert.assertNull(this.after.getUserId());
        }

        // Vergleiche Benutzername
        if (this.before.getName() != null) {
            Assert.assertNotNull(this.after.getName());
            Assert.assertEquals(this.before.getName(), this.after.getName());
        } else {
            Assert.assertNull(this.after.getName());
        }

        // Vergleiche Password
        if (this.before.getPassword() != null) {
            Assert.assertNotNull(this.after.getPassword());
            Assert.assertEquals(this.before.getPassword(), this.after.getPassword());
        } else {
            Assert.assertNull(this.after.getPassword());
        }

        // Vergleiche neues Password
        if (this.before.getNewPassword() != null) {
            Assert.assertNotNull(this.after.getNewPassword());
            Assert.assertEquals(this.before.getNewPassword(), this.after.getNewPassword());
        } else {
            Assert.assertNull(this.after.getNewPassword());
        }

        // Vergleiche Avatar
        if (this.before.getAvatar() != null) {
            Assert.assertNotNull(this.after.getAvatar());
            Assert.assertEquals(this.before.getAvatar(), this.after.getAvatar());
        } else {
            Assert.assertNull(this.after.getAvatar());
        }

        // Vergleiche Status
        if (this.before.getStatus() != null) {
            Assert.assertNotNull(this.after.getStatus());
            Assert.assertEquals(this.before.getStatus(), this.after.getStatus());
        } else {
            Assert.assertNull(this.after.getStatus());
        }
    }
}
