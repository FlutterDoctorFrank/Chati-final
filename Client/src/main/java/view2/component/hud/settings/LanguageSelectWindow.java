package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import view2.Chati;
import view2.component.ChatiWindow;
import view2.component.ChatiTextButton;

public class LanguageSelectWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 350;

    public LanguageSelectWindow() {
        super("Sprache auswählen");

        Label infoLabel = new Label("Wähle eine Sprache aus!", Chati.CHATI.getSkin());

        SelectBox<String> languageSelectBox = new SelectBox<>(Chati.CHATI.getSkin());
        // TODO

        ChatiTextButton confirmButton = new ChatiTextButton("Bestätigen", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // TODO
                close();
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("Abbrechen", true);
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
        Table languageSelectContainer = new Table();
        languageSelectContainer.add(languageSelectBox).growX();
        container.add(languageSelectContainer).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();
    }
}
