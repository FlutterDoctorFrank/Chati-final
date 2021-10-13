package view.userInterface.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.user.Avatar;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.userInterface.actor.ChatiTable;
import view.userInterface.actor.ChatiTextButton;
import view.Response;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Eine Klasse, welche das Menü zum Wechseln des Avatars repräsentiert.
 */
public class AvatarSelectTable extends ChatiTable {

    private static final int AVATARS_PER_ROW = 3;
    private static final float AVATAR_BUTTON_MAX_SIZE = ROW_WIDTH / AVATARS_PER_ROW;
    private static final float AVATAR_IMAGE_SIZE = AVATAR_BUTTON_MAX_SIZE / 3;

    private final Set<AvatarListEntry> avatarEntries;

    /**
     * Erzeugt eine neue Instanz des AvatarSelectTable.
     */
    public AvatarSelectTable() {
        super("table.entry.change-avatar");
        this.avatarEntries = new TreeSet<>();

        Table avatarListContainer = new Table();
        ScrollPane avatarScrollPane = new ScrollPane(avatarListContainer, Chati.CHATI.getSkin());
        avatarScrollPane.setFadeScrollBars(false);
        avatarScrollPane.setOverscroll(false, false);
        avatarScrollPane.setScrollingDisabled(true, false);

        ButtonGroup<AvatarListEntry> avatarButtonGroup = new ButtonGroup<>();
        avatarButtonGroup.setMinCheckCount(1);
        avatarButtonGroup.setMaxCheckCount(1);
        avatarButtonGroup.setUncheckLast(true);

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getScreen().setPendingResponse(Response.AVATAR_CHANGE);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, avatarButtonGroup.getChecked().getAvatar());
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });

        EnumSet.allOf(Avatar.class).forEach(avatar -> {
            AvatarListEntry avatarListEntry = new AvatarListEntry(avatar);
            avatarEntries.add(avatarListEntry);
            avatarButtonGroup.add(avatarListEntry);

            if (Chati.CHATI.getInternUser() != null && avatar == Chati.CHATI.getInternUser().getAvatar()) {
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

        // Translatable register
        translatables.add(confirmButton);
        translatables.add(cancelButton);
        translatables.trimToSize();
    }

    @Override
    public void resetTextFields() {

    }

    /**
     * Eine Klasse, welche einen Eintrag in der Liste aller verfügbaren Avatare repräsentiert.
     */
    private static class AvatarListEntry extends ChatiTextButton implements Comparable<AvatarListEntry> {

        private final Avatar avatar;

        /**
         * Erzeugt eine neue Instanz eines AvatarListEntry.
         * @param avatar Zum Eintrag zugehöriger Avatar.
         */
        public AvatarListEntry(@NotNull final Avatar avatar) {
            super(avatar, false);
            this.avatar = avatar;
            getLabelCell().padTop(-2 * SPACING).row();
            TextureRegion avatarDrawable = Chati.CHATI.getDrawable(avatar.getPath() + "_stand_down").getRegion();
            add(new Image(avatarDrawable)).center().size(AVATAR_IMAGE_SIZE).padBottom(2 * SPACING);
        }

        @Override
        public void draw(@NotNull final Batch batch, final float parentAlpha) {
            if (Chati.CHATI.getInternUser() != null && this.avatar != Chati.CHATI.getInternUser().getAvatar() &&
                    this.avatar.getName().matches("\\s*")) {
                super.draw(batch, 0.01f);
            } else {
                super.draw(batch, 1);
            }
        }

        /**
         * Gibt den Avatar dieses Eintrags zurück.
         * @return den Avatar des Eintrags.
         */
        public @NotNull Avatar getAvatar() {
            return avatar;
        }

        @Override
        public int compareTo(@NotNull final AvatarSelectTable.AvatarListEntry other) {
            return Chati.CHATI.getInternUser() != null && this.avatar == Chati.CHATI.getInternUser().getAvatar() ? -1
                    : (this.avatar.getName().matches("\\s*")) ? 1
                    : this.avatar.getName().compareTo(other.avatar.getName());
        }
    }
}
