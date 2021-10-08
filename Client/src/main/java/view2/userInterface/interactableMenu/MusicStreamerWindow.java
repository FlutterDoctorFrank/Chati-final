package view2.userInterface.interactableMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTooltip;
import view2.userInterface.WeblinkClickListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;

/**
 * Eine Klasse, welche das Menü des MusicStreamer repräsentiert.
 */
public class MusicStreamerWindow extends InteractableWindow {

    private static final int MENU_OPTION_PLAY = 1;
    private static final int MENU_OPTION_PAUSE = 2;
    private static final int MENU_OPTION_STOP = 3;
    private static final int MENU_OPTION_PREVIOUS = 4;
    private static final int MENU_OPTION_NEXT = 5;
    private static final int MENU_OPTION_LOOPING = 6;
    private static final int MENU_OPTION_RANDOM = 7;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 675;
    private static final float BUTTON_SPACING = 5;
    private static final float BUTTON_SIZE = 100;
    private static final float BUTTON_SCALE_FACTOR = 0.05f;

    private final List<MusicListItem> musicList;
    private final ChatiImageButton playButton;
    private final ChatiImageButton stopButton;
    private final ChatiImageButton backButton;
    private final ChatiImageButton skipButton;
    private final ChatiImageButton loopingButton;
    private final ChatiImageButton randomButton;
    private final Label titleNameLabel;
    private final Label musicProgressLabel;
    private final ProgressBar musicProgressBar;

    /**
     * Erzeugt eine neue Instanz des MusicStreamerWindow.
     * @param musicPlayerId ID des zugehörigen MusicStreamer.
     */
    public MusicStreamerWindow(@NotNull final ContextID musicPlayerId) {
        super("window.title.music-player", musicPlayerId, ContextMenu.MUSIC_STREAMER_MENU, WINDOW_WIDTH, WINDOW_HEIGHT);

        infoLabel = new ChatiLabel("window.entry.music-player");

        musicList = new List<>(Chati.CHATI.getSkin());
        MusicListItem[] musicListItems = EnumSet.allOf(ContextMusic.class).stream().map(MusicListItem::new).toArray(MusicListItem[]::new);
        Arrays.sort(musicListItems);
        musicList.setItems(musicListItems);
        musicList.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                IInternUserView internUser = Chati.CHATI.getInternUser();
                if (musicList.getSelected() == null || internUser != null
                        && internUser.getMusic() == musicList.getSelected().getMusic()) {
                    return;
                }
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                        new String[] {musicList.getSelected().getMusic().getName()}, MENU_OPTION_PLAY);
            }
        });
        ScrollPane musicListScrollPane = new ScrollPane(musicList, Chati.CHATI.getSkin());
        musicListScrollPane.setFadeScrollBars(false);
        musicListScrollPane.setOverscroll(false, false);
        musicListScrollPane.setScrollingDisabled(true, false);

        playButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_play"), Chati.CHATI.getDrawable("music_play"),
                Chati.CHATI.getDrawable("music_play_disabled"), BUTTON_SCALE_FACTOR);
        playButton.addListener(new ChatiTooltip("hud.tooltip.play"));
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_PAUSE);
            }
        });

        stopButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_stop"), Chati.CHATI.getDrawable("music_stop"),
                Chati.CHATI.getDrawable("music_stop_disabled"), BUTTON_SCALE_FACTOR);
        stopButton.addListener(new ChatiTooltip("hud.tooltip.stop"));
        stopButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_STOP);
            }
        });

        backButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_back"), Chati.CHATI.getDrawable("music_back"),
                Chati.CHATI.getDrawable("music_back_disabled"), BUTTON_SCALE_FACTOR);
        backButton.addListener(new ChatiTooltip("hud.tooltip.back"));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_PREVIOUS);
            }
        });

        skipButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_skip"), Chati.CHATI.getDrawable("music_skip"),
                Chati.CHATI.getDrawable("music_skip_disabled"), BUTTON_SCALE_FACTOR);
        skipButton.addListener(new ChatiTooltip("hud.tooltip.skip"));
        skipButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_NEXT);
            }
        });

        loopingButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_looping_on"),
                Chati.CHATI.getDrawable("music_looping_on"), Chati.CHATI.getDrawable("music_looping_off"), BUTTON_SCALE_FACTOR);
        loopingButton.addListener(new ChatiTooltip("hud.tooltip.looping"));
        loopingButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_LOOPING);
            }
        });

        randomButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_random_on"),
                Chati.CHATI.getDrawable("music_random_on"), Chati.CHATI.getDrawable("music_random_off"), BUTTON_SCALE_FACTOR);
        randomButton.addListener(new ChatiTooltip("hud.tooltip.random"));
        randomButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId, new String[0], MENU_OPTION_RANDOM);
            }
        });

        ChatiImageButton soundButton = new ChatiImageButton(Chati.CHATI.getDrawable("music_sound_on"),
                Chati.CHATI.getDrawable("music_sound_off"), Chati.CHATI.getDrawable("music_sound_disabled"), BUTTON_SCALE_FACTOR);
        soundButton.addListener(new ChatiTooltip("hud.tooltip.sound"));
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMultimediaManager().toggleMusic(!soundButton.isChecked());
            }
        });

        ChatiLabel currentTitleLabel = new ChatiLabel("window.entry.current-title");
        titleNameLabel = new Label("", Chati.CHATI.getSkin());

        musicProgressLabel = new Label("00:00", Chati.CHATI.getSkin());
        musicProgressBar = new ProgressBar(0, 1, 0.00001f, false, Chati.CHATI.getSkin());

        Color weblinkColor = Color.ROYAL;
        Label creditsLabel = new Label("Music by [#" + weblinkColor + "]https://www.bensound.com", Chati.CHATI.getSkin());
        creditsLabel.addListener(new WeblinkClickListener(creditsLabel, weblinkColor, -SPACE / 0.67f));

        setButtonStates();
        setSelectedTitle();

        // Layout
        setModal(true);
        setMovable(false);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).padBottom(SPACE).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).padTop(SPACE).row();

        container.add(musicListScrollPane).height(0).grow().row();

        Table currentTitleTable = new Table();
        currentTitleTable.defaults().padRight(SPACE).left();
        currentTitleLabel.setAlignment(Align.left, Align.left);
        titleNameLabel.setAlignment(Align.left, Align.left);
        currentTitleTable.add(currentTitleLabel, titleNameLabel).left();
        container.add(currentTitleTable).height(ROW_HEIGHT / 2).left().growX().row();

        Table musicProgressContainer = new Table();
        musicProgressContainer.add(musicProgressLabel).width(WINDOW_WIDTH / 16).padRight(SPACE);
        musicProgressContainer.add(musicProgressBar).growX();
        container.add(musicProgressContainer).growX().row();

        Table buttonContainer = new Table();
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
        buttonContainer.add(soundButton).size(1 / 2f * BUTTON_SIZE).padLeft(BUTTON_SPACING);
        soundButton.getImage().setOrigin(1 / 4f * BUTTON_SIZE, 1 / 4f * BUTTON_SIZE);
        container.add(buttonContainer).row();

        creditsLabel.setFontScale(0.67f);
        creditsLabel.setAlignment(Align.center, Align.center);
        container.add(creditsLabel).padBottom(0);
        add(container).padLeft(SPACE).padRight(SPACE).grow();

        // Translatable Register
        translatables.add(currentTitleLabel);
        translatables.trimToSize();
    }

    @Override
    public void act(final float delta) {
        setButtonStates();
        if (Chati.CHATI.isMusicChanged()) {
            setSelectedTitle();
        }
        musicProgressBar.setValue(Chati.CHATI.getMultimediaManager().getCurrentPosition());
        Date currentSeconds = new Date(Chati.CHATI.getMultimediaManager().getCurrentSeconds() * 1000L);
        musicProgressLabel.setText(new SimpleDateFormat("mm:ss").format(currentSeconds));
        super.act(delta);
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (!success && messageBundle != null) {
            infoLabel.setText(Chati.CHATI.getLocalization().format(messageBundle.getMessageKey(), messageBundle.getArguments()));
        }
    }

    @Override
    public void focus() {
        if (getStage() != null) {
            getStage().setScrollFocus(musicList);
        }
    }

    /**
     * Setzt die sichtbaren Zustände der Buttons dieses Bildschirms.
     */
    private void setButtonStates() {
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null) {
            if (internUser.getMusic() != null) {
                playButton.setDisabled(false);
                playButton.setTouchable(Touchable.enabled);
                stopButton.setDisabled(false);
                stopButton.setTouchable(Touchable.enabled);
                backButton.setDisabled(false);
                backButton.setTouchable(Touchable.enabled);
                skipButton.setDisabled(false);
                skipButton.setTouchable(Touchable.enabled);
                if (Chati.CHATI.getMultimediaManager().isPlayingMusic()) {
                    playButton.getStyle().imageUp = Chati.CHATI.getDrawable("music_pause");
                    playButton.getStyle().imageDown = Chati.CHATI.getDrawable("music_pause");
                    playButton.getStyle().imageChecked = Chati.CHATI.getDrawable("music_pause");
                } else {
                    playButton.getStyle().imageUp = Chati.CHATI.getDrawable("music_play");
                    playButton.getStyle().imageDown = Chati.CHATI.getDrawable("music_play");
                    playButton.getStyle().imageChecked = Chati.CHATI.getDrawable("music_play");
                }
            } else {
                playButton.setDisabled(true);
                playButton.setTouchable(Touchable.disabled);
                stopButton.setDisabled(true);
                stopButton.setTouchable(Touchable.disabled);
                backButton.setDisabled(true);
                backButton.setTouchable(Touchable.disabled);
                skipButton.setDisabled(true);
                skipButton.setTouchable(Touchable.disabled);
            }

            if (loopingButton.isDisabled() && internUser.isLooping()) {
                loopingButton.setDisabled(false);
            } else if (!loopingButton.isDisabled() && !internUser.isLooping()) {
                loopingButton.setDisabled(true);
            }
            if (randomButton.isDisabled() && internUser.isRandom()) {
                randomButton.setDisabled(false);
            } else if (!randomButton.isDisabled() && !internUser.isRandom()) {
                randomButton.setDisabled(true);
            }
        }
    }

    /**
     * Setzt den aktuellen Titel in der Anzeige.
     */
    private void setSelectedTitle() {
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null) {
            ContextMusic currentMusic = internUser.getMusic();
            if (currentMusic != null) {
                musicList.getItems().forEach(item -> {
                    if (item.getMusic().equals(currentMusic)) {
                        musicList.setSelected(item);
                    }
                });
                titleNameLabel.setText(currentMusic.getName());
            } else {
                musicList.setSelected(null);
                titleNameLabel.setText("");
            }
        }
    }

    /**
     * Eine Klasse, welche die Elemente repräsentiert, die in der Liste aller Musikstücke angezeigt werden.
     */
    private static class MusicListItem implements Comparable<MusicListItem> {

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

        @Override
        public int compareTo(@NotNull final MusicListItem other) {
            return this.music.getName().compareTo(other.music.getName());
        }
    }
}
