package controller;

import controller.network.ClientNetworkManager;
import model.context.global.GlobalContext;
import model.user.UserManager;
import view.Chati;

public class Launcher {

    public static void main(String[] args) {

        final GlobalContext global = GlobalContext.getInstance();
        final UserManager userManager = UserManager.getInstance();
        final ClientNetworkManager network = new ClientNetworkManager(global, userManager);

        network.start();
        global.setIModelObserver(new Chati(network.getConnection(), global, userManager).getScreen().getHud());


        //new Chati();
    }



}
