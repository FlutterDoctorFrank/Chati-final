package controller.network.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Eine Schnittstelle für die Verarbeitung von Netzwerkpaketen, welche ausschließlich vom
 * Client an den Server geschickt werden.
 * Diese Schnittstelle ist beim Server zu implementieren.
 */
public interface PacketListenerIn extends PacketListener {

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketInContextInteract packet);
}
