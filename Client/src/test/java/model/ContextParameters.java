package model;

import model.context.ContextID;

public class ContextParameters {
    public static int TILE_LENGTH = 32;

    public static String WORLD_NAME = "World";
    public static ContextID WORLD_ID = new ContextID("Global.World");

    public static String ROOM_NAME = "Room";
    public static ContextID ROOM_ID = new ContextID("Global.World.Room");

    public static String PRIVATE_ROOM_NAME = "PrivateRoom";
    public static ContextID PRIVATE_ROOM_ID =  new ContextID("Global.World.PrivateRoom");
    public static float PRIVATE_ROOM_LOCATION_X = 40;
    public static float PRIVATE_ROOM_LOCATION_Y = 40;

    public static ContextID DISCO_ID = new ContextID("Global.World.Room.Disco");
    public static float DISCO_LOCATION_X = 500;
    public static float DISCO_LOCATION_Y = 1500;

    public static ContextID DISCO_Jukebox_ID = new ContextID("Global.World.Room.Disco.Jukebox");
    public static float DISCO_LOCATION_JUKEBOX_INTERACT_X = 930;
    public static float DISCO_LOCATION_JUKEBOX_INTERACT_Y = 1806;

    public static ContextID PARK_Id = new ContextID("Global.World.Room.Park");
    public static float PARK_LOCATION_X = 1200;
    public static float PARK_LOCATION_Y = 80;
}
