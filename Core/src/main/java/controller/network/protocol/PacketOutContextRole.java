package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import model.role.Role;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;

/**
 * Ein Paket, das Informationen über die Rollen innerhalb eines Kontexts enthält.
 * Das Paket wird vom Server erzeugt und an einen Client gesendet.
 * Das Paket teilt dem Client die Rollen und somit die Berechtigung mit, die dieser Benutzer in einem Kontext besitzt.
 */
public class PacketOutContextRole implements Packet<PacketListenerOut> {

    private ContextID contextId;
    private Role[] roles;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    public PacketOutContextRole() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param contextId die ID des Kontexts in der die Rolle gesetzt wird.
     * @param roles die neue Rollen die gesetzt werden sollen.
     */
    public PacketOutContextRole(@NotNull final ContextID contextId, @NotNull final Collection<Role> roles) {
        this.contextId = contextId;
        this.roles = roles.toArray(new Role[0]);
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeContextId(output, this.contextId);
        output.writeInt(this.roles.length, true);

        for (final Role role : this.roles) {
            PacketUtils.writeEnum(output, role);
        }
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);
        this.roles = new Role[input.readInt(true)];

        for (int index = 0; index < this.roles.length; index++) {
            this.roles[index] = PacketUtils.readEnum(input, Role.class);
        }
    }

    /**
     * Gibt die Kontext-ID des geltenden Kontexts der Rollen zurück.
     * @return die ID des Kontexts
     */
    public @NotNull ContextID getContextId() {
        return this.contextId;
    }

    /**
     * Gibt die neuen Rollen innerhalb des Kontexts zurück.
     * @return die neuen Rollen im Kontext.
     */
    public @NotNull Role[] getRoles() {
        return this.roles;
    }
}
