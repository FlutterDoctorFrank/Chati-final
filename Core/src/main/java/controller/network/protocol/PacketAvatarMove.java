package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.spatial.Direction;
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
    private Direction direction;
    private UUID userId;
    private float posX;
    private float posY;
    private boolean sprinting;
    private boolean movable;

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
     * @param sprinting true, wenn ob sich der Benutzer schnell fortbewegt, ansonsten false.
     */
    public PacketAvatarMove(@NotNull final Direction direction, final float posX, final float posY,
                            final boolean sprinting) {
        this.action = AvatarAction.MOVE_AVATAR;
        this.direction = direction;
        this.posX = posX;
        this.posY = posY;
        this.sprinting = sprinting;
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
     * @param sprinting true, wenn sich der Benutzer schnell fortbewegt, ansonsten false.
     */
    public PacketAvatarMove(@NotNull final AvatarAction action, @Nullable final UUID userId,
                            @NotNull final Direction direction, final float posX, final float posY,
                            final boolean sprinting, final boolean movable) {
        this.action = action;
        this.userId = userId;
        this.direction = direction;
        this.posX = posX;
        this.posY = posY;
        this.sprinting = sprinting;
        this.movable = movable;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeEnum(output, this.action);
        PacketUtils.writeNullableUniqueId(output, this.userId);
        PacketUtils.writeNullableEnum(output, this.direction);
        output.writeFloat(this.posX);
        output.writeFloat(this.posY);
        output.writeBoolean(this.sprinting);
        output.writeBoolean(this.movable);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.action = PacketUtils.readEnum(input, AvatarAction.class);
        this.userId = PacketUtils.readNullableUniqueId(input);
        this.direction = PacketUtils.readNullableEnum(input, Direction.class);
        this.posX = input.readFloat();
        this.posY = input.readFloat();
        this.sprinting = input.readBoolean();
        this.movable = input.readBoolean();
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{action=" + this.action + ", userId=" + this.userId
                + ", direction=" + this.direction + ", posX=" + this.posX + ", posY=" + this.posY
                + ", sprinting=" + this.sprinting + ", movable=" + this.movable + "}";
    }

    /**
     * Gibt die Aktion, die auf den Avatar des Benutzers ausgeführt werden soll, zurück.
     * @return die Avatar-Aktion.
     */
    public @NotNull AvatarAction getAction() {
        return this.action;
    }

    /**
     * Gibt die Benutzer-ID des zum Avatar gehörenden Benutzers zurück, falls das Paket vom Server versendet wurde.
     * @return die Benutzer-ID oder null.
     */
    public @Nullable UUID getUserId() {
        return this.userId;
    }

    /**
     * Gibt die Richtung, in die der Avatar schaut, an.
     * @return die Richtung des Avatars.
     */
    public @Nullable Direction getDirection() {
        return this.direction;
    }

    /**
     * Gibt die neue X-Position des Avatars im Raum zurück.
     * @return die neue X-Koordinate.
     */
    public float getPosX() {
        return this.posX;
    }

    /**
     * Gibt die neue Y-Position des Avatars im Raum zurück.
     * @return die neue Y-Koordinate.
     */
    public float getPosY() {
        return this.posY;
    }

    /**
     * Gibt zurück, ob sich der Benutzer schnell fortbewegt.
     * @return true, wenn sich der Benutzer schnell fortbewegt, ansonsten false.
     */
    public boolean isSprinting() {
        return this.sprinting;
    }

    /**
     * Gibt zurück, ob sich der Benutzer bewegen kann.
     * @return true, wenn sich der Benutzer bewegen kann, ansonsten false.
     */
    public boolean isMovable() {
        return this.movable;
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
        MOVE_AVATAR,

        /**
         * Führt dazu, dass die Position eines Avatars auf einer Karte aktualisiert wird.
         */
        SPAWN_AVATAR,
    }
}
