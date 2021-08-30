package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import view2.Assets;
import view2.Settings;
import view2.component.AbstractWindow;
import view2.component.hud.HeadUpDisplay;
import view2.component.hud.HudMenuWindow;

public class VolumeChangeWindow extends AbstractWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;

    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 1f;
    private static final float VOLUME_STEP_SIZE = 0.01f;
    private static final float SNAP_VALUE_STEP_SIZE = 0.25f;
    private static final float SNAP_VALUE_THRESHOLD = 0.05f;

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
        totalVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                        3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        totalVolumeSlider.setValue(Settings.getTotalVolume());
        totalVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        voiceVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Assets.SKIN);
        voiceVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                        3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        voiceVolumeSlider.setValue(Settings.getVoiceVolume());
        voiceVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        musicVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Assets.SKIN);
        musicVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                        3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        musicVolumeSlider.setValue(Settings.getMusicVolume());
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        soundVolumeSlider = new Slider(MIN_VOLUME, MAX_VOLUME, VOLUME_STEP_SIZE, false, Assets.SKIN);
        soundVolumeSlider.setSnapToValues(new float[]{SNAP_VALUE_STEP_SIZE, 2 * SNAP_VALUE_STEP_SIZE,
                        3 * SNAP_VALUE_STEP_SIZE}, SNAP_VALUE_THRESHOLD);
        soundVolumeSlider.setValue(Settings.getSoundVolume());
        soundVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Ändere die Lautstärke!");
            }
        });

        confirmButton = new TextButton("Übernehmen", Assets.getNewSkin());
        disableButton();
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                disableButton();
                Settings.setTotalVolume(totalVolumeSlider.getValue());
                Settings.setVoiceVolume(voiceVolumeSlider.getValue());
                Settings.setMusicVolume(musicVolumeSlider.getValue());
                Settings.setSoundVolume(soundVolumeSlider.getValue());
                infoLabel.setText("Deine Änderungen wurden gespeichert!");
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
                enableButton();
                totalVolumeSlider.setValue(Settings.DEFAULT_TOTAL_VOLUME);
                voiceVolumeSlider.setValue(Settings.DEFAULT_VOICE_VOLUME);
                musicVolumeSlider.setValue(Settings.DEFAULT_MUSIC_VOLUME);
                soundVolumeSlider.setValue(Settings.DEFAULT_SOUND_VOLUME);
                infoLabel.setText("Ändere die Lautstärke!");
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

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    private void enableButton() {
        confirmButton.setDisabled(false);
        confirmButton.setTouchable(Touchable.enabled);
        confirmButton.getLabel().setColor(Color.WHITE);
        confirmButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
    }

    private void disableButton() {
        confirmButton.setDisabled(true);
        confirmButton.setTouchable(Touchable.disabled);
        confirmButton.getLabel().setColor(Color.DARK_GRAY);
        confirmButton.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
    }
}
