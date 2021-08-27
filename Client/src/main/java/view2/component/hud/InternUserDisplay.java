package view2.component.hud;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import model.user.IInternUserView;
import model.user.Status;
import view2.Chati;
import view2.Assets;
import view2.component.AbstractTable;
import view2.component.InformationToolTip;
import view2.component.UserInfoContainer;

public class InternUserDisplay extends AbstractTable {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;

    private StatusSelectTable statusSelectTable;
    private UserInfoContainer userInfoContainer;
    private Image statusImage;
    private TextButton statusButton;

    public InternUserDisplay() {
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            if (internUser != null) {
                setStatusImage();
            }
        }
        super.act(delta);
    }

    @Override
    protected void create() {
        this.userInfoContainer = new UserInfoContainer(Chati.CHATI.getUserManager().getInternUserView());

        statusButton = new TextButton("", Assets.getNewSkin());
        statusButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (statusSelectTable == null) {
                    statusSelectTable = new StatusSelectTable();
                    add(statusSelectTable).top().left().row();
                    statusButton.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
                } else {
                    statusSelectTable.remove();
                    statusSelectTable = null;
                    statusButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
                }
            }
        });

        statusImage = new Image();
        setStatusImage();
    }

    @Override
    protected void setLayout() {
        top().left();
        Table container = new Table();

        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Assets.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        container.setBackground(controlsBackground);
        container.left().defaults().top().padTop(VERTICAL_SPACING);

        statusButton.add(statusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                .padLeft(USER_INFO_ICON_SIZE).padRight(USER_INFO_ICON_SIZE);
        container.add(statusButton).width(2 * USER_INFO_ICON_SIZE).height(2 * USER_INFO_ICON_SIZE)
                .space(HORIZONTAL_SPACING).padLeft(HORIZONTAL_SPACING);
        container.add(userInfoContainer).left().center().height(USER_INFO_ICON_SIZE).row();
        add(container).top().left().width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.BUTTON_SIZE).growY().row();
    }

    private void setStatusImage() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        statusImage.getListeners().forEach(statusImage::removeListener);
        switch (internUser.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Assets.ONLINE_ICON);
                statusImage.addListener(new InformationToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Assets.AWAY_ICON);
                statusImage.addListener(new InformationToolTip("Abwesend"));
                break;
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }
    }

    private static class StatusSelectTable extends AbstractTable {

        ButtonGroup<TextButton> statusButtonGroup;
        private Image onlineStatusImage;
        private Image busyStatusImage;
        private Image offlineStatusImage;
        private TextButton onlineStatusButton;
        private TextButton busyStatusButton;
        private TextButton offlineStatusButton;

        public StatusSelectTable() {
            create();
            setLayout();
        }

        @Override
        protected void create() {
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            statusButtonGroup = new ButtonGroup<>();
            statusButtonGroup.setMinCheckCount(1);
            statusButtonGroup.setMaxCheckCount(1);
            statusButtonGroup.setUncheckLast(true);

            onlineStatusImage = new Image(Assets.ONLINE_ICON);
            busyStatusImage = new Image(Assets.BUSY_ICON);
            offlineStatusImage = new Image(Assets.OFFLINE_ICON);

            onlineStatusButton = new TextButton("", Assets.getNewSkin());
            onlineStatusButton.addListener(new InformationToolTip("Online"));
            if (internUser.getStatus() == Status.ONLINE || internUser.getStatus() == Status.AWAY) {
                onlineStatusButton.setChecked(true);
                onlineStatusButton.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
            }
            onlineStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !onlineStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    onlineStatusButton.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
                    busyStatusButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
                    offlineStatusButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
                }
            });

            busyStatusButton = new TextButton("", Assets.getNewSkin());
            busyStatusButton.addListener(new InformationToolTip("Beschäftigt"));
            /*
            if (internUser.getStatus() == Status.BUSY) {
                busyStatusButton.setChecked(true);
                busyStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
            }

             */

            busyStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !busyStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    onlineStatusButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
                    busyStatusButton.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
                    offlineStatusButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
                }
            });

            offlineStatusButton = new TextButton("", Assets.getNewSkin());
            offlineStatusButton.addListener(new InformationToolTip("Unsichtbar"));
            /*
            if (internUser.getStatus() == Status.INVISIBLE) {
                offlineStatusButton.setChecked(true);
                offlineStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
            }

             */

            offlineStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !offlineStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    onlineStatusButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
                    busyStatusButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
                    offlineStatusButton.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
                }
            });

            statusButtonGroup.add(onlineStatusButton);
            statusButtonGroup.add(busyStatusButton);
            statusButtonGroup.add(offlineStatusButton);
        }

        @Override
        protected void setLayout() {
            Table container = new Table();

            NinePatchDrawable controlsBackground =
                    new NinePatchDrawable(new NinePatch(Assets.SKIN.getRegion("panel1"), 10, 10, 10, 10));
            container.setBackground(controlsBackground);

            onlineStatusButton.add(onlineStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);
            busyStatusButton.add(busyStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);
            offlineStatusButton.add(offlineStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);

            container.add(onlineStatusButton).size(2 * USER_INFO_ICON_SIZE)
                   .spaceBottom(VERTICAL_SPACING).padTop(VERTICAL_SPACING).row();
            container.add(busyStatusButton).size(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).row();
            container.add(offlineStatusButton).size(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).padBottom(VERTICAL_SPACING);

            add(container).top().left().width(HeadUpDisplay.BUTTON_SIZE + HORIZONTAL_SPACING)
                    .padTop(HeadUpDisplay.BUTTON_SIZE - VERTICAL_SPACING).expand();
        }
    }
}