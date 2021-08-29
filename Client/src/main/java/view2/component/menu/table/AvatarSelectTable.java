package view2.component.menu.table;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.Avatar;
import view2.Assets;
import view2.Chati;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

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
        this.avatarEntries = new HashSet<>();
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle einen Avatar!");

        avatarListContainer = new Table();
        avatarScrollPane = new ScrollPane(avatarListContainer, Assets.SKIN);

        EnumSet.allOf(Avatar.class).forEach(avatar -> avatarEntries.add(new AvatarListEntry(avatar)));

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
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
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).spaceRight(SPACING).growX();
        buttonContainer.add(confirmButton, cancelButton);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {
    }

    private class AvatarListEntry extends TextButton {

        private final Avatar avatar;

        private Image avatarImage;

        public AvatarListEntry(Avatar avatar) {
            super(avatar.getName(), Assets.SKIN);
            this.avatar = avatar;
            create();
            setLayout();
        }

        private void create() {
            TextureAtlas atlas = new TextureAtlas(avatar.getPath());
            TextureAtlas.AtlasRegion stand = atlas.findRegion(avatar.getIdleRegionName());
            TextureRegion avatarStandDown = new TextureRegion(stand, 96, 16, 32, 48);
            avatarImage = new Image(avatarStandDown);
        }

        private void setLayout() {
            getLabelCell().padTop(-2 * SPACING).row();
            add(avatarImage).center().size(AVATAR_IMAGE_SIZE).padBottom(2 * SPACING);
        }
    }
}
