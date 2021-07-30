package view.Screens;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import controller.network.ServerConnection;
import controller.network.ServerSender;
import view.Chati;
import view.UIComponents.Hud;
import view.UIComponents.LoginTable;

public class MenuScreen extends ApplicationScreen {
    ServerSender serverSender;

    public MenuScreen (Chati game) {
        super(game);
        hud.addTable(new LoginTable(hud));
    }

}
