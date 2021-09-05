package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import view2.Chati;
import view2.ChatiPreferences;
import view2.Settings;
import view2.component.AbstractWindow;
import view2.component.ChatiTextButton;

public class VolumeChangeWindow extends AbstractWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;

    private static final float MIN_VOLUME = 0;
    private static final float MAX_VOLUME = 1;
    private static final float VOLUME_STEP_SIZE = MAX_VOLUME / 100;
    private static final float SNAP_VALUE_STEP_SIZE = MAX_VOLUME / 4;
    private static final float SNAP_VALUE_THRESHOLD = SNAP_VALUE_STEP_SIZE / 10;

    private final ChatiTextButton confirmButton;

    public VolumeChangeWindow() {
        super("Lautstärke anpassen");

        Label infoLabel = new Label("Ändere die Lautstärke!", Chati.CHATI.getSkin());

        Label totalVolumeLabel = new Label("Gesamt", Chati.CHATI.getSkin());
        Label voiceVolumeLabel = new Label("Sprache", Chati.CHATI.getSkin());
        Label musicVolumeLabel = new Label("Musik", Chati.CHATI.getSkin());
        Label soundVolumeLabel = new Label("Hintergrundgeräusche", Chati.CHATI.getSkin());

        Slider totalVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.CHATI.getSkin());
        totalVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        totalVolumeSlider.setValue(Settings.getTotalVolume());
        totalVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        Slider voiceVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.CHATI.getSkin());
        voiceVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        voiceVolumeSlider.setValue(Settings.getVoiceVolume());
        voiceVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        Slider musicVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.CHATI.getSkin());
        musicVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        musicVolumeSlider.setValue(Settings.getMusicVolume());
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        Slider soundVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Chati.CHATI.getSkin());
        soundVolumeSlider.setSnapToValues(new float[]{0, SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        soundVolumeSlider.setValue(Settings.getSoundVolume());
        soundVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        confirmButton = new ChatiTextButton("Übernehmen", false);
        disableButton();
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                disableButton();
                Chati.CHATI.getPreferences().setTotalVolume(totalVolumeSlider.getValue());
                Chati.CHATI.getPreferences().setVoiceVolume(voiceVolumeSlider.getValue());
                Chati.CHATI.getPreferences().setMusicVolume(musicVolumeSlider.getValue());
                Chati.CHATI.getPreferences().setSoundVolume(soundVolumeSlider.getValue());
                infoLabel.setText("Deine Änderungen wurden gespeichert!");
            }
        });

        ChatiTextButton defaultButton = new ChatiTextButton("Standardeinstellung", true);
        defaultButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                enableButton();
                totalVolumeSlider.setValue(ChatiPreferences.DEFAULT_TOTAL_VOLUME);
                voiceVolumeSlider.setValue(ChatiPreferences.DEFAULT_VOICE_VOLUME);
                musicVolumeSlider.setValue(ChatiPreferences.DEFAULT_MUSIC_VOLUME);
                soundVolumeSlider.setValue(ChatiPreferences.DEFAULT_SOUND_VOLUME);
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("Abbrechen", true);
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
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(3).bottom().height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(defaultButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();
    }

    private void enableButton() {
        confirmButton.setTouchable(Touchable.enabled);
        confirmButton.getLabel().setColor(Color.WHITE);
    }

    private void disableButton() {
        confirmButton.setTouchable(Touchable.disabled);
        confirmButton.getLabel().setColor(Color.DARK_GRAY);
    }
}
