package view.Screens;

import controller.network.ServerSender;
import model.context.spatial.Menu;
import view.Chati;
import view.UIComponents.Hud;
import view.UIComponents.LoginTable;

public class MenuScreen extends ApplicationScreen {
    ServerSender serverSender;


    public MenuScreen (Chati game) {
        super(game);
        this.hud = new Hud(spriteBatch, this);
        hud.addMenuTable(new LoginTable(hud));
    }

    public MenuScreen (Chati game, Hud hud) {
        super(game);
        this.hud = hud;
    }

}
