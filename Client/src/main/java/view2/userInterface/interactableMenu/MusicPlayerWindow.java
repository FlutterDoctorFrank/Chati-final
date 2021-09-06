package view2.userInterface.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.context.spatial.ContextMusic;
import view2.Chati;

import java.util.EnumSet;

@Deprecated
public class MusicPlayerWindow extends InteractableWindow {

    private static final int MENU_OPTION_PLAY = 1;
    private static final int MENU_OPTION_STOP = 2;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 350;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;

    private Label infoLabel;
    private Label musicSelectLabel;
    private SelectBox<ContextMusic> musicSelectBox;
    private TextButton playButton;
    private TextButton stopButton;
    private TextButton randomButton;
    private TextButton cancelButton;
    private Label creditsLabel;
    private TextButton closeButton;

    public MusicPlayerWindow(ContextID musicPlayerId) {
        super("Jukebox", musicPlayerId, ContextMenu.MUSIC_PLAYER_MENU);
        create();
        setLayout();
    }

    protected void create() {
        infoLabel = new Label("Spiele Musik ab!", Chati.CHATI.getSkin());

        musicSelectLabel = new Label("Musik: ", Chati.CHATI.getSkin());
        musicSelectBox = new SelectBox<>(Chati.CHATI.getSkin());
        musicSelectBox.setItems(EnumSet.allOf(ContextMusic.class).toArray(new ContextMusic[0]));

       playButton = new TextButton("Abspielen", Chati.CHATI.getSkin());
       playButton.addListener(new ClickListener() {
           @Override
           public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               return true;
           }
           @Override
           public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               if (musicSelectBox.getSelected() == null) {
                   infoLabel.setText("Bitte wähle ein Lied aus!");
                   return;
               }
               Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                       new String[]{musicSelectBox.getSelected().getName()}, MENU_OPTION_PLAY);
           }
       });

       stopButton = new TextButton("Stoppen", Chati.CHATI.getSkin());
       stopButton.addListener(new ClickListener() {
           @Override
           public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               return true;
           }
           @Override
           public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                       new String[0], MENU_OPTION_STOP);
           }
       });

       randomButton = new TextButton("Zufall", Chati.CHATI.getSkin());
       randomButton.addListener(new ClickListener() {
           @Override
           public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               return true;
           }
           @Override
           public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               musicSelectBox.setSelected(musicSelectBox.getItems().random());
               Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                       new String[]{musicSelectBox.getSelected().getName()}, MENU_OPTION_PLAY);
           }
       });

       cancelButton = new TextButton("Abbrechen", Chati.CHATI.getSkin());
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

       creditsLabel = new Label("Music by https://www.bensound.com", Chati.CHATI.getSkin());

       closeButton = new TextButton("X", Chati.CHATI.getSkin());
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

    protected void setLayout() {
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();

        Table musicSelectContainer = new Table();
        musicSelectContainer.defaults().colspan(8).growX();
        musicSelectContainer.add(musicSelectLabel).colspan(2);
        musicSelectContainer.add(musicSelectBox).colspan(6);
        container.add(musicSelectContainer).row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(4).height(ROW_HEIGHT).growX();
        buttonContainer.add(playButton).padRight(SPACING / 2);
        buttonContainer.add(stopButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        buttonContainer.add(randomButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer).spaceBottom(0).row();

        creditsLabel.setFontScale(0.67f);
        creditsLabel.setAlignment(Align.center, Align.center);
        container.add(creditsLabel).spaceBottom(0).padBottom(-2 * SPACING);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
        if (!success) {
            infoLabel.setText(messageKey);
        }
    }
}