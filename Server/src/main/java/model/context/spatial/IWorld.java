package model.context.spatial;

import model.context.ContextID;

import java.util.Map;

public interface IWorld extends IRoom {

    /**
     * Gibt die Menge aller enthaltenen privaten Räume dieser Welt zurück.
     * @return Menge aller privaten Räume.
     */
    Map<ContextID, Room> getPrivateRooms();
}