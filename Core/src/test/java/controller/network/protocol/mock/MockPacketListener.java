package controller.network.protocol.mock;

import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketMenuOption;
import controller.network.protocol.PacketNotificationResponse;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketVoiceMessage;
import controller.network.protocol.PacketWorldAction;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;

public class MockPacketListener implements PacketListener {

    protected final Set<Class<?>> calls;

    public MockPacketListener() {
        this.calls = new HashSet<>();
    }

    public <T extends Packet<?>> boolean handled(@NotNull final Class<T> clazz) {
        return this.calls.contains(clazz);
    }

    @Override
    public void handle(@NotNull final PacketAvatarMove packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketChatMessage packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketVoiceMessage packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketMenuOption packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketNotificationResponse packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketWorldAction packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketProfileAction packet) {
        this.calls.add(packet.getClass());
    }
}
