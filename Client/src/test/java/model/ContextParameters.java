package model;

import model.context.ContextID;

import java.util.Set;

public class ContextParameters {
    public static int TILE_LENGTH = 32;

    public static String GLOBAL_NAME = "Global";

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

    public static ContextID SEAT_ID = new ContextID("Global.World.Room.Hotel.Bank_12.Seat_24");

    public static Set<String> CONTEXT_ID_SET = Set.of(
            "Global",
            "Global.World",
            "Global.World.Room",
            "Global.World.Room.Park",
            "Global.World.Room.Disco",
            "Global.World.Room.Hotel",
            "Global.World.Room.Park.Spawn",
            "Global.World.Room.Hotel.Reception",
            "Global.World.Room.Park.Podium",
            "Global.World.Room.Disco.Bar",
            "Global.World.Room.Disco.Table_1",
            "Global.World.Room.Disco.Table_2",
            "Global.World.Room.Disco.Table_1.GameBoard_1",
            "Global.World.Room.Disco.Table_2.GameBoard_2",
            "Global.World.Room.Hotel.Bank_11",
            "Global.World.Room.Hotel.Bank_12",
            "Global.World.Room.Disco.Jukebox",
            "Global.World.Room.Hotel.Bank_8",
            "Global.World.Room.Park.Bank_3",
            "Global.World.Room.Park.Bank_6",
            "Global.World.Room.Hotel.Bank_7",
            "Global.World.Room.Hotel.Bank_9",
            "Global.World.Room.Hotel.Bank_10",
            "Global.World.Room.Park.Bank_1",
            "Global.World.Room.Park.Bank_2",
            "Global.World.Room.Park.Bank_4",
            "Global.World.Room.Park.Bank_5",
            "Global.World.Room.Park.Podium.Area_Planner",
            "Global.World.Room.Hotel.Bank_11.Seat_21",
            "Global.World.Room.Hotel.Bank_11.Seat_22",
            "Global.World.Room.Hotel.Bank_11.Seat_23",
            "Global.World.Room.Hotel.Bank_12.Seat_24",
            "Global.World.Room.Hotel.Bank_12.Seat_25",
            "Global.World.Room.Hotel.Bank_12.Seat_26",
            "Global.World.Room.Disco.Bar.Seat_27",
            "Global.World.Room.Disco.Bar.Seat_28",
            "Global.World.Room.Disco.Bar.Seat_29",
            "Global.World.Room.Disco.Bar.Seat_30",
            "Global.World.Room.Park.Bank_3.Seat_5",
            "Global.World.Room.Park.Bank_3.Seat_6",
            "Global.World.Room.Park.Bank_6.Seat_11",
            "Global.World.Room.Park.Bank_6.Seat_12",
            "Global.World.Room.Hotel.Bank_7.Seat_13",
            "Global.World.Room.Hotel.Bank_7.Seat_14",
            "Global.World.Room.Hotel.Bank_8.Seat_15",
            "Global.World.Room.Hotel.Bank_8.Seat_16",
            "Global.World.Room.Hotel.Bank_9.Seat_17",
            "Global.World.Room.Hotel.Bank_9.Seat_18",
            "Global.World.Room.Hotel.Bank_10.Seat_19",
            "Global.World.Room.Hotel.Bank_10.Seat_20",
            "Global.World.Room.Park.Bank_1.Seat_1",
            "Global.World.Room.Park.Bank_1.Seat_2",
            "Global.World.Room.Park.Bank_2.Seat_3",
            "Global.World.Room.Park.Bank_2.Seat_4",
            "Global.World.Room.Park.Bank_4.Seat_7",
            "Global.World.Room.Park.Bank_4.Seat_8",
            "Global.World.Room.Park.Bank_5.Seat_9",
            "Global.World.Room.Park.Bank_5.Seat_10");
}
