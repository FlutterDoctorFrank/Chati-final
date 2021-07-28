package controller.network.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Eine Schnittstelle, die von ankommenden Netzwerkpaketen aufgerufen wird, um die Verarbeitung
 * des Pakets anzustoßen.
 */
public interface PacketListener {

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketAvatarMove packet);

    /**
     * Prüft die Korrektheit der Daten des Pakets und verarbeitet anschließend das Paket.
     * @param packet Das zu verarbeitende Paket.
     */
    void handle(@NotNull final PacketWorldAction packet);
}
