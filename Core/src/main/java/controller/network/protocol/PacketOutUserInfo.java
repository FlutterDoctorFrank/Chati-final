package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import model.user.Avatar;
import model.user.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über die aktiven Benutzer innerhalb eines Kontextes enthält.
 * <p>
 *     Das Paket wird vom Server erzeugt und einen den Client gesendet.
 *     Das Paket teilt dem Client die Freunde, die aktiven Benutzer, etc... , mit.
 * </p>
 */
public class PacketOutUserInfo implements Packet<PacketListenerOut> {

    private ContextID contextId;
    private Action action;

    /*
     * Um die maximale Paketgröße beim Senden vieler Benutzerinformationen nicht zu überschreiten, wird vorerst über
     * dieses Netzwerkpaket nur die Benutzerinformation eines einzelnen Benutzers gesendet.
     */
    private UserInfo info;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketOutUserInfo() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param contextId die ID des Kontexts, in dem die Benutzerinformation gilt.
     * @param action die Aktion die auf die Benutzerinformation ausgeführt werden soll.
     * @param info die Benutzerinformation des Benutzers.
     */
    public PacketOutUserInfo(@Nullable final ContextID contextId, @NotNull final Action action, @NotNull final UserInfo info) {
        this.contextId = contextId;
        this.action = action;
        this.info = info;
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableContextId(output, this.contextId);
        PacketUtils.writeEnum(output, this.action);

        // Schreiben der Benutzerinformation
        PacketUtils.writeUniqueId(output, this.info.userId);
        output.writeAscii(this.info.name);
        PacketUtils.writeNullableEnum(output, this.info.avatar);
        PacketUtils.writeNullableEnum(output, this.info.status);
        PacketUtils.writeNullableContextId(output, this.info.world);
        PacketUtils.writeNullableContextId(output, this.info.room);
        output.writeBoolean(this.info.privateRoom);

        /*
         * Um die gesetzten Flags der Benutzerinformation in serialisierter Form kompakt zu halten, werden die Flags
         * in ein Bitfeld geschrieben. Dadurch können im besten Fall 6 Bytes gespart werden.
         */
        int bitfield = 0;

        for (final UserInfo.Flag flag : this.info.flags) {
            bitfield = bitfield | 1 << flag.ordinal();
        }

        output.writeVarInt(bitfield, true);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readNullableContextId(input);
        this.action = PacketUtils.readEnum(input, Action.class);

        // Lesen der Benutzerinformation
        this.info = new UserInfo(PacketUtils.readUniqueId(input));
        this.info.name = input.readString();
        this.info.avatar = PacketUtils.readNullableEnum(input, Avatar.class);
        this.info.status = PacketUtils.readNullableEnum(input, Status.class);
        this.info.world = PacketUtils.readNullableContextId(input);
        this.info.room = PacketUtils.readNullableContextId(input);
        this.info.privateRoom = input.readBoolean();

        /*
         * Wie in der #write()-Methode beschrieben, werden die gesetzten Flags in ein Bitfeld geschrieben und
         * versendet. Das Bitfeld muss nun wieder in ein Flag Array transformiert werden.
         */
        final int bitfield = input.readVarInt(true);
        this.info.flags = EnumSet.noneOf(UserInfo.Flag.class);

        for (int position = 0; position < UserInfo.Flag.values().length; position++) {
            if ((bitfield & (1 << position)) != 0) {
                this.info.flags.add(UserInfo.Flag.class.getEnumConstants()[position]);
            }
        }
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + ", action=" + this.action
                + ", info=" + this.info + "}";
    }

    /**
     * Gibt die Kontext-ID der Welt oder null, wenn es sich um den global Kontext handelt, zurück.
     * @return die Kontext-ID oder null.
     */
    public @Nullable ContextID getContextId() {
        return this.contextId;
    }

    /**
     * Gibt die Aktion, die auf die Benutzerinformation ausgeführt werden soll, zurück.
     * @return die auszuführende Aktion.
     */
    public @NotNull Action getAction() {
        return this.action;
    }

    /**
     * Gibt die Benutzerinformationen des Benutzers zurück.
     * @return die Informationen über den Benutzer.
     */
    public @NotNull UserInfo getInfo() {
        return this.info;
    }

    /**
     * Eine Klasse, die die Informationen eines einzelnen Benutzers hält.
     */
    public static class UserInfo {

        private final UUID userId;

        private String name;
        private Avatar avatar;
        private Status status;
        private ContextID world;
        private ContextID room;
        private boolean privateRoom;

        private Set<Flag> flags;

        public UserInfo(@NotNull final UUID userId) {
            this.userId = userId;
            this.flags = EnumSet.noneOf(Flag.class);
        }

        public UserInfo(@NotNull final UUID userId, @NotNull final String name) {
            this(userId);

            this.name = name;
        }

        public UserInfo(@NotNull final UUID userId, @NotNull final String name, @NotNull final Status status) {
            this(userId);

            this.name = name;
            this.status = status;
        }

        /**
         * Gibt die Benutzer-ID des Benutzers zurück.
         * @return die ID des Benutzers.
         */
        public @NotNull UUID getUserId() {
            return this.userId;
        }

        /**
         * Gibt den Benutzernamen des Benutzers zurück.
         * @return der Name des Benutzers.
         */
        public @Nullable String getName() {
            return this.name;
        }

        /**
         * Gibt den Avatar des Benutzers zurück.
         * @return der Avatar des Benutzers.
         */
        public @Nullable Avatar getAvatar() {
            return this.avatar;
        }

        /**
         * Setzt den Avatar des Benutzers.
         * @param avatar der Avatar des Benutzers.
         */
        public void setAvatar(@NotNull final Avatar avatar) {
            this.avatar = avatar;
        }

        /**
         * Gibt den Online-Status des Benutzers zurück.
         * @return der aktuelle Status des Benutzers.
         */
        public @NotNull Status getStatus() {
            return this.status != null ? this.status : Status.OFFLINE;
        }

        /**
         * Setzt den Online-Status des Benutzers.
         * @param status der aktuelle Status des Benutzers.
         */
        public void setStatus(@NotNull final Status status) {
            this.status = status;
        }

        /**
         * Gibt die zusätzlichen Flags des Benutzers zurück.
         * @return die gesetzten Flags des Benutzers.
         */
        public @NotNull Set<Flag> getFlags() {
            return this.flags;
        }

        /**
         * Fügt eine zusätzliche Flag zum Benutzer hinzu.
         * @param flag die hinzuzufügende Flag.
         */
        public void addFlag(@NotNull final Flag flag) {
            this.flags.add(flag);
        }

        /**
         * Gibt zurück, ob sich der Benutzer in einem privaten Raum befindet.
         * @return true, wenn der Benutzer in einem privaten Raum ist, ansonsten false.
         */
        public boolean getInPrivateRoom() {
            return this.privateRoom;
        }

        /**
         * Setzt, ob sich der Benutzer in einem privaten Raum befindet.
         * @param privateRoom true, wenn der Benutzer in einem privaten Raum ist, ansonsten false.
         */
        public void setInPrivateRoom(final boolean privateRoom) {
            this.privateRoom = privateRoom;
        }

        /**
         * Gibt zurück, in welcher Welt sich der Benutzer befindet.
         * @return die Welt des Benutzers oder null.
         */
        public @Nullable ContextID getWorld() {
            return this.world;
        }

        /**
         * Setzt, in welcher Welt sich der Benutzer befindet.
         * @param contextId die ID der Welt in der sich der Benutzer befindet.
         */
        public void setWorld(@NotNull final ContextID contextId) {
            this.world = contextId;
        }

        /**
         * Gibt zurück, in welchem Raum sich der Benutzer befindet.
         * @return der Raum des Benutzers oder null.
         */
        public @Nullable ContextID getRoom() {
            return this.room;
        }

        /**
         * Setzt, in welchem Raum sich der Benutzer befindet.
         * @param contextId die ID des Raumes in dem sich der Benutzer befindet.
         */
        public void setRoom(@NotNull final ContextID contextId) {
            this.room = contextId;
        }

        @Override
        public @NotNull String toString() {
            return "{userId=" + this.userId + ", name='" + this.name + "', avatar=" + this.avatar + ", status="
                    + this.status + ", world=" + this.world + ", room=" + this.room  + ", private="
                    + this.privateRoom + ", flags=" + this.flags + "}";
        }

        @Override
        public boolean equals(@Nullable final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            return this.userId.equals(((UserInfo) object).userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.userId);
        }

        /**
         * Eine Enumeration für die weiteren Informationen über einen Benutzer.
         */
        public enum Flag {

            /**
             * Information, dass der Benutzer ein Freund ist.
             */
            FRIEND,

            /**
             * Information, dass der Benutzer ignoriert wird.
             */
            IGNORED,

            /**
             * Information, dass der Benutzer aus der Welt gesperrt wurde.
             */
            BANNED,

            /**
             * Information, dass der Benutzer gemeldet wurde.
             */
            REPORTED,
        }
    }

    /**
     * Eine Enumeration für die verschiedenen Aktualisierungsmöglichkeiten der bekannten Benutzer.
     */
    public enum Action {

        /**
         * Führt dazu, dass die Benutzer aus der Liste entfernt werden.
         */
        REMOVE_USER,

        /**
         * Führt dazu, dass die Benutzer in der Liste aktualisiert oder hinzugefügt werden.
         */
        UPDATE_USER,
    }
}
