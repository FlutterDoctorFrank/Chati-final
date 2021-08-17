package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Chati;
import view2.component.ChatiWindow;

public class VolumeChangeWindow extends ChatiWindow {

    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 1f;
    private static final float VOLUME_STEP_SIZE = 0.01f;
    private static final float SNAP_VALUE_STEP_SIZE = 0.25f;
    private static final float SNAP_VALUE_THRESHOLD = 0.05f;
    private static final float STANDARD_VOLUME = 0.5f;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_WIDTH = 650;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;

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
    private TextButton cancelButton;
    private TextButton defaultButton;

    public VolumeChangeWindow() {
        super("Lautstärke anpassen");
        create();
        setLayout();
    }

    public void setDefaultVolume() {
        totalVolumeSlider.setValue(STANDARD_VOLUME);
        voiceVolumeSlider.setValue(STANDARD_VOLUME);
        musicVolumeSlider.setValue(STANDARD_VOLUME);
        soundVolumeSlider.setValue(STANDARD_VOLUME);
    }

    @Override
    protected void create() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        infoLabel = new Label("Ändere die Lautstärke!", style);

        totalVolumeLabel = new Label("Gesamt", style);
        voiceVolumeLabel = new Label("Sprache", style);
        musicVolumeLabel = new Label("Musik", style);
        soundVolumeLabel = new Label("Hintergrundgeräusche", style);

        totalVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.SKIN);
        totalVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        totalVolumeSlider.setValue(TOTAL_VOLUME);

        voiceVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.SKIN);
        voiceVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        voiceVolumeSlider.setValue(VOICE_VOLUME);

        musicVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.SKIN);
        musicVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        musicVolumeSlider.setValue(MUSIC_VOLUME);

        soundVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.SKIN);
        soundVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE, 3 * SNAP_VALUE_STEP_SIZE}
                , SNAP_VALUE_THRESHOLD);
        soundVolumeSlider.setValue(SOUND_VOLUME);

        if (!CHANGED) {
            setDefaultVolume();
        }

        confirmButton = new TextButton("Bestätigen", Chati.SKIN);
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
                remove();
            }
        });

        defaultButton = new TextButton("Standardeinstellung", Chati.SKIN);
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

        cancelButton = new TextButton("Abbrechen", Chati.SKIN);
        cancelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                remove();
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
        totalVolumeContainer.add(totalVolumeLabel).width(ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        totalVolumeContainer.add(totalVolumeSlider).width(2 * ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        add(totalVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table voiceVolumeContainer = new Table();
        voiceVolumeContainer.add(voiceVolumeLabel).width(ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        voiceVolumeContainer.add(voiceVolumeSlider).width(2 * ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        add(voiceVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table musicVolumeContainer = new Table();
        musicVolumeContainer.add(musicVolumeLabel).width(ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        musicVolumeContainer.add(musicVolumeSlider).width(2 * ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        add(musicVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table soundVolumeContainer = new Table();
        soundVolumeContainer.add(soundVolumeLabel).width(ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        soundVolumeContainer.add(soundVolumeSlider).width(2 * ROW_WIDTH / 3).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING);
        add(soundVolumeContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();


        Table buttonContainer = new Table(Chati.SKIN);
        buttonContainer.add(confirmButton).width((ROW_WIDTH - VERTICAL_SPACING) / 3).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
        buttonContainer.add(defaultButton).width((ROW_WIDTH - VERTICAL_SPACING) / 3).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
        buttonContainer.add(cancelButton).width((ROW_WIDTH - VERTICAL_SPACING) / 3).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
        add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);
    }
}
