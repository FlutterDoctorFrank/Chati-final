package view2.userInterface.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.ChatiPreferences;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiWindow;

/**
 * Eine Klasse, welche das Menü zum Vornehmen von Soundeinstellungen repräsentiert.
 */
public class SoundConfigurationWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 650;

    private static final float MIN_VALUE = 0;
    private static final float MAX_VALUE = 1;
    private static final float STEP_SIZE = MAX_VALUE / 100;
    private static final float SNAP_VALUE_STEP_SIZE = MAX_VALUE / 4;
    private static final float SNAP_VALUE_THRESHOLD = SNAP_VALUE_STEP_SIZE / 10;

    private final ChatiTextButton confirmButton;

    /**
     * Erzeugt eine neue Instanz des SoundConfigurationWindow.
     */
    public SoundConfigurationWindow() {
        super("window.title.sound-settings");

        infoLabel = new ChatiLabel("window.entry.sound-settings");

        ChatiLabel totalVolumeLabel = new ChatiLabel("menu.label.total");
        Slider totalVolumeSlider = new Slider(MIN_VALUE, MAX_VALUE, STEP_SIZE, false, Chati.CHATI.getSkin());
        totalVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE, MAX_VALUE}, SNAP_VALUE_THRESHOLD);
        totalVolumeSlider.setValue(Chati.CHATI.getPreferences().getTotalVolume());
        totalVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        ChatiLabel voiceVolumeLabel = new ChatiLabel("menu.label.voice");
        Slider voiceVolumeSlider = new Slider(MIN_VALUE, MAX_VALUE, STEP_SIZE, false, Chati.CHATI.getSkin());
        voiceVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE, MAX_VALUE}, SNAP_VALUE_THRESHOLD);
        voiceVolumeSlider.setValue(Chati.CHATI.getPreferences().getVoiceVolume());
        voiceVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        ChatiLabel musicVolumeLabel = new ChatiLabel("menu.label.music");
        Slider musicVolumeSlider = new Slider(MIN_VALUE, MAX_VALUE, STEP_SIZE, false, Chati.CHATI.getSkin());
        musicVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE, MAX_VALUE}, SNAP_VALUE_THRESHOLD);
        musicVolumeSlider.setValue(Chati.CHATI.getPreferences().getMusicVolume());
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        ChatiLabel soundVolumeLabel = new ChatiLabel("menu.label.sound");
        Slider soundVolumeSlider = new Slider(MIN_VALUE, MAX_VALUE, STEP_SIZE, false, Chati.CHATI.getSkin());
        soundVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE, MAX_VALUE}, SNAP_VALUE_THRESHOLD);
        soundVolumeSlider.setValue(Chati.CHATI.getPreferences().getSoundVolume());
        soundVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        ChatiLabel microphoneSensitivityLabel = new ChatiLabel("menu.label.sensitivity");
        Slider microphoneSensitivitySlider = new Slider(MIN_VALUE, MAX_VALUE, STEP_SIZE, false, Chati.CHATI.getSkin());
        microphoneSensitivitySlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE, MAX_VALUE}, SNAP_VALUE_THRESHOLD);
        microphoneSensitivitySlider.setValue(Chati.CHATI.getPreferences().getMicrophoneSensitivity());
        microphoneSensitivitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        ChatiLabel pushToTalkLabel = new ChatiLabel("menu.label.push-to-talk");
        CheckBox pushToTalkCheckBox = new CheckBox("", Chati.CHATI.getSkin());
        pushToTalkCheckBox.setChecked(Chati.CHATI.getPreferences().getPushToTalk());
        pushToTalkCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        confirmButton = new ChatiTextButton("menu.button.apply", true);
        disableButton();
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                disableButton();
                Chati.CHATI.getPreferences().setTotalVolume(totalVolumeSlider.getValue());
                Chati.CHATI.getPreferences().setVoiceVolume(voiceVolumeSlider.getValue());
                Chati.CHATI.getPreferences().setMusicVolume(musicVolumeSlider.getValue());
                Chati.CHATI.getPreferences().setSoundVolume(soundVolumeSlider.getValue());
                Chati.CHATI.getPreferences().setMicrophoneSensitivity(microphoneSensitivitySlider.getValue());
                Chati.CHATI.getPreferences().setPushToTalk(pushToTalkCheckBox.isChecked());
                Chati.CHATI.getAudioManager().setMicrophoneSensitivity();
                Chati.CHATI.getAudioManager().setVolume();
                showMessage("window.settings.saved");
            }
        });

        ChatiTextButton defaultButton = new ChatiTextButton("menu.button.default-settings", true);
        defaultButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                totalVolumeSlider.setValue(ChatiPreferences.DEFAULT_TOTAL_VOLUME);
                voiceVolumeSlider.setValue(ChatiPreferences.DEFAULT_VOICE_VOLUME);
                musicVolumeSlider.setValue(ChatiPreferences.DEFAULT_MUSIC_VOLUME);
                soundVolumeSlider.setValue(ChatiPreferences.DEFAULT_SOUND_VOLUME);
                microphoneSensitivitySlider.setValue(ChatiPreferences.DEFAULT_MICROPHONE_SENSITIVITY);
                pushToTalkCheckBox.setChecked(ChatiPreferences.DEFAULT_PUSH_TO_TALK);
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
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING / 2).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        Table totalVolumeContainer = new Table();
        totalVolumeContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        totalVolumeContainer.add(totalVolumeLabel).padRight(SPACING / 2);
        totalVolumeContainer.add(totalVolumeSlider).padLeft(SPACING / 2);
        container.add(totalVolumeContainer).row();
        Table voiceVolumeContainer = new Table();
        voiceVolumeContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        voiceVolumeContainer.add(voiceVolumeLabel).padRight(SPACING / 2);
        voiceVolumeContainer.add(voiceVolumeSlider).padLeft(SPACING / 2);
        container.add(voiceVolumeContainer).row();
        Table musicVolumeContainer = new Table();
        musicVolumeContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        musicVolumeContainer.add(musicVolumeLabel).padRight(SPACING / 2);
        musicVolumeContainer.add(musicVolumeSlider).padLeft(SPACING / 2);
        container.add(musicVolumeContainer).row();
        Table soundVolumeContainer = new Table();
        soundVolumeContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        soundVolumeContainer.add(soundVolumeLabel).padRight(SPACING / 2);
        soundVolumeContainer.add(soundVolumeSlider).padLeft(SPACING / 2);
        container.add(soundVolumeContainer).row();
        Table microphoneSensitivityContainer = new Table();
        microphoneSensitivityContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        microphoneSensitivityContainer.add(microphoneSensitivityLabel).padRight(SPACING / 2);
        microphoneSensitivityContainer.add(microphoneSensitivitySlider).padLeft(SPACING / 2);
        container.add(microphoneSensitivityContainer).row();
        Table pushToTalkContainer = new Table();
        pushToTalkContainer.defaults().colspan(16).height(ROW_HEIGHT).space(SPACING).growX();
        pushToTalkContainer.add(pushToTalkLabel).colspan(15);
        pushToTalkContainer.add(pushToTalkCheckBox).colspan(1);
        pushToTalkCheckBox.getImage().scaleBy(0.25f);
        container.add(pushToTalkContainer).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(3).bottom().height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(defaultButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer).padTop(SPACING);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        // Translatable register
        translates.add(infoLabel);
        translates.add(totalVolumeLabel);
        translates.add(voiceVolumeLabel);
        translates.add(musicVolumeLabel);
        translates.add(soundVolumeLabel);
        translates.add(microphoneSensitivityLabel);
        translates.add(pushToTalkLabel);
        translates.add(confirmButton);
        translates.add(defaultButton);
        translates.add(cancelButton);
        translates.trimToSize();
    }

    /**
     * Aktiviert den Bestätigungsbutton.
     */
    private void enableButton() {
        confirmButton.setTouchable(Touchable.enabled);
        confirmButton.getLabel().setColor(Color.WHITE);
    }

    /**
     * Deaktiviert den Bestätigungsbutton.
     */
    private void disableButton() {
        confirmButton.setTouchable(Touchable.disabled);
        confirmButton.getLabel().setColor(Color.DARK_GRAY);
    }
}
