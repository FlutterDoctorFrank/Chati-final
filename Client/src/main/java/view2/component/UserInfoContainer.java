package view2.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import model.role.Role;
import model.user.IUserView;
import model.user.Status;
import view2.Assets;
import view2.Chati;

import java.util.ArrayList;
import java.util.List;

public class UserInfoContainer extends Table {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float HORIZONTAL_SPACING = 10;

    private final IUserView user;

    private final List<Image> roleIcons;
    private Label usernameLabel;
    private Table roleIconContainer;

    public UserInfoContainer(IUserView user) {
        this.user = user;
        this.roleIcons = new ArrayList<>();
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            updateInfo();
        }
        super.act(delta);
    }

    private void create() {
        usernameLabel = new Label("", Assets.SKIN);
        roleIconContainer = new Table();
    }

    private void setLayout() {
        add(usernameLabel).space(HORIZONTAL_SPACING);
        roleIconContainer.defaults().size(USER_INFO_ICON_SIZE).space(HORIZONTAL_SPACING / 2);
        updateInfo();
        add(roleIconContainer);
    }

    private void updateInfo() {
        roleIcons.clear();
        roleIconContainer.clear();

        usernameLabel.setText(user.getUsername());
        if (user.hasRole(Role.OWNER)) {
            usernameLabel.setColor(Color.GOLD);
            Image ownerImage = new Image(Assets.OWNER_ICON);
            ownerImage.addListener(new InformationToolTip("Besitzer"));
            roleIcons.add(ownerImage);
        } else if (user.hasRole(Role.ADMINISTRATOR)) {
            usernameLabel.setColor(Color.SKY);
            Image administratorImage = new Image(Assets.ADMINISTRATOR_ICON);
            administratorImage.addListener(new InformationToolTip("Administrator"));
            roleIcons.add(administratorImage);
        } else if (user.hasRole(Role.MODERATOR)) {
            usernameLabel.setColor(Color.ORANGE);
            Image moderatorImage = new Image(Assets.MODERATOR_ICON);
            moderatorImage.addListener(new InformationToolTip("Moderator"));
            roleIcons.add(moderatorImage);
        } else {
            usernameLabel.setColor(Color.WHITE);
        }
        if (user.hasRole(Role.ROOM_OWNER)) {
            Image roomOwnerImage = new Image(Assets.ROOM_OWNER_ICON);
            roomOwnerImage.addListener(new InformationToolTip("Raumbesitzer"));
            roleIcons.add(roomOwnerImage);
        }
        if (user.hasRole(Role.AREA_MANAGER)) {
            Image areaManagerImage = new Image(Assets.AREA_MANAGER_ICON);
            areaManagerImage.addListener(new InformationToolTip("Bereichsberechtigter"));
            roleIcons.add(areaManagerImage);
        }
        if (user.getStatus() == Status.OFFLINE) {
            usernameLabel.setColor(Color.GRAY);
        }

        roleIcons.forEach(roleIconContainer::add);
    }
}
