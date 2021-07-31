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
import java.util.HashSet;
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
     * Da KryoNet momentan nur eine maximale Paketgröße von 512 Bytes erlaubt, werden über dieses Netzwerkpaket nur die
     * Benutzerinformationen eines einzelnen Benutzers gesendet.
     * Die Benutzerinformationen eines einzelnen Benutzers können bis zu ~64 Bytes benötigen. Werden mehrere
     * Benutzerinformationen dieser Größe versendet, so wird die maximale Paketgröße bereits nach wenigen Benutzern
     * überschritten.
     */
    private UserInfo info;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
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
        PacketUtils.writeContextId(output, this.contextId);
        PacketUtils.writeEnum(output, this.action);

        // Schreiben der Benutzerinformation
        PacketUtils.writeUniqueId(output, this.info.userId);
        output.writeAscii(this.info.name);
        PacketUtils.writeNullableEnum(output, this.info.avatar);
        PacketUtils.writeNullableEnum(output, this.info.status);
        output.writeBoolean(this.info.teleportTo);
        output.writeVarInt(this.info.mutes.size(), true);
        for (final ContextID context : this.info.mutes) {
            PacketUtils.writeContextId(output, context);
        }

        /*
         * Durch die Beschränkung der Paketgröße von 512 Bytes werden die gesetzten Flags der Benutzerinformation
         * in ein Bitfeld geschrieben und versendet. Dadurch können im besten Fall 6 Bytes gespart werden.
         */
        int bitfield = 0;

        for (final UserInfo.Flag flag : this.info.flags) {
            bitfield = bitfield | 1 << flag.ordinal();
        }

        output.writeVarInt(bitfield, true);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);
        this.action = PacketUtils.readEnum(input, Action.class);

        // Lesen der Benutzerinformation
        this.info = new UserInfo(PacketUtils.readUniqueId(input));
        this.info.name = input.readString();
        this.info.avatar = PacketUtils.readNullableEnum(input, Avatar.class);
        this.info.status = PacketUtils.readNullableEnum(input, Status.class);
        this.info.teleportTo = input.readBoolean();
        this.info.mutes = new HashSet<>();
        for (int index = 0; index < input.readVarInt(true); index++) {
            this.info.mutes.add(PacketUtils.readContextId(input));
        }

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
        private boolean teleportTo;

        private Set<ContextID> mutes;
        private Set<Flag> flags;

        public UserInfo(@NotNull final UUID userId) {
            this.userId = userId;
            this.mutes = new HashSet<>();
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

        public void setAvatar(@NotNull final Avatar avatar) {
            this.avatar = avatar;
        }

        /**
         * Gibt den Online-Status des Benutzers zurück.
         * @return der aktuelle Status des Benutzers.
         */
        public @Nullable Status getStatus() {
            return this.status;
        }

        public void setStatus(@NotNull final Status status) {
            this.status = status;
        }

        /**
         * Gibt die Kontext-IDs, in denen der Benutzer stumm geschaltet ist, zurück.
         * @return die Kontexte in denen der Benutzer stumm geschaltet ist.
         */
        public @NotNull Set<ContextID> getMutes() {
            return this.mutes;
        }

        public void addMute(@NotNull final ContextID contextId) {
            this.mutes.add(contextId);
        }

        /**
         * Gibt die zusätzlichen Flags des Benutzers zurück.
         * @return die gesetzten Flags des Benutzers.
         */
        public @NotNull Set<Flag> getFlags() {
            return this.flags;
        }

        public void addFlag(@NotNull final Flag flag) {
            this.flags.add(flag);
        }

        /**
         * Gibt zurück, ob der Benutzer durch Teleportieren erreichbar ist.
         * @return true, wenn der Benutzer erreichbar ist, ansonsten false.
         */
        public boolean getTeleportTo() {
            return this.teleportTo;
        }

        public void setTeleportTo(final boolean canTeleportTo) {
            this.teleportTo = canTeleportTo;
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
         * Eine Enumeration für die weiteren Informationen über einem Benutzer.
         */
        public enum Flag {

            /**
             * Information, dass der Benutzer gemeldet wurde.
             */
            REPORTED,

            /**
             * Information, dass der Benutzer ein Freund ist.
             */
            FRIEND,

            /**
             * Information, dass der Benutzer ignoriert wird.
             */
            IGNORED,

            /**
             * Information, dass der Benutzer aus der Welt gesperrt ist.
             */
            BANNED,
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
