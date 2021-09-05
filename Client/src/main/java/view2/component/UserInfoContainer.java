package view2.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import model.role.Permission;
import model.role.Role;
import model.user.IUserView;
import model.user.Status;
import view2.Chati;

import java.util.ArrayList;
import java.util.List;

public class UserInfoContainer extends Table {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float SPACING = 10;

    private final IUserView user;

    private final List<Image> roleIcons;
    private final Label usernameLabel;
    private final Table roleIconContainer;

    public UserInfoContainer(IUserView user) {
        this.user = user;
        this.roleIcons = new ArrayList<>();

        usernameLabel = new Label("", Chati.CHATI.getSkin());
        roleIconContainer = new Table();

        add(usernameLabel).space(SPACING);
        roleIconContainer.defaults().size(USER_INFO_ICON_SIZE).space(SPACING / 2);
        updateInfo();
        add(roleIconContainer);
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            updateInfo();
        }
        super.act(delta);
    }

    private void updateInfo() {
        roleIcons.clear();
        roleIconContainer.clear();

        usernameLabel.setText(user.getUsername());

        if (user.hasRole(Role.OWNER)) {
            usernameLabel.setColor(Color.GOLD);
            Image ownerImage = new Image(Chati.CHATI.getTextureRegion("role_owner"));
            ownerImage.addListener(new ChatiToolTip("Besitzer"));
            roleIcons.add(ownerImage);
        } else if (user.hasRole(Role.ADMINISTRATOR)) {
            usernameLabel.setColor(Color.SKY);
            Image administratorImage = new Image(Chati.CHATI.getTextureRegion("role_administrator"));
            administratorImage.addListener(new ChatiToolTip("Administrator"));
            roleIcons.add(administratorImage);
        } else if (user.hasRole(Role.MODERATOR)) {
            usernameLabel.setColor(Color.ORANGE);
            Image moderatorImage = new Image(Chati.CHATI.getTextureRegion("role_moderator"));
            moderatorImage.addListener(new ChatiToolTip("Moderator"));
            roleIcons.add(moderatorImage);
        } else {
            usernameLabel.setColor(Color.WHITE);
        }

        if (user.hasRole(Role.ROOM_OWNER)) {
            Image roomOwnerImage = new Image(Chati.CHATI.getTextureRegion("role_room_owner"));
            roomOwnerImage.addListener(new ChatiToolTip("Raumbesitzer"));
            roleIcons.add(roomOwnerImage);
        }
        if (user.hasRole(Role.AREA_MANAGER)) {
            Image areaManagerImage = new Image(Chati.CHATI.getTextureRegion("role_area_manager"));
            areaManagerImage.addListener(new ChatiToolTip("Bereichsberechtigter"));
            roleIcons.add(areaManagerImage);
        }

        if (user.getStatus() == Status.OFFLINE) {
            usernameLabel.setColor(Color.GRAY);
        } else if (user.isReported() && !user.equals(Chati.CHATI.getUserManager().getInternUserView())
                && (Chati.CHATI.getUserManager().getInternUserView().hasPermission(Permission.BAN_MODERATOR)
                && !user.hasPermission(Permission.BAN_MODERATOR)
                || Chati.CHATI.getUserManager().getInternUserView().hasPermission(Permission.BAN_USER)
                && !user.hasPermission(Permission.BAN_USER) && !user.hasPermission(Permission.BAN_MODERATOR))) {
            usernameLabel.setColor(Color.RED);
        }

        roleIcons.forEach(roleIconContainer::add);
    }
}
