package controller.network.protocol.mock;


import controller.network.protocol.*;
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
    public void handle(@NotNull final PacketAudioMessage packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketChatMessage packet) {
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
    public void handle(@NotNull final PacketUserTyping packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketProfileAction packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull PacketVideoFrame packet) {

    }

    @Override
    public void handle(@NotNull final PacketWorldAction packet) {
        this.calls.add(packet.getClass());
    }
}
