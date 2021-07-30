package controller;

import controller.network.ServerNetworkManager;
import model.context.global.GlobalContext;
import model.user.account.UserAccountManager;

public class Launcher {

    public static void main(String[] args) {
        final ServerNetworkManager network = new ServerNetworkManager(GlobalContext.getInstance(), UserAccountManager.getInstance());

        network.start();
    }
}
