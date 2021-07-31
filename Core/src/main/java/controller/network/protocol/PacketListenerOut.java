package controller.network.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Eine Schnittstelle für die Verarbeitung von Netzwerkpaketen, welche ausschließlich vom
 * Server an den Client geschickt werden.
 * Diese Schnittstelle ist beim Client zu implementieren.
 */
public interface PacketListenerOut extends PacketListener {

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketOutContextInfo packet);

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketOutContextJoin packet);

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketOutContextMusic packet);

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketOutContextRole packet);

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketOutMenuAction packet);

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketOutUserInfo packet);
}
