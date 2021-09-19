package view2.userInterface.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.context.spatial.ContextMusic;
import model.user.IInternUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.ChatiLocalization;
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiLabel;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.NoSuchElementException;

/**
 * Eine Klasse, welche das Menü des MusicStreamer repräsentiert.
 */
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
    private static final float BUTTON_SPACING = 5;
    private static final float BUTTON_SIZE = 100;
    private static final float BUTTON_SCALE_FACTOR = 0.05f;

    private final MusicListItem[] musicListItems;
    private final List<MusicListItem> musicList;
    private final ChatiImageButton playButton;
    private final ChatiImageButton stopButton;
    private final ChatiImageButton backButton;
    private final ChatiImageButton skipButton;

    /**
     * Erzeugt eine neue Instanz des MusicStreamerWindow.
     * @param musicPlayerId ID des zugehörigen MusicStreamer.
     */
    public MusicStreamerWindow(@NotNull final ContextID musicPlayerId) {
        super("window.title.music-player", musicPlayerId, ContextMenu.MUSIC_STREAMER_MENU);

        infoLabel = new ChatiLabel("window.entry.music-player");
        musicList = new List<>(Chati.CHATI.getSkin(), "dimmed");
        musicListItems = EnumSet.allOf(ContextMusic.class).stream().map(MusicListItem::new).toArray(MusicListItem[]::new);
        musicList.setItems(musicListItems);
        setSelectedMusic();
        musicList.getStyle().over = Chati.CHATI.getSkin().getDrawable("list-selection");
        musicList.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (musicList.getSelected() == null) {
                    return;
                }
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[] {musicList.getSelected().getMusic().getName()}, MENU_OPTION_PLAY);
            }
        });
        ScrollPane musicListScrollPane = new ScrollPane(musicList);
        musicListScrollPane.setFadeScrollBars(false);
        musicListScrollPane.setOverscroll(false, false);
        musicListScrollPane.setScrollingDisabled(true, false);

        playButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_play"), Chati.CHATI.getDrawable("music_pause"),
                Chati.CHATI.getDrawable("music_play_disabled"), BUTTON_SCALE_FACTOR);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_PAUSE);
            }
        });

        stopButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_stop"), Chati.CHATI.getDrawable("music_stop"),
                Chati.CHATI.getDrawable("music_stop_disabled"), BUTTON_SCALE_FACTOR);
        stopButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_STOP);
            }
        });

        backButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_back"), Chati.CHATI.getDrawable("music_back"),
                Chati.CHATI.getDrawable("music_back_disabled"), BUTTON_SCALE_FACTOR);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_PREVIOUS);
            }
        });

        skipButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_skip"), Chati.CHATI.getDrawable("music_skip"),
                Chati.CHATI.getDrawable("music_skip_disabled"), BUTTON_SCALE_FACTOR);
        skipButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_NEXT);
            }
        });

        ChatiImageButton loopingButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_loop"),
                Chati.CHATI.getDrawable("music_loop"), Chati.CHATI.getDrawable("music_loop_disabled"), BUTTON_SCALE_FACTOR);
        loopingButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_LOOPING);
            }
        });

        ChatiImageButton randomButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_random"),
                Chati.CHATI.getDrawable("music_random"), Chati.CHATI.getDrawable("music_random_disabled"), BUTTON_SCALE_FACTOR);
        randomButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_RANDOM);
            }
        });

        ChatiImageButton volumeButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_volume"),
                Chati.CHATI.getDrawable("music_volume"), Chati.CHATI.getDrawable("music_volume_disabled"), BUTTON_SCALE_FACTOR);
        volumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                // TODO
            }
        });

        Label musicProgressLabel = new Label("0:00", Chati.CHATI.getSkin()); // TODO
        ProgressBar musicProgressBar = new ProgressBar(0, 1, 0.01f, false, Chati.CHATI.getSkin());

        Label creditsLabel = new Label("Music by https://www.bensound.com", Chati.CHATI.getSkin());

        setSelectedMusic();
        setButtonImages();

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
    }

    @Override
    public void act(final float delta) {
        setSelectedMusic();
        setButtonImages();
        super.act(delta);
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (!success && messageBundle != null) {
            infoLabel.setText(Chati.CHATI.getLocalization().format(messageBundle.getMessageKey(), messageBundle.getArguments()));
        }
    }

    /**
     * Setzt das aktuell abgespielte Musikstück in der Liste der angezeigten Musikstücke.
     */
    private void setSelectedMusic() {
        IInternUserView internUser = Chati.CHATI.getInternUser();

        // TODO Das funktioniert noch nicht richtig.
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

    /**
     * Setzt die korrekte Anzeige der ImageButtons.
     */
    private void setButtonImages() {
        IInternUserView internUser = Chati.CHATI.getInternUser();

        if (internUser != null && internUser.getMusic() != null) {
            enableButton(playButton);
            enableButton(stopButton);
            enableButton(backButton);
            enableButton(skipButton);
            playButton.setChecked(Chati.CHATI.getAudioManager().isPlayingMusic());
        } else {
            disableButton(playButton);
            disableButton(stopButton);
            disableButton(backButton);
            disableButton(skipButton);
        }
    }

    /**
     * Aktiviert einen ImageButtons.
     * @param button Zu aktivierender ImageButton.
     */
    private void enableButton(@NotNull final Button button) {
        button.setDisabled(false);
        button.setTouchable(Touchable.enabled);
    }

    /**
     * Deaktiviert einen ImageButton.
     * @param button Zu deaktivierender ImageButton.
     */
    private void disableButton(@NotNull final Button button) {
        button.setDisabled(true);
        button.setTouchable(Touchable.disabled);
    }

    /**
     * Eine Klasse, welche die Elemente repräsentiert, die in der Liste aller Musikstücke angezeigt werden.
     */
    private static class MusicListItem {

        private final ContextMusic music;

        /**
         * Erzeugt eine neue Instanz des MusicListItem.
         * @param music Zugehörige Musik.
         */
        public MusicListItem(@NotNull final ContextMusic music) {
            this.music = music;
        }

        /**
         * Gibt die Musik dieses Elements zurück.
         * @return Musik des Elements.
         */
        public @NotNull ContextMusic getMusic() {
            return music;
        }

        @Override
        public @NotNull String toString() {
            return music.getName();
        }
    }
}
