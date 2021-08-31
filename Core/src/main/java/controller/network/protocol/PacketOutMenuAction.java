package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;

/**
 * Ein Paket, das Informationen über das Öffnen und Schließen eines Menüs enthält.
 * <p>
 *     Das Paket wird vom Server erzeugt und an einen Client gesendet. Das Paket teilt dem Client mit, dass ein Menü
 *     geöffnet oder ein geöffnetes Menü geschlossen werden soll.
 * </p>
 */
public class PacketOutMenuAction implements Packet<PacketListenerOut> {

    private ContextID contextId;
    private ContextMenu menu;
    private boolean open;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketOutMenuAction() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param contextId die Kontext-ID des Objekts, das die Menü-Aktion ausführt.
     * @param menu das Menü das geöffnet oder geschlossen werden soll.
     * @param open true, wenn das Menü geöffnet werden soll, ansonsten false.
     */
    public PacketOutMenuAction(@NotNull final ContextID contextId, @NotNull final ContextMenu menu, final boolean open) {
        this.contextId = contextId;
        this.menu = menu;
        this.open = open;
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeContextId(output, this.contextId);
        PacketUtils.writeEnum(output, this.menu);
        output.writeBoolean(this.open);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);
        this.menu = PacketUtils.readEnum(input, ContextMenu.class);
        this.open = input.readBoolean();
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + ", menu=" + this.menu + ", open=" + this.open + "}";
    }

    /**
     * Gibt die Kontext-ID des zum Menü gehörenden Objekts zurück.
     * @return die Kontext-ID des Objekts.
     */
    public @NotNull ContextID getContextId() {
        return this.contextId;
    }

    /**
     * Gibt das Menü, das geöffnet oder geschlossen werden soll, zurück.
     * @return das zugehörige Menü.
     */
    public @NotNull ContextMenu getMenu() {
        return this.menu;
    }

    /**
     * Gibt zurück, ob das Menü geöffnet oder geschlossen werden soll.
     * @return true, wenn das Menü geöffnet werden soll, ansonsten false.
     */
    public boolean isOpen() {
        return this.open;
    }
}
