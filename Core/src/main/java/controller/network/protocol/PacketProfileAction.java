package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.MessageBundle;
import model.user.Avatar;
import model.user.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über die verschiedenen Aktionen auf ein Benutzerprofil enthält.
 * <p>
 *     Das Paket wird von einem Client mit den benötigten Informationen erzeugt und an den Server gesendet.
 *     Nach der Verarbeitung des Pakets vom Server werden die Informationen des Pakets aktualisiert und das Paket
 *     zurück an den Client gesendet.
 * </p>
 */
public class PacketProfileAction implements Packet<PacketListener> {

    private UUID userId;
    private String name;
    private String password;
    private String newPassword;
    private Avatar avatar;
    private Status status;

    private Action action;
    private MessageBundle message;
    private boolean success;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketProfileAction() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Einloggen oder Registrieren von der Client-Anwendung.
     * @param name der Benutzername des anzumeldenden oder registrierenden Benutzers.
     * @param password das Password zum Anmelden oder Registrieren.
     * @param register true, wenn der Benutzer registriert werden soll, ansonsten false.
     */
    public PacketProfileAction(@NotNull final String name, @NotNull final String password, final boolean register) {
        this.action = register ? Action.REGISTER : Action.LOGIN;
        this.name = name;
        this.password = password;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Ändern des Passworts von der Client-Anwendung.
     * @param password das aktuelle Passwort des Benutzers.
     * @param newPassword das neue Passwort für den Benutzer.
     */
    public PacketProfileAction(@NotNull final String password, @NotNull final String newPassword) {
        this.action = Action.CHANGE_PASSWORD;
        this.password = password;
        this.newPassword = newPassword;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Ändern des Avatars von der Client-Anwendung.
     * @param avatar der neue Avatar für den Benutzer.
     */
    public PacketProfileAction(@NotNull final Avatar avatar) {
        this.action = Action.CHANGE_AVATAR;
        this.avatar = avatar;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Ändern des Status von der Client-Anwendung.
     * @param status der neue Status für den Benutzer.
     */
    public PacketProfileAction(@NotNull final Status status) {
        this.action = Action.CHANGE_STATUS;
        this.status = status;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Ausloggen oder Löschen von der Client-Anwendung.
     * @param password das aktuelle Passwort des Benutzers, oder null, wenn der Benutzer ausgeloggt werden soll.
     * @param delete true, wenn der Benutzer gelöscht werden soll, ansonsten false.
     */
    public PacketProfileAction(@Nullable final String password, final boolean delete) {
        if (delete && password == null) {
            throw new IllegalArgumentException("Password can not be null, when delete is set to true");
        }

        this.action = delete ? Action.DELETE : Action.LOGOUT;
        this.password = password;
    }

    /**
     * Ausschließlich für die Erzeugung einer Antwort des Netzwerkpakets von der Server-Anwendung.
     * @param previous das Netzwerkpaket auf das geantwortet werden soll.
     * @param message die Nachricht, die durch die Aktion erzeugt wurde. Null, falls keine erzeugt wurde.
     * @param success true, wenn die Aktion erfolgreich war, ansonsten false.
     */
    public PacketProfileAction(@NotNull final PacketProfileAction previous,
                               @Nullable final MessageBundle message, final boolean success) {
        this.name = previous.getName();
        this.password = previous.getPassword();
        this.newPassword = previous.getNewPassword();
        this.avatar = previous.getAvatar();
        this.action = previous.getAction();
        this.message = message;
        this.success = success;
    }

    /**
     * Ausschließlich für die Erzeugung einer Antwort des Netzwerkpakets zum Login von der Server-Anwendung.
     * @param previous das Netzwerkpaket auf das geantwortet werden soll.
     * @param userId die Benutzer-ID des angemeldeten Benutzers.
     * @param message der Schlüssel der Nachricht, die durch die Aktion erzeugt wurde. Null, falls keine erzeugt wurde.
     * @param success true, wenn die Aktion erfolgreich war, ansonsten false.
     */
    public PacketProfileAction(@NotNull final PacketProfileAction previous, @NotNull final UUID userId,
                               @NotNull final Avatar avatar, @Nullable final MessageBundle message, final boolean success) {
        this(previous, message, success);

        this.userId = userId;
        this.avatar = avatar;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableUniqueId(output, this.userId);
        output.writeString(this.name);
        output.writeString(this.password);
        output.writeString(this.newPassword);
        PacketUtils.writeNullableEnum(output, this.avatar);
        PacketUtils.writeNullableEnum(output, this.status);
        PacketUtils.writeEnum(output, this.action);
        PacketUtils.writeNullableBundle(kryo, output, this.message);
        output.writeBoolean(this.success);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.userId = PacketUtils.readNullableUniqueId(input);
        this.name = input.readString();
        this.password = input.readString();
        this.newPassword = input.readString();
        this.avatar = PacketUtils.readNullableEnum(input, Avatar.class);
        this.status = PacketUtils.readNullableEnum(input, Status.class);
        this.action = PacketUtils.readEnum(input, Action.class);
        this.message = PacketUtils.readNullableBundle(kryo, input);
        this.success = input.readBoolean();
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{userId=" + this.userId + ", name='" + this.name + "', password='"
                + this.password + "', newPassword='" + this.newPassword + "', avatar=" + this.avatar + ", action="
                + this.action + ", message='" + this.message + "', success=" + this.success + "}";
    }

    /**
     * Gibt die Benutzer-ID des Benutzerprofils zurück.
     * @return die ID des Benutzers, oder null.
     */
    public @Nullable UUID getUserId() {
        return this.userId;
    }

    /**
     * Gibt den Namen des Benutzerprofils zurück.
     * @return den Namen des Benutzers, oder null.
     */
    public @Nullable String getName() {
        return this.name;
    }

    /**
     * Gibt das Password des Benutzerprofils zurück.
     * @return das Password des Benutzers, oder null.
     */
    public @Nullable String getPassword() {
        return this.password;
    }

    /**
     * Gibt das neue Password des Benutzerprofils zurück.
     * @return das neue Password des Benutzers, oder null.
     */
    public @Nullable String getNewPassword() {
        return this.newPassword;
    }

    /**
     * Gibt den Avatar des Benutzerprofils zurück.
     * @return der Avatar des Benutzers, oder null.
     */
    public @Nullable Avatar getAvatar() {
        return this.avatar;
    }

    /**
     * Gibt den Status des Benutzerprofils zurück.
     * @return der Status des Benutzers, oder null.
     */
    public @Nullable Status getStatus() {
        return this.status;
    }

    /**
     * Gibt die Aktion, die auf das Benutzerprofil ausgeführt werden soll, zurück.
     * @return die Aktion, die ausgeführt werden soll.
     */
    public @NotNull Action getAction() {
        return this.action;
    }

    /**
     * Gibt die Nachrichten einer Fehlermeldung zurück.
     * @return die Fehlernachricht, oder null.
     */
    public @Nullable MessageBundle getMessage() {
        return this.message;
    }

    /**
     * Gibt zurück, ob die Aktion erfolgreich war.
     * @return true, wenn die Aktion erfolgreich war, ansonsten false.
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Eine Enumeration für die verschiedenen Profil-Aktionen.
     */
    public enum Action {

        /**
         * Führt dazu, dass ein neues Profil angelegt wird.
         */
        REGISTER,

        /**
         * Führt dazu, dass ein vorhandenes Profil gelöscht wird.
         */
        DELETE,

        /**
         * Führt dazu, dass ein vorhandenes Profil angemeldet wird.
         */
        LOGIN,

        /**
         * Führt dazu, dass ein angemeldetes Profil abgemeldet wird.
         */
        LOGOUT,

        /**
         * Führt dazu, dass das Password eines vorhandenen Profils geändert wird.
         */
        CHANGE_PASSWORD,

        /**
         * Führt dazu, dass der Avatar eines vorhandenen Profils geändert wird.
         */
        CHANGE_AVATAR,

        /**
         * Führt dazu, dass der Status eines vorhandenen Profils geändert wird.
         */
        CHANGE_STATUS
    }
}
