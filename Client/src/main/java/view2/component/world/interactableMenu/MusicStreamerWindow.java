package view2.component.world.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.context.spatial.ContextMusic;
import model.user.IInternUserView;
import view2.Assets;
import view2.Chati;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.NoSuchElementException;

public class MusicStreamerWindow extends InteractableWindow {

    /** Menü-Option zum Auswählen eines abzuspielenden Musikstücks. */
    private static final int MENU_OPTION_PLAY = 1;

    /** Menü-Option zum Pausieren oder Fortsetzen des gerade ausgewählten Musikstücks. */
    private static final int MENU_OPTION_PAUSE = 2;

    /** Menü-Option zum Stoppen des gerade ausgewählten Musikstücks. */
    private static final int MENU_OPTION_STOP = 3;

    /** Menü-Option zur Wiedergabe des nächsten Musikstücks in der Liste. */
    private static final int MENU_OPTION_PREVIOUS = 4;

    /** Menü-Option zur Wiedergabe des vorherigen Musikstücks in der Liste. */
    private static final int MENU_OPTION_NEXT = 5;

    /** Menü-Option zum Ein- und Ausschalten der wiederholten Wiedergabe eines ausgewählten Musikstücks. */
    private static final int MENU_OPTION_LOOPING = 6;

    /** Menü-Option zum Ein- und Ausschalten von zufällig abzuspielenden Musikstücken. */
    private static final int MENU_OPTION_RANDOM = 7;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 600;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;
    private static final float BUTTON_SPACING = 5;
    private static final float BUTTON_SIZE = 100;
    private static final float BUTTON_SCALE_FACTOR = 0.05f;

    private Label infoLabel;
    private MusicListItem[] musicListItems;
    private List<MusicListItem> musicList;
    private ScrollPane musicListScrollPane;
    private ImageButton playButton;
    private ImageButton stopButton;
    private ImageButton backButton;
    private ImageButton skipButton;
    private ImageButton loopingButton;
    private ImageButton randomButton;
    private ImageButton volumeButton;
    private ProgressBar musicProgressBar;
    private Label musicProgressLabel;
    private Label creditsLabel;
    private TextButton closeButton;

    public MusicStreamerWindow(ContextID musicPlayerId) {
        super("Jukebox", musicPlayerId, ContextMenu.MUSIC_STREAMER_MENU);
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        setSelectedMusic();
        setButtonImages();
        super.act(delta);
    }

    @Override
    protected void create() {
        infoLabel = new Label("Wähle einen Titel.", Assets.SKIN);

        musicList = new List<>(Assets.SKIN, "dimmed");
        musicListItems = EnumSet.allOf(ContextMusic.class).stream().map(MusicListItem::new).toArray(MusicListItem[]::new);
        musicList.setItems(musicListItems);
        setSelectedMusic();
        musicList.getStyle().over = Assets.SKIN.getDrawable("list-selection");
        musicList.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (musicList.getSelected() == null) {
                    return;
                }
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[] {musicList.getSelected().getMusic().getName()}, MENU_OPTION_PLAY);
            }
        });
        musicListScrollPane = new ScrollPane(musicList);

        ImageButton.ImageButtonStyle playButtonStyle = new ImageButton.ImageButtonStyle();
        playButtonStyle.imageUp = Assets.PLAY_BUTTON_IMAGE;
        playButtonStyle.imageDisabled = Assets.DISABLED_PLAY_BUTTON_IMAGE;
        playButton = new ImageButton(playButtonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[0], MENU_OPTION_PAUSE);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    playButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    playButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ImageButton.ImageButtonStyle stopButtonStyle = new ImageButton.ImageButtonStyle();
        stopButtonStyle.imageUp = Assets.STOP_BUTTON_IMAGE;
        stopButtonStyle.imageDisabled = Assets.DISABLED_STOP_BUTTON_IMAGE;
        stopButton = new ImageButton(stopButtonStyle);
        stopButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stopButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                stopButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[0], MENU_OPTION_STOP);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    stopButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    stopButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ImageButton.ImageButtonStyle backButtonStyle = new ImageButton.ImageButtonStyle();
        backButtonStyle.imageUp = Assets.PREVIOUS_BUTTON_IMAGE;
        backButtonStyle.imageDisabled = Assets.DISABLED_PREVIOUS_BUTTON_IMAGE;
        backButton = new ImageButton(backButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                backButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                backButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[0], MENU_OPTION_PREVIOUS);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    backButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    backButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ImageButton.ImageButtonStyle skipButtonStyle = new ImageButton.ImageButtonStyle();
        skipButtonStyle.imageUp = Assets.NEXT_BUTTON_IMAGE;
        skipButtonStyle.imageDisabled = Assets.DISABLED_NEXT_BUTTON_IMAGE;
        skipButton = new ImageButton(skipButtonStyle);
        skipButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                skipButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                skipButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[0], MENU_OPTION_NEXT);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    skipButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    skipButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ImageButton.ImageButtonStyle loopingButtonStyle = new ImageButton.ImageButtonStyle();
        loopingButtonStyle.imageUp = Assets.LOOPING_BUTTON_IMAGE;
        loopingButtonStyle.imageDisabled = Assets.DISABLED_LOOPING_BUTTON_IMAGE;
        loopingButton = new ImageButton(loopingButtonStyle);
        loopingButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                loopingButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                loopingButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[0], MENU_OPTION_LOOPING);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    loopingButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    loopingButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ImageButton.ImageButtonStyle randomButtonStyle = new ImageButton.ImageButtonStyle();
        randomButtonStyle.imageUp = Assets.RANDOM_BUTTON_IMAGE;
        randomButtonStyle.imageDisabled = Assets.DISABLED_RANDOM_BUTTON_IMAGE;
        randomButton = new ImageButton(randomButtonStyle);
        randomButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                randomButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                randomButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[0], MENU_OPTION_RANDOM);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    randomButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    randomButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ImageButton.ImageButtonStyle volumeButtonStyle = new ImageButton.ImageButtonStyle();
        volumeButtonStyle.imageUp = Assets.VOLUME_BUTTON_IMAGE;
        volumeButtonStyle.imageDisabled = Assets.DISABLED_VOLUME_BUTTON_IMAGE;
        volumeButton = new ImageButton(volumeButtonStyle);
        volumeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                volumeButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                volumeButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);

            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    volumeButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    volumeButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        musicProgressLabel = new Label("0:00", Assets.SKIN); // TODO
        musicProgressBar = new ProgressBar(0, 1, 0.01f, false, Assets.SKIN);

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

        setSelectedMusic();
        setButtonImages();
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

        container.add(musicListScrollPane).top().height(2 / 5f * WINDOW_HEIGHT).growY().row();

        Table musicProgressContainer = new Table();
        musicProgressContainer.add(musicProgressLabel).width(WINDOW_WIDTH / 16).padRight(SPACING);
        musicProgressContainer.add(musicProgressBar).growX();
        container.add(musicProgressContainer).growX().row();

        Table buttonContainer = new Table();
        buttonContainer.defaults();
        buttonContainer.add(loopingButton).size(1 / 2f * BUTTON_SIZE).padRight(BUTTON_SPACING);
        loopingButton.getImage().setOrigin(1 / 4f * BUTTON_SIZE, 1 / 4f * BUTTON_SIZE);
        buttonContainer.add(stopButton).size(1 / 2f * BUTTON_SIZE);
        stopButton.getImage().setOrigin(1 / 4f * BUTTON_SIZE, 1 / 4f * BUTTON_SIZE);
        buttonContainer.add(backButton).width(BUTTON_SIZE).height(1 / 2f * BUTTON_SIZE);
        backButton.getImage().setOrigin(1 / 2f * BUTTON_SIZE, 1 / 4f * BUTTON_SIZE);
        buttonContainer.add(playButton).size(BUTTON_SIZE).padLeft(-BUTTON_SPACING).padRight(-BUTTON_SPACING);
        playButton.getImage().setOrigin(1 / 2f * BUTTON_SIZE, 1 / 2f * BUTTON_SIZE);
        buttonContainer.add(skipButton).width(BUTTON_SIZE).height(1 / 2f * BUTTON_SIZE);
        skipButton.getImage().setOrigin(1 / 2f * BUTTON_SIZE, 1 / 4f * BUTTON_SIZE);
        buttonContainer.add(randomButton).size(1 / 2f * BUTTON_SIZE);
        randomButton.getImage().setOrigin(1 / 4f * BUTTON_SIZE, 1 / 4f * BUTTON_SIZE);
        buttonContainer.add(volumeButton).size(1 / 2f * BUTTON_SIZE).padLeft(BUTTON_SPACING);
        volumeButton.getImage().setOrigin(1 / 4f * BUTTON_SIZE, 1 / 4f * BUTTON_SIZE);
        container.add(buttonContainer).row();

        creditsLabel.setFontScale(0.67f);
        creditsLabel.setAlignment(Align.center, Align.center);
        container.add(creditsLabel);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
        if (!success) {
            infoLabel.setText(messageKey);
        }
    }

    private void setSelectedMusic() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();

        if (Chati.CHATI.isMusicChanged() && internUser != null) {
            MusicListItem item;
            try {
               item = Arrays.stream(musicListItems)
                       .filter(selectItem -> selectItem.getMusic() == internUser.getMusic()).findFirst().orElseThrow();
            } catch (NoSuchElementException e) {
                item = null;
            }
            musicList.setSelected(item);
        }
    }

    private void setButtonImages() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();

        if (internUser != null && internUser.getMusic() != null) {
            enableButton(playButton);
            enableButton(stopButton);
            enableButton(backButton);
            enableButton(skipButton);
            if (Chati.CHATI.getAudioManager().isPlayingMusic()) {
                playButton.getStyle().imageUp = Assets.PAUSE_BUTTON_IMAGE;
            } else {
                playButton.getStyle().imageUp = Assets.PLAY_BUTTON_IMAGE;
            }
        } else {
            disableButton(playButton);
            disableButton(stopButton);
            disableButton(backButton);
            disableButton(skipButton);
        }
    }

    private void enableButton(Button button) {
        button.setDisabled(false);
        button.setTouchable(Touchable.enabled);
    }

    private void disableButton(Button button) {
        button.setDisabled(true);
        button.setTouchable(Touchable.disabled);
    }

    private static class MusicListItem {

        private final ContextMusic music;

        public MusicListItem(ContextMusic music) {
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
