package model.context.spatial;

import model.context.ContextID;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public interface IWorld extends IArea {

    /**
     * Gibt den öffentlichen Raum dieser Welt zurück.
     * @return Öffentlicher Raum der Welt.
     */
    @NotNull Room getPublicRoom();

    /**
     * Gibt die Menge aller enthaltenen privaten Räume dieser Welt zurück.
     * @return Menge aller privaten Räume.
     */
    @NotNull Map<ContextID, Room> getPrivateRooms();
}