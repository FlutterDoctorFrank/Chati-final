package controller.network;

import controller.network.ClientSender.SendAction;
import controller.network.protocol.Packet;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.mockito.Mockito;

public abstract class PacketServerTest extends RandomTest {

    private final SendAction action;

    protected IUser user;

    public PacketServerTest() {
        this.action = null;
    }

    public PacketServerTest(@NotNull final SendAction action) {
        this.action = action;
    }

    @Before
    public void setup() {
        this.user = Mockito.mock(IUser.class);
    }

    public <T extends Packet<?>> @NotNull T getPacket(@NotNull final Class<T> clazz, @NotNull final Object object) {
        if (this.action == null) {
            throw new IllegalStateException("Missing SendAction: Can not get packet from non-existing SendAction");
        }

        final Packet<?> packet = this.action.getPacket(this.user, object);

        if (clazz.isInstance(packet)) {
            return clazz.cast(packet);
        }

        throw new IllegalStateException(String.format("Illegal SendAction: %s does not return class of type %s", this.action, clazz.getSimpleName()));
    }

    public <T extends Packet<?>> @NotNull T getPacket(@NotNull final SendAction action, @NotNull final Class<T> clazz,
                                                      @NotNull final Object object) {
        final Packet<?> packet = action.getPacket(this.user, object);

        if (clazz.isInstance(packet)) {
            return clazz.cast(packet);
        }

        throw new IllegalStateException(String.format("Illegal SendAction: %s does not return class of type %s", action, clazz.getSimpleName()));
    }
}
