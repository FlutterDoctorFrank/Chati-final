package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.PacketOutContextRole;
import model.context.IContext;
import model.role.IContextRole;
import model.role.Role;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.EnumSet;
import java.util.Set;

public class PacketOutContextRoleTest extends PacketServerTest {

    public PacketOutContextRoleTest() {
        super(SendAction.CONTEXT_ROLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPackagingTest() {
        this.getPacket(PacketOutContextRole.class, new Object());
    }

    @Test
    public void correctPackagingTest() {
        final IContextRole role = Mockito.mock(IContextRole.class);
        final IContext context = Mockito.mock(IContext.class);
        final Set<Role> roles = EnumSet.of(randomEnum(Role.class));

        Mockito.when(context.getContextId()).thenReturn(randomContextId());
        Mockito.when(this.user.getUserId()).thenReturn(randomUniqueId());
        Mockito.when(role.getUser()).thenReturn(this.user);
        Mockito.when(role.getContext()).thenReturn(context);
        Mockito.when(role.getRoles()).thenReturn(roles);

        final PacketOutContextRole packet = this.getPacket(PacketOutContextRole.class, role);

        Assert.assertEquals(this.user.getUserId(), packet.getUserId());
        Assert.assertEquals(context.getContextId(), packet.getContextId());
        Assert.assertArrayEquals(roles.toArray(), packet.getRoles());
    }
}
