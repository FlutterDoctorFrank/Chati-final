package controller.network.protocol.mock;

import controller.network.protocol.PacketInContextInteract;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketListenerIn;
import org.jetbrains.annotations.NotNull;

public class MockPacketListenerIn extends MockPacketListener implements PacketListenerIn {

    @Override
    public void handle(@NotNull final PacketInContextInteract packet) {
        this.calls.add(packet.getClass());
    }

    @Override
    public void handle(@NotNull final PacketInUserManage packet) {
        this.calls.add(packet.getClass());
    }
}
