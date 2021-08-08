package controller;

import controller.network.ClientNetworkManager;
import model.user.UserManager;
import view.Chati;

public class Launcher {

    public static void main(String[] args) {
        /*
        final UserManager userManager = UserManager.getInstance();
        final ClientNetworkManager network = new ClientNetworkManager(userManager);
        final Thread controller = new Thread(network::start);
        controller.setDaemon(true);
        controller.start();
        userManager.setModelObserver(new Chati(network.getConnection(), null, userManager).getScreen().getHud());
         */

        new Chati();
    }
}
