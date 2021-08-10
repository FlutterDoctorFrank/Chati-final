package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;

/**
 * Ein Paket, das Informationen über eine Aktion innerhalb eines Menüs enthält.
 * <p>
 *     Das Paket wird von einem Client mit den benötigten Informationen und an den Server gesendet.
 *     Das Paket teilt dem Server mit, welche Aktion der Benutzer in einem Menü getätigt hat.
 *     Nach der Verarbeitung des Pakets vom Server, werden die Informationen des Pakets aktualisiert und das Paket
 *     als Bestätigung der ausgeführten Menü-Option zurück an den Client gesendet.
 * </p>
 */
public class PacketMenuOption implements Packet<PacketListener> {

    private ContextID contextId;
    private String[] arguments;
    private int option;

    private String message;
    private boolean success;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    public PacketMenuOption() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param contextId die Kontext-ID des Objekts, zu dem das Menü gehört.
     * @param arguments die Argumente für die auszuführende Menü-Option.
     * @param option die auszuführende Menü-Option.
     */
    public PacketMenuOption(@NotNull final ContextID contextId, @NotNull final String[] arguments, final int option) {
        this.contextId = contextId;
        this.arguments = arguments;
        this.option = option;
    }

    /**
     * Ausschließlich für die Erzeugung einer Antwort des Netzwerkpakets von der Server-Anwendung.
     * @param previous das Netzwerkpaket auf das geantwortet werden soll.
     * @param message die Nachricht, die durch die Menü-Option erzeugt wurde. Null, falls keine erzeugt wurde.
     * @param success true, wenn die Aktion erfolgreich war, ansonsten false.
     */
    public PacketMenuOption(@NotNull final PacketMenuOption previous, @Nullable final String message, final boolean success) {
        this.contextId = previous.getContextId();
        this.arguments = previous.getArguments();
        this.option = previous.getOption();
        this.message = message;
        this.success = success;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeContextId(output, this.contextId);

        output.writeVarInt(this.arguments.length, true);
        for (final String argument : this.arguments) {
            output.writeString(argument);
        }

        output.writeVarInt(this.option, true);
        output.writeString(this.message);
        output.writeBoolean(this.success);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);

        this.arguments = new String[input.readVarInt(true)];
        for (int index = 0; index < this.arguments.length; index++) {
            this.arguments[index] = input.readString();
        }

        this.option = input.readVarInt(true);
        this.message = input.readString();
        this.success = input.readBoolean();
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + ", arguments="
                + Arrays.toString(this.arguments) + ", option=" + this.option + ", message='" + this.message
                + "', success=" + this.success + '}';
    }

    /**
     * Gibt die Kontext-ID des zum Menü gehörenden Objekts zurück.
     * @return die Kontext-ID des Objekts.
     */
    public @NotNull ContextID getContextId() {
        return this.contextId;
    }

    /**
     * Gibt die Eingaben innerhalb des Menüs zurück.
     * @return die Argumente für die Menü-Option.
     */
    public @NotNull String[] getArguments() {
        return this.arguments;
    }

    /**
     * Gibt die ausgewählte Menü-Option zurück.
     * @return die auszuführende Menü-Option.
     */
    public int getOption() {
        return this.option;
    }

    /**
     * Gibt den Nachrichten-Schlüssel einer Fehlermeldung zurück, falls ein Fehler aufgetreten ist.
     * @return den Nachrichten-Schlüssel der Meldung.
     */
    public @Nullable String getMessage() {
        return this.message;
    }

    /**
     * Gibt zurück, ob die Menü-Option erfolgreich war.
     * @return true, wenn die Menü-Option erfolgreich war, ansonsten false
     */
    public boolean isSuccess() {
        return this.success;
    }
}
