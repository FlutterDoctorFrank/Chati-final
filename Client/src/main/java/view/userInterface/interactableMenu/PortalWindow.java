package view.userInterface.interactableMenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Chati;
import view.userInterface.actor.ChatiLabel;
import view.userInterface.actor.ChatiTextButton;

/**
 * Eine Klasse, welche das Menü des Portals repräsentiert.
 */
public class PortalWindow extends InteractableWindow {

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 275;

    /**
     * Erzeugt eine neue Instanz des PortalWindow.
     * @param portalId ID des zugehörigen Portals.
     */
    public PortalWindow(@NotNull final ContextID portalId) {
        super("window.title.portal", portalId, ContextMenu.PORTAL_MENU, WINDOW_WIDTH, WINDOW_HEIGHT);

        infoLabel = new ChatiLabel("window.entry.portal");

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.yes", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], 1);
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.no", true);
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
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
    }

    @Override
    public void focus() {
    }
}
