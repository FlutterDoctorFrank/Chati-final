package view.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenHandler {

    private Game game;

    public ScreenHandler(Game game) {
        this.game = game;
    }

    public void setScreen(Screen screen) {
        game.setScreen(screen);
    }
}
