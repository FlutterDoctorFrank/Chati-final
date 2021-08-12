package controller.network.protocol;

import controller.network.protocol.mock.MockPacketListenerOut;
import model.role.Role;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PacketOutContextRoleTest extends PacketTest<PacketOutContextRole> {

    public PacketOutContextRoleTest() {
        super(PacketOutContextRole.class);
    }

    @Test
    public void callListenerTest() {
        final MockPacketListenerOut listener = new MockPacketListenerOut();

        this.before = new PacketOutContextRole(randomContextId(), randomUniqueId(), Collections.emptySet());
        this.before.call(listener);

        Assert.assertTrue(listener.handled(PacketOutContextRole.class));
    }

    @Test
    public void emptySerializationTest() {
        this.before = new PacketOutContextRole(randomContextId(), randomUniqueId(), Collections.emptySet());

        this.serialize();
        this.equals();
    }

    @Test
    public void singleSerializationTest() {
        this.before = new PacketOutContextRole(randomContextId(), randomUniqueId(), Collections.singleton(randomEnum(Role.class)));

        this.serialize();
        this.equals();
    }

    @Test
    public void multipleSerializationTest() {
        final Set<Role> roles = new HashSet<>();
        final int size = randomInt(Role.values().length);

        while (roles.size() < size) {
            roles.add(randomEnum(Role.class));
        }

        this.before = new PacketOutContextRole(randomContextId(), randomUniqueId(), roles);

        this.serialize();
        this.equals();
    }

    @Override
    public void equals() {
        // Vergleiche Kontext-ID und User-ID
        Assert.assertEquals(this.before.getContextId(), this.after.getContextId());
        Assert.assertEquals(this.before.getUserId(), this.after.getUserId());

        // Vergleiche Rollen
        Assert.assertEquals(this.before.getRoles().length, this.after.getRoles().length);
        Assert.assertArrayEquals(this.before.getRoles(), this.after.getRoles());
    }
}
