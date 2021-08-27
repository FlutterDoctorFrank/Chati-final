package view2.component.world.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.Menu;
import view2.Assets;
import view2.Chati;

public class PortalWindow extends InteractableWindow {

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 350;
    private static final float ROW_WIDTH = 450;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;

    private Label infoLabel;
    private TextButton confirmButton;
    private TextButton cancelButton;
    private TextButton closeButton;

    public PortalWindow(ContextID portalId) {
        super("Raum verlassen", portalId, Menu.PORTAL_MENU);
        create();
        setLayout();
    }

    @Override
    protected void create() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        infoLabel = new Label("Möchtest du den Raum wirklich verlassen?", style);

        confirmButton = new TextButton("Ja", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], 1);
            }
        });

        cancelButton = new TextButton("Nein", Assets.SKIN);
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

        closeButton = new TextButton("X", Assets.SKIN);
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
    }

    @Override
    protected void setLayout() {
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        Table labelContainer = new Table();
        labelContainer.add(infoLabel).center();

        add(labelContainer).top().width(ROW_WIDTH).height(ROW_HEIGHT).spaceTop(VERTICAL_SPACING)
                .spaceBottom(VERTICAL_SPACING).expandY().row();

        Table buttonContainer = new Table();
        buttonContainer.add(confirmButton).width((ROW_WIDTH - HORIZONTAL_SPACING) / 2).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
        buttonContainer.add(cancelButton).width((ROW_WIDTH - HORIZONTAL_SPACING) / 2).height(ROW_HEIGHT);
        add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT).spaceBottom(VERTICAL_SPACING);

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void showMessage(String messageKey) {

    }
}
