package view2.component.menu.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.user.Avatar;
import org.jetbrains.annotations.NotNull;
import view2.Assets;
import view2.Chati;
import view2.component.Response;
import view2.component.hud.HudMenuWindow;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

public class AvatarSelectTable extends MenuTable {

    private static final int AVATARS_PER_ROW = 3;
    private static final float AVATAR_BUTTON_MAX_SIZE = ROW_WIDTH / AVATARS_PER_ROW;
    private static final float AVATAR_IMAGE_SIZE = AVATAR_BUTTON_MAX_SIZE / 3;

    private final Set<AvatarListEntry> avatarEntries;

    private ScrollPane avatarScrollPane;
    private Table avatarListContainer;
    private TextButton confirmButton;
    private TextButton cancelButton;

    public AvatarSelectTable() {
        this.avatarEntries = new TreeSet<>();
        create();
        setLayout();
    }

    @Override
    protected void create() {
        ButtonGroup<AvatarListEntry> avatarButtonGroup = new ButtonGroup<>();
        avatarButtonGroup.setMinCheckCount(1);
        avatarButtonGroup.setMaxCheckCount(1);
        avatarButtonGroup.setUncheckLast(true);

        infoLabel.setText("Bitte wähle einen Avatar!");

        avatarListContainer = new Table();
        avatarScrollPane = new ScrollPane(avatarListContainer, Assets.SKIN);

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getScreen().setPendingResponse(Response.AVATAR_CHANGE);
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.PROFILE_CHANGE,
                        avatarButtonGroup.getChecked().getAvatar());
            }
        });

        cancelButton = new TextButton("Zurück", Assets.SKIN);
        cancelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });

        EnumSet.allOf(Avatar.class).forEach(avatar -> {
            AvatarListEntry avatarListEntry = new AvatarListEntry(avatar);
            avatarEntries.add(avatarListEntry);
            avatarListEntry.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (!avatarListEntry.isChecked()) {
                        unselectButton(avatarButtonGroup.getChecked());
                        return true;
                    }
                    return false;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    selectButton(avatarListEntry);
                }
            });
            avatarButtonGroup.add(avatarListEntry);
            if (avatar == Chati.CHATI.getUserManager().getInternUserView().getAvatar()) {
                selectButton(avatarListEntry);
            }
        });
    }

    @Override
    protected void setLayout() {
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();

        avatarListContainer.top().left();
        avatarListContainer.top().left().defaults().colspan(AVATARS_PER_ROW).height(AVATAR_BUTTON_MAX_SIZE)
                .maxWidth(AVATAR_BUTTON_MAX_SIZE).top().left().grow();
        int colIndex = 0;
        for (AvatarListEntry avatarEntry : avatarEntries) {
            avatarListContainer.add(avatarEntry);
            if (AVATARS_PER_ROW <= ++colIndex) {
                avatarListContainer.row();
                colIndex = 0;
            }
        }
        container.add(avatarScrollPane).height(2 * AVATAR_BUTTON_MAX_SIZE).grow().row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);

        Chati.CHATI.getScreen().getStage().setScrollFocus(avatarScrollPane);
    }

    @Override
    public void resetTextFields() {
    }

    private void selectButton(TextButton button) {
        button.setChecked(true);
        button.getLabel().setColor(Color.MAGENTA);
        button.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
    }

    private void unselectButton(TextButton button) {
        button.setChecked(false);
        button.getLabel().setColor(Color.WHITE);
        button.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
    }

    private static class AvatarListEntry extends TextButton implements Comparable<AvatarListEntry> {

        private final Avatar avatar;
        private Image avatarImage;

        public AvatarListEntry(Avatar avatar) {
            super(avatar.getName(), Assets.getNewSkin());
            this.avatar = avatar;
            create();
            setLayout();
        }

        private void create() {
            TextureAtlas atlas = new TextureAtlas(avatar.getPath());
            TextureAtlas.AtlasRegion stand = atlas.findRegion(avatar.getIdleRegionName());
            TextureRegion avatarStandDown = new TextureRegion(stand, 96, 0, 32, 48);
            avatarImage = new Image(avatarStandDown);
        }

        private void setLayout() {
            getLabelCell().padTop(-2 * SPACING).row();
            add(avatarImage).center().size(AVATAR_IMAGE_SIZE).padBottom(2 * SPACING);
        }

        public Avatar getAvatar() {
            return avatar;
        }

        @Override
        public int compareTo(@NotNull AvatarSelectTable.AvatarListEntry other) {
            return this.avatar == Chati.CHATI.getUserManager().getInternUserView().getAvatar() ? -1
                    : this.avatar.getName().compareTo(other.avatar.getName());
        }
    }
}
