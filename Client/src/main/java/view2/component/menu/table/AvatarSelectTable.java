package view2.component.menu.table;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.user.Avatar;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.component.ChatiTextButton;
import view2.component.Response;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

public class AvatarSelectTable extends MenuTable {

    private static final int AVATARS_PER_ROW = 3;
    private static final float AVATAR_BUTTON_MAX_SIZE = ROW_WIDTH / AVATARS_PER_ROW;
    private static final float AVATAR_IMAGE_SIZE = AVATAR_BUTTON_MAX_SIZE / 3;

    private final Set<AvatarListEntry> avatarEntries;

    public AvatarSelectTable() {
        infoLabel.setText("Bitte wähle einen Avatar!");

        this.avatarEntries = new TreeSet<>();

        Table avatarListContainer = new Table();
        ScrollPane avatarScrollPane = new ScrollPane(avatarListContainer, Chati.CHATI.getAssetManager().getSkin());

        ButtonGroup<AvatarListEntry> avatarButtonGroup = new ButtonGroup<>();
        avatarButtonGroup.setMinCheckCount(1);
        avatarButtonGroup.setMaxCheckCount(1);
        avatarButtonGroup.setUncheckLast(true);

        TextButton confirmButton = new ChatiTextButton("Bestätigen", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getScreen().setPendingResponse(Response.AVATAR_CHANGE);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, avatarButtonGroup.getChecked().getAvatar());
            }
        });

        TextButton cancelButton = new ChatiTextButton("Zurück", true);
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
            avatarButtonGroup.add(avatarListEntry);

            if (avatar == Chati.CHATI.getInternUser().getAvatar()) {
                avatarListEntry.setChecked(true);
            }
        });

        // Layout
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

    private static class AvatarListEntry extends ChatiTextButton implements Comparable<AvatarListEntry> {

        private final Avatar avatar;

        public AvatarListEntry(Avatar avatar) {
            super(avatar.getName(), false);
            this.avatar = avatar;

            TextureAtlas atlas = new TextureAtlas(avatar.getPath()); // TODO Avatare im Assetmanager laden
            TextureAtlas.AtlasRegion stand = atlas.findRegion(avatar.getIdleRegionName());
            TextureRegion avatarStandDown = new TextureRegion(stand, 96, 0, 32, 48);
            Image avatarImage = new Image(avatarStandDown);

            getLabelCell().padTop(-2 * SPACING).row();
            add(avatarImage).center().size(AVATAR_IMAGE_SIZE).padBottom(2 * SPACING);
        }

        public Avatar getAvatar() {
            return avatar;
        }

        @Override
        public int compareTo(@NotNull AvatarSelectTable.AvatarListEntry other) {
            return this.avatar == Chati.CHATI.getInternUser().getAvatar() ? -1
                    : this.avatar.getName().compareTo(other.avatar.getName());
        }
    }
}
