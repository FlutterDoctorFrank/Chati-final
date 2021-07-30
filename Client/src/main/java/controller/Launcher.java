package controller;

import controller.network.ClientNetworkManager;
import model.Context.Global.GlobalContext;
import model.User.UserManager;
import view.Chati;

public class Launcher {

    public static void main(String[] args) {
        final GlobalContext global = GlobalContext.getInstance();
        final UserManager userManager = UserManager.getInstance();
        final ClientNetworkManager network = new ClientNetworkManager(global, userManager);

        network.start();
        global.setIModelObserver(new Chati(network.getConnection()).getScreen().getHud());
    }
}
