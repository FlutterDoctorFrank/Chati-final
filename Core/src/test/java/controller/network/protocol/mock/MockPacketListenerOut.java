package controller.network.protocol.mock;

import controller.network.protocol.PacketListenerOut;
import controller.network.protocol.PacketOutCommunicable;
import controller.network.protocol.PacketOutContextInfo;
import controller.network.protocol.PacketOutContextJoin;
import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketOutMenuAction;
import controller.network.protocol.PacketOutNotification;
import controller.network.protocol.PacketOutUserInfo;
import org.jetbrains.annotations.NotNull;

public class MockPacketListenerOut extends MockPacketListener implements PacketListenerOut {

    @Override
    public void handle(@NotNull final PacketOutCommunicable packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketOutContextList packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketOutContextJoin packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketOutContextInfo packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketOutContextRole packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketOutMenuAction packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketOutNotification packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketOutUserInfo packet) {
        this.calls.add(packet.getClass());
    }
}
