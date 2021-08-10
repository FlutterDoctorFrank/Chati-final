package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import model.context.spatial.SpatialMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ein Paket, das Informationen über verschiedene Aktionen auf eine Welt enthält.
 * <p>
 *     Das Paket wird von einem Client mit den zu der Aktion benötigten Informationen erzeugt und an den Server
 *     gesendet.
 *     Das Paket teilt dem Server mit, dass der Benutzer eine Aktion auf eine Welt ausführen möchte.
 *     Nach der Verarbeitung des Pakets vom Server werden die Informationen des Pakets aktualisiert und das Paket
 *     zurück an den Client gesendet.
 * </p>
 */
public class PacketWorldAction implements Packet<PacketListener> {

    private ContextID contextId;
    private Action action;
    private String message;
    private boolean success;

    // Variablen für die Welterstellung.
    private SpatialMap map;
    private String name;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    public PacketWorldAction() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Erstellen einer Welt von der Client-Anwendung.
     * @param map die Karte der zur erstellenden Welt.
     * @param name der Name der zur erstellenden Welt.
     */
    public PacketWorldAction(@NotNull final SpatialMap map, @NotNull final String name) {
        this.action = Action.CREATE;
        this.map = map;
        this.name = name;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * <p><i>
     *     Dieser Konstruktor erlaubt nicht die Erzeugung des WorldCreate-Pakets. Siehe {@link PacketWorldAction(SpatialMap, String)}
     * </i></p>
     * @param action die Aktion die auf die Welt ausgeführt werden soll.
     * @param contextId die Kontext-ID der Welt, auf die die Aktion ausgeführt werden soll.
     */
    public PacketWorldAction(@NotNull final Action action, @NotNull final ContextID contextId) {
        if (action == Action.CREATE) {
            throw new IllegalArgumentException("Invalid Constructor for creating WorldCreate-Packets");
        }

        this.action = action;
        this.contextId = contextId;
    }

    /**
     * Ausschließlich für die Erzeugung einer Antwort des Netzwerkpakets von der Server-Anwendung.
     * <p><i>
     *     Dieser Konstruktor erlaubt nicht die Erzeugung des WorldCreate-Pakets. Siehe {@link PacketWorldAction(SpatialMap, String)}
     * </i></p>
     * @param action die Aktion die auf die Welt ausgeführt wurde.
     * @param contextId die Kontext-ID der Welt, auf die die Aktion ausgeführt wurde.
     * @param message die Nachricht, die durch die Aktion erzeugt wurde. Null, falls keine erzeugt wurde.
     * @param success true, wenn die Aktion erfolgreich war, ansonsten false.
     */
    public PacketWorldAction(@NotNull final Action action, @NotNull final ContextID contextId,
                             @NotNull final String name, @Nullable final String message, final boolean success) {
        if (action == Action.CREATE) {
            throw new IllegalArgumentException("Invalid Constructor for creating WorldCreate-Packets");
        }

        this.action = action;
        this.contextId = contextId;
        this.name = name;
        this.message = message;
        this.success = success;
    }

    /**
     * Ausschließlich für die Erzeugung einer Antwort des Netzwerkpakets von der Server-Anwendung.
     * @param previous das Netzwerkpaket auf das geantwortet werden soll.
     * @param message die Nachricht, die durch die Aktion erzeugt wurde. Null, falls keine erzeugt wurde.
     * @param success true, wenn die Aktion erfolgreich war, ansonsten false.
     */
    public PacketWorldAction(@NotNull final PacketWorldAction previous,
                             @Nullable final String message, final boolean success) {
        this.action = previous.getAction();
        this.contextId = previous.getContextId();
        this.map = previous.getMap();
        this.name = previous.getName();
        this.message = message;
        this.success = success;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableContextId(output, this.contextId);
        PacketUtils.writeEnum(output, this.action);
        PacketUtils.writeNullableEnum(output, this.map);
        output.writeAscii(this.name);
        output.writeString(this.message);
        output.writeBoolean(this.success);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readNullableContextId(input);
        this.action = PacketUtils.readEnum(input, Action.class);
        this.map = PacketUtils.readNullableEnum(input, SpatialMap.class);
        this.name = input.readString();
        this.message = input.readString();
        this.success = input.readBoolean();
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + ", action=" + this.action +
                ", message='" + this.message + "', success=" + this.success + ", map=" + this.map +
                ", name='" + this.name + "'}";
    }

    /**
     * Gibt die Aktion, die auf der Welt ausgeführt werden soll, zurück.
     * @return die Aktion, die ausgeführt werden soll.
     */
    public @NotNull Action getAction() {
        return this.action;
    }

    /**
     * Gibt die Kontext-ID der Welt zurück.
     * @return die ID des Kontexts.
     */
    public @Nullable ContextID getContextId() {
        return this.contextId;
    }

    /**
     * Gibt den Namen der Welt zurück.
     * <p><i>
     *     Wird ausschließlich für die Aktion {@link Action#CREATE} genutzt.
     * </i></p>
     * @return den Namen der Welt.
     */
    public @Nullable String getName() {
        return this.name;
    }

    /**
     * Gibt die Karte der Welt zurück.
     * <p><i>
     *     Wird ausschließlich für die Aktion {@link Action#CREATE} genutzt.
     * </i></p>
     * @return die Karte der Welt.
     */
    public @Nullable SpatialMap getMap() {
        return this.map;
    }

    /**
     * Gibt den Nachrichten-Schlüssel einer Fehlermeldung zurück, falls ein Fehler aufgetreten ist.
     * @return den Nachrichten-Schlüssel der Meldung.
     */
    public @Nullable String getMessage() {
        return this.message;
    }

    /**
     * Gibt zurück, ob die Aktion erfolgreich war.
     * @return true, wenn die Aktion erfolgreich war, ansonsten false
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Eine Enumeration für die verschiedenen Welt-Aktionen.
     */
    public enum Action {

        /**
         * Führt dazu, dass eine existierende Welt beigetreten wird.
         */
        JOIN,

        /**
         * Führt dazu, dass eine beigetretene Welt verlassen wird.
         */
        LEAVE,

        /**
         * Führt dazu, dass eine neue Welt erstellt wird.
         */
        CREATE,

        /**
         * Führt dazu, dass eine existierende Welt gelöscht wird.
         */
        DELETE
    }
}
