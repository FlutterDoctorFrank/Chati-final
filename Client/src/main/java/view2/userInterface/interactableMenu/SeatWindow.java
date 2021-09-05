package view2.userInterface.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import view2.Chati;
import view2.userInterface.ChatiTextButton;

public class SeatWindow extends InteractableWindow {

    private static final int MENU_OPTION_SIT = 1;

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 275;

    public SeatWindow(ContextID seatId) {
        super("Hinsetzen", seatId, ContextMenu.SEAT_MENU);

        Label infoLabel = new Label("Möchtest du hier Platz nehmen?", Chati.CHATI.getSkin());

        ChatiTextButton confirmButton = new ChatiTextButton("Ja", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_SIT);
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("Nein", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                close();
            }
        });

        // Layout
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
    }
}
