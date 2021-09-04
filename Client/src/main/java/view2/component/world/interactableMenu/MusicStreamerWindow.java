package view2.component.world.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.context.spatial.ContextMusic;
import org.lwjgl.system.CallbackI;
import view2.Assets;
import view2.Chati;

import java.util.EnumSet;

public class MusicStreamerWindow extends InteractableWindow {

    private static final int MENU_OPTION_PLAY = 1;
    private static final int MENU_OPTION_STOP = 2;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 600;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;
    private static final float BUTTON_SPACING = 5;
    private static final float BUTTON_SIZE = 100;

    private Label infoLabel;
    private Label musicSelectLabel;
    private List<MusicListItems> musicList;
    private ScrollPane musicListScrollPane;
    private ImageButton playButton2;
    private ImageButton stopButton2;
    private ImageButton backButton2;
    private ImageButton skipButton2;
    private ImageButton loopingButton2;
    private ImageButton randomButton2;
    private ImageButton volumeButton2;
    private Label creditsLabel;
    private TextButton closeButton;

    private SelectBox<ContextMusic> musicSelectBox;
    private TextButton playButton;
    private TextButton stopButton;
    private TextButton randomButton;
    private TextButton cancelButton;


    public MusicStreamerWindow(ContextID musicPlayerId) {
        super("Jukebox", musicPlayerId, ContextMenu.MUSIC_STREAMER_MENU);
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel = new Label("Spiele Musik ab!", Assets.SKIN);

        musicSelectLabel = new Label("Musik: ", Assets.SKIN);

        musicList = new List<>(Assets.SKIN, "dimmed");
        MusicListItems[] musicListItems = EnumSet.allOf(ContextMusic.class).stream()
                .map(MusicListItems::new).toArray(MusicListItems[]::new);
        musicList.setItems(musicListItems);
        musicListScrollPane = new ScrollPane(musicList);

        ImageButton.ImageButtonStyle playButtonStyle = new ImageButton.ImageButtonStyle();
        playButtonStyle.imageUp = Assets.PLAY_BUTTON_IMAGE;
        playButtonStyle.imageChecked = Assets.PAUSE_BUTTON_IMAGE;
        playButtonStyle.imageDisabled = Assets.DISABLED_PLAY_BUTTON_IMAGE;
        playButton2 = new ImageButton(playButtonStyle);

        ImageButton.ImageButtonStyle stopButtonStyle = new ImageButton.ImageButtonStyle();
        stopButtonStyle.imageUp = Assets.STOP_BUTTON_IMAGE;
        stopButtonStyle.imageDisabled = Assets.DISABLED_STOP_BUTTON_IMAGE;
        stopButton2 = new ImageButton(stopButtonStyle);

        ImageButton.ImageButtonStyle backButtonStyle = new ImageButton.ImageButtonStyle();
        backButtonStyle.imageUp = Assets.PREVIOUS_BUTTON_IMAGE;
        backButtonStyle.imageDisabled = Assets.DISABLED_PREVIOUS_BUTTON_IMAGE;
        backButton2 = new ImageButton(backButtonStyle);

        ImageButton.ImageButtonStyle skipButtonStyle = new ImageButton.ImageButtonStyle();
        skipButtonStyle.imageUp = Assets.NEXT_BUTTON_IMAGE;
        skipButtonStyle.imageDisabled = Assets.DISABLED_NEXT_BUTTON_IMAGE;
        skipButton2 = new ImageButton(skipButtonStyle);

        ImageButton.ImageButtonStyle loopingButtonStyle = new ImageButton.ImageButtonStyle();
        loopingButtonStyle.imageUp = Assets.LOOPING_BUTTON_IMAGE;
        loopingButtonStyle.imageDisabled = Assets.DISABLED_LOOPING_BUTTON_IMAGE;
        loopingButton2 = new ImageButton(loopingButtonStyle);

        ImageButton.ImageButtonStyle randomButtonStyle = new ImageButton.ImageButtonStyle();
        randomButtonStyle.imageUp = Assets.RANDOM_BUTTON_IMAGE;
        randomButtonStyle.imageDisabled = Assets.DISABLED_RANDOM_BUTTON_IMAGE;
        randomButton2 = new ImageButton(randomButtonStyle);

        ImageButton.ImageButtonStyle volumeButtonStyle = new ImageButton.ImageButtonStyle();
        volumeButtonStyle.imageUp = Assets.VOLUME_BUTTON_IMAGE;
        volumeButtonStyle.imageDisabled = Assets.DISABLED_VOLUME_BUTTON_IMAGE;
        volumeButton2 = new ImageButton(volumeButtonStyle);



        musicSelectBox = new SelectBox<>(Assets.SKIN);
        musicSelectBox.setItems(EnumSet.allOf(ContextMusic.class).toArray(new ContextMusic[0]));

        playButton = new TextButton("Abspielen", Assets.SKIN);
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
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[] {musicSelectBox.getSelected().getName()}, MENU_OPTION_PLAY);
            }
        });

        stopButton = new TextButton("Stoppen", Assets.SKIN);
        stopButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[0], MENU_OPTION_STOP);
            }
        });

        randomButton = new TextButton("Zufall", Assets.SKIN);
        randomButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                musicSelectBox.setSelected(musicSelectBox.getItems().random());
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[]{musicSelectBox.getSelected().getName()}, MENU_OPTION_PLAY);
            }
        });

        cancelButton = new TextButton("Abbrechen", Assets.SKIN);
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

        creditsLabel = new Label("Music by https://www.bensound.com", Assets.SKIN);

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

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();

        container.add(musicListScrollPane).top().growY().row();

        Table buttonContainer = new Table();
        buttonContainer.defaults();
        buttonContainer.add(loopingButton2).size(1 / 2f * BUTTON_SIZE).padRight(BUTTON_SPACING);
        buttonContainer.add(stopButton2).size(1 / 2f * BUTTON_SIZE);
        buttonContainer.add(backButton2).width(BUTTON_SIZE).height(1 / 2f * BUTTON_SIZE);
        buttonContainer.add(playButton2).size(BUTTON_SIZE).padLeft(-BUTTON_SPACING).padRight(-BUTTON_SPACING);
        buttonContainer.add(skipButton2).width(BUTTON_SIZE).height(1 / 2f * BUTTON_SIZE);
        buttonContainer.add(randomButton2).size(1 / 2f * BUTTON_SIZE);
        buttonContainer.add(volumeButton2).size(1 / 2f * BUTTON_SIZE).padLeft(BUTTON_SPACING);
        container.add(buttonContainer).row();

        creditsLabel.setFontScale(0.67f);
        creditsLabel.setAlignment(Align.center, Align.center);
        container.add(creditsLabel);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void showMessage(String messageKey) {
        infoLabel.setText(messageKey);
    }

    private static class MusicListItems {

        private final ContextMusic music;

        public MusicListItems(ContextMusic music) {
            this.music = music;
        }

        public ContextMusic getMusic() {
            return music;
        }

        @Override
        public String toString() {
            return music.getName();
        }
    }
}
