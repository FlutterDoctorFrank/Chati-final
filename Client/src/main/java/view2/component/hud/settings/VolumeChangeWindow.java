package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Assets;
import view2.Chati;
import view2.component.AbstractWindow;

public class VolumeChangeWindow extends AbstractWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_WIDTH = 650;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;

    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 1f;
    private static final float VOLUME_STEP_SIZE = 0.01f;
    private static final float SNAP_VALUE_STEP_SIZE = 0.25f;
    private static final float SNAP_VALUE_THRESHOLD = 0.05f;
    private static final float STANDARD_VOLUME = 0.5f;

    private static boolean CHANGED = false;
    private static float TOTAL_VOLUME = 0;
    private static float VOICE_VOLUME = 0;
    private static float MUSIC_VOLUME = 0;
    private static float SOUND_VOLUME = 0;

    private Label infoLabel;
    private Label totalVolumeLabel;
    private Label voiceVolumeLabel;
    private Label musicVolumeLabel;
    private Label soundVolumeLabel;
    private Slider totalVolumeSlider;
    private Slider voiceVolumeSlider;
    private Slider musicVolumeSlider;
    private Slider soundVolumeSlider;
    private TextButton confirmButton;
    private TextButton defaultButton;
    private TextButton cancelButton;
    private TextButton closeButton;

    public VolumeChangeWindow() {
        super("Lautstärke anpassen");
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel = new Label("Ändere die Lautstärke!", Assets.SKIN);

        totalVolumeLabel = new Label("Gesamt", Assets.SKIN);
        voiceVolumeLabel = new Label("Sprache", Assets.SKIN);
        musicVolumeLabel = new Label("Musik", Assets.SKIN);
        soundVolumeLabel = new Label("Hintergrundgeräusche", Assets.SKIN);

        totalVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Assets.SKIN);
        totalVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        totalVolumeSlider.setValue(TOTAL_VOLUME);

        voiceVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Assets.SKIN);
        voiceVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        voiceVolumeSlider.setValue(VOICE_VOLUME);

        musicVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Assets.SKIN);
        musicVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        musicVolumeSlider.setValue(MUSIC_VOLUME);

        soundVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Assets.SKIN);
        soundVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        soundVolumeSlider.setValue(SOUND_VOLUME);

        if (!CHANGED) {
            setDefaultVolume();
        }

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                CHANGED = true;
                TOTAL_VOLUME = totalVolumeSlider.getValue();
                VOICE_VOLUME = voiceVolumeSlider.getValue();
                MUSIC_VOLUME = musicVolumeSlider.getValue();
                SOUND_VOLUME = soundVolumeSlider.getValue();
                close();
            }
        });

        defaultButton = new TextButton("Standardeinstellung", Assets.SKIN);
        defaultButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setDefaultVolume();
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
        add(labelContainer).width(ROW_WIDTH).height(ROW_HEIGHT).spaceTop(VERTICAL_SPACING).spaceBottom(VERTICAL_SPACING).row();

        Table totalVolumeContainer = new Table();
        totalVolumeContainer.defaults().colspan(2).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).growX();
        totalVolumeContainer.add(totalVolumeLabel, totalVolumeSlider);
        add(totalVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table voiceVolumeContainer = new Table();
        voiceVolumeContainer.defaults().colspan(2).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).growX();
        voiceVolumeContainer.add(voiceVolumeLabel, voiceVolumeSlider);
        add(voiceVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table musicVolumeContainer = new Table();
        musicVolumeContainer.defaults().colspan(2).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).growX();
        musicVolumeContainer.add(musicVolumeLabel, musicVolumeSlider);
        add(musicVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table soundVolumeContainer = new Table();
        soundVolumeContainer.defaults().colspan(2).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).growX();
        soundVolumeContainer.add(soundVolumeLabel, soundVolumeSlider);
        add(soundVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(3).height(ROW_HEIGHT).space(HORIZONTAL_SPACING).growX();
        buttonContainer.add(confirmButton, defaultButton, cancelButton);
        add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    private void setDefaultVolume() {
        totalVolumeSlider.setValue(STANDARD_VOLUME);
        voiceVolumeSlider.setValue(STANDARD_VOLUME);
        musicVolumeSlider.setValue(STANDARD_VOLUME);
        soundVolumeSlider.setValue(STANDARD_VOLUME);
    }
}
