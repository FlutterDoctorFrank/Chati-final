package view2.userInterface.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.Localization;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiSelectBox;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiWindow;
import view2.userInterface.hud.HeadUpDisplay;
import java.util.Locale;

/**
 * Eine Klasse, welche das Menü zum Ändern der Sprache repräsentiert.
 */
public class LanguageSelectWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 350;

    /**
     * Erzeugt eine neue Instanz des LanguageSelectWindow.
     */
    public LanguageSelectWindow() {
        super("window.title.select-language");

        ChatiLabel infoLabel = new ChatiLabel("window.entry.select-language");
        ChatiSelectBox<Locale> languageSelectBox = new ChatiSelectBox<>(locale -> locale.getDisplayLanguage(Localization.locale()));
        languageSelectBox.setItems(Localization.AVAILABLE_LOCALES);

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Localization.getInstance().load(languageSelectBox.getSelected());
                Chati.CHATI.getPreferences().setLanguage(languageSelectBox.getSelected());
                Chati.CHATI.getMenuScreen().translate();
                Chati.CHATI.getWorldScreen().translate();
                HeadUpDisplay.getInstance().translate();
                close();
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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

        // Translatable register
        translates.add(infoLabel);
        translates.add(confirmButton);
        translates.add(cancelButton);
        translates.trimToSize();
    }
}
