package view.userInterface.hud.settings;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.ChatiLocalization;
import view.userInterface.actor.ChatiLabel;
import view.userInterface.actor.ChatiSelectBox;
import view.userInterface.actor.ChatiTextButton;
import view.userInterface.actor.ChatiWindow;
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
        super("window.title.select-language", WINDOW_WIDTH, WINDOW_HEIGHT);

        ChatiLabel infoLabel = new ChatiLabel("window.entry.select-language");
        ChatiSelectBox<Locale> languageSelectBox = new ChatiSelectBox<>(locale ->
                locale.getDisplayLanguage(Chati.CHATI.getLocalization().getLocale()));
        languageSelectBox.setItems(ChatiLocalization.AVAILABLE_LOCALES);
        languageSelectBox.setSelected(Chati.CHATI.getLocalization().getLocale());

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getPreferences().setLanguage(languageSelectBox.getSelected());
                Chati.CHATI.translate();
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
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACE).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        Table languageSelectContainer = new Table();
        languageSelectContainer.add(languageSelectBox).growX();
        container.add(languageSelectContainer).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACE / 2);
        buttonContainer.add(cancelButton).padLeft(SPACE / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACE).padRight(SPACE).grow();

        // Translatable register
        translatables.add(infoLabel);
        translatables.add(confirmButton);
        translatables.add(cancelButton);
        translatables.trimToSize();
    }

    @Override
    public void focus() {
    }
}
