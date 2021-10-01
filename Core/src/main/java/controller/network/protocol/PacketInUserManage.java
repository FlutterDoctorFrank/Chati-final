package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über die Verwaltungsmöglichkeiten eines Benutzers enthält.
 * <p>
 *     Das Paket wird von einem Client erzeugt und an den Server gesendet.
 *     Das Paket teilt dem Server mit, dass der Benutzer eine Verwaltungsmöglichkeit auf einem anderen Benutzer
 *     ausführen möchte.
 * </p>
 */
public class PacketInUserManage implements Packet<PacketListenerIn> {

    private UUID userId;
    private Action action;
    private String[] arguments;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketInUserManage() {
        this.arguments = new String[0];
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param userId die ID des Benutzers, auf dem die Verwaltungsmöglichkeit ausgeführt werden soll.
     * @param action die Aktion, die auf dem Benutzer ausgeführt werden soll.
     * @param arguments die Argumente, die zur auszuführenden Aktion gehören.
     */
    public PacketInUserManage(@NotNull final UUID userId, @NotNull final Action action, @Nullable final String[] arguments) {
        this.userId = userId;
        this.action = action;
        this.arguments = arguments != null ? arguments : new String[0];
    }

    @Override
    public void call(@NotNull final PacketListenerIn listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeUniqueId(output, this.userId);
        PacketUtils.writeEnum(output, this.action);
        output.writeInt(this.arguments.length, true);

        for (final String argument : this.arguments) {
            output.writeString(argument);
        }
    }

    @Override
    public void read(Kryo kryo, Input input) {
        this.userId = PacketUtils.readUniqueId(input);
        this.action = PacketUtils.readEnum(input, Action.class);
        this.arguments = new String[input.readInt(true)];

        for (int index = 0; index < this.arguments.length; index++) {
            this.arguments[index] = input.readString();
        }
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{userId=" + this.userId + ", action=" + this.action + ", arguments="
                + Arrays.toString(this.arguments) + "}";
    }

    /**
     * Gibt die Benutzer-ID des Benutzers, auf dem eine Aktion ausgeführt werden soll, zurück.
     * @return die ID des Benutzers.
     */
    public @NotNull UUID getUserId() {
        return this.userId;
    }

    /**
     * Gibt die Aktion, die auf dem Benutzer ausgeführt werden soll, zurück.
     * @return die auszuführende Aktion.
     */
    public @NotNull Action getAction() {
        return this.action;
    }

    /**
     * Gibt die zur Aktion gehörigen Argumente zurück.
     * <p><i>
     *     Wird nur bei den Aktionen {@link Action#BAN_USER} und {@link Action#REPORT_USER} genutzt.
     * </i></p>
     * @return die zur Aktion gehörigen Argumente.
     */
    public @NotNull String[] getArguments() {
        return this.arguments;
    }

    /**
     * Eine Enumeration für die verschiedenen Verwaltungsmöglichkeiten eines Benutzers.
     */
    public enum Action {

        /**
         * Führt dazu, dass der Benutzer eine Freundschaftsanfrage erhält.
         */
        INVITE_FRIEND,

        /**
         * Führt dazu, dass der Benutzer als Freund entfernt wird.
         */
        REMOVE_FRIEND,

        /**
         * Führt dazu, dass der Benutzer ignoriert wird.
         */
        IGNORE_USER,

        /**
         * Führt dazu, dass der Benutzer nicht mehr ignoriert wird.
         */
        UNIGNORE_USER,

        /**
         * Führt dazu, dass der Benutzer stumm geschaltet wird.
         */
        MUTE_USER,

        /**
         * Führt dazu, dass die Stummschaltung des Benutzers aufgehoben wird.
         */
        UNMUTE_USER,

        /**
         * Führt dazu, dass der Benutzer aus der aktuellen Welt gesperrt wird.
         */
        BAN_USER,

        /**
         * Führt dazu, dass der Benutzer von der aktuellen Welt entsperrt wird.
         */
        UNBAN_USER,

        /**
         * Führt dazu, dass der Benutzer in der aktuellen Welt gemeldet wird.
         */
        REPORT_USER,

        /**
         * Führt dazu, dass der Benutzer in der aktuellen Welt gewarnt wird.
         */
        WARN_USER,

        /**
         * Führt dazu, dass sich der eigene Benutzer zum anderen Benutzer teleportiert.
         */
        TELEPORT_TO_USER,

        /**
         * Führt dazu, dass der Benutzer eine Einladung in den privaten Raum erhält.
         */
        ROOM_INVITE,

        /**
         * Führt dazu, dass der Benutzer aus dem privaten Raum gekickt wird.
         */
        ROOM_KICK,

        /**
         * Führt dazu, dass der Benutzer die Rolle des Moderators in der aktuellen Welt erhält.
         */
        ASSIGN_MODERATOR,

        /**
         * Führt dazu, dass dem Benutzer die Rolle des Moderators in der aktuellen Welt entzogen wird.
         */
        WITHDRAW_MODERATOR,

        /**
         * Führt dazu, dass der Benutzer die Rolle des Administrators erhält.
         */
        ASSIGN_ADMINISTRATOR,

        /**
         * Führt dazu, dass dem Benutzer die Rolle des Administrators entzogen wird.
         */
        WITHDRAW_ADMINISTRATOR,
    }
}
