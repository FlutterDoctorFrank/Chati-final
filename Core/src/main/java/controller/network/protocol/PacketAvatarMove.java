package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/**
 * Ein Paket, das Information über die Bewegung eines Avatars auf einer Karte, enthält.
 * <p>
 *     Das Paket wird von einem Client mit der neuen Position des Avatars erzeugt und an den Server gesendet.
 *     Nach der Verarbeitung des Pakets vom Server, wird die Benutzer-ID des Avatars und die Avatar Aktion gesetzt und
 *     das Paket wird an all die Clients der Benutzer verteilt, die sich innerhalb des gleichen Raums befinden.
 *     Das Paket wird ebenfalls vom Server erzeugt und an einen Client gesendet, um dem Client die Position der anderen
 *     Avatare mitzuteilen.
 * </p>
 */
public class PacketAvatarMove implements Packet<PacketListener> {

    private AvatarAction action;
    private UUID userId;
    private int posX;
    private int posY;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketAvatarMove() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param posX Die neue X-Koordinate.
     * @param posY Die neue Y-Koordinate.
     */
    public PacketAvatarMove(final int posX, final int posY) {
        this(AvatarAction.UPDATE_AVATAR, null, posX, posY);
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Entfernen des Benutzers von der Server-Anwendung.
     * @param userId Die ID des zu entfernenden Benutzers.
     */
    public PacketAvatarMove(@NotNull final UUID userId) {
        this.action = AvatarAction.REMOVE_AVATAR;
        this.userId = userId;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param action Die Aktion auf den Avatar des Benutzers.
     * @param userId Die ID des bewegenden Benutzers.
     * @param posX Die neue X-Koordinate.
     * @param posY Die neue Y-Koordinate.
     */
    public PacketAvatarMove(@NotNull final AvatarAction action, @Nullable final UUID userId, final int posX, final int posY) {
        this.action = action;
        this.userId = userId;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeEnum(output, this.action);
        PacketUtils.writeNullableUniqueId(output, this.userId);
        output.writeInt(this.posX, true);
        output.writeInt(this.posY, true);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.action = PacketUtils.readEnum(input, AvatarAction.class);
        this.userId = PacketUtils.readNullableUniqueId(input);
        this.posX = input.readInt(true);
        this.posY = input.readInt(true);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{action=" + this.action + ", userId=" + this.userId
                + ", posX=" + this.posX + ", posY=" + this.posY + "}";
    }

    /**
     * Gibt die Aktion, die auf den Avatar des Benutzers ausgeführt werden soll, zurück.
     * @return Die Avatar-Aktion.
     */
    public @NotNull AvatarAction getAction() {
        return this.action;
    }

    /**
     * Gibt die Benutzer-ID des zum Avatar gehörenden Benutzers zurück, falls das Paket vom Server versendet wurde.
     * @return Die Benutzer-ID oder null.
     */
    public @Nullable UUID getUserId() {
        return this.userId;
    }

    /**
     * Gibt die neue X-Position des Avatars im Raum zurück.
     * @return Die neue X-Koordinate.
     */
    public int getPosX() {
        return this.posX;
    }

    /**
     * Gibt die neue Y-Position des Avatars im Raum zurück.
     * @return Die neue Y-Koordinate.
     */
    public int getPosY() {
        return this.posY;
    }

    /**
     * Eine Enumeration für die verschiedenen Avatar-Aktionen.
     */
    public enum AvatarAction {

        /**
         * Führt dazu, dass eine Avatar aus der Karte entfernt wird.
         */
        REMOVE_AVATAR,

        /**
         * Führt dazu, dass ein neuer Avatar auf einer Karte dargestellt wird.
         */
        UPDATE_AVATAR,

        /**
         * Führt dazu, dass die Position eines Avatars auf einer Karte aktualisiert wird.
         */
        SPAWN_AVATAR
    }
}
