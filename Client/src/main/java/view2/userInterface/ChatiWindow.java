package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Chati;

public abstract class ChatiWindow extends Window {

    protected static final float ROW_HEIGHT = 60;
    protected static final float SPACING = 15;

    protected ChatiWindow(String title) {
        super(title, Chati.CHATI.getSkin());

        ChatiTextButton closeButton = new ChatiTextButton("X", true);
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                close();
            }
        });

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    public void open() {
        Chati.CHATI.getScreen().getStage().openWindow(this);
    }

    public void close() {
        Chati.CHATI.getScreen().getStage().closeWindow(this);
    }
}