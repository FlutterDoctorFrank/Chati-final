package view2.userInterface.hud;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.user.IInternUserView;
import view2.Chati;
import view2.userInterface.ChatiWindow;
import view2.userInterface.hud.userList.UserListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CommunicationWindow extends ChatiWindow {

    private static final float HUD_WINDOW_WIDTH = 400;
    private static final float HUD_WINDOW_HEIGHT = 500;

    private final Set<UserListEntry> communicableUserEntries;
    private final Table userListContainer;

    public CommunicationWindow() {
        super("Kommunikation");
        this.communicableUserEntries = new TreeSet<>();
        List<Image> communicationMediaImages = new ArrayList<>(); // TODO

        Label communicationFormLabel = new Label("Kommunikationsform:", Chati.CHATI.getSkin()); // TODO
        Label communicationMediaLabel = new Label("Kommunikationsmedien:", Chati.CHATI.getSkin()); // TODO

        userListContainer = new Table();
        ScrollPane userListScrollPane = new ScrollPane(userListContainer, Chati.CHATI.getSkin());

        showCommunicableUsers();

        // Layout
        setMovable(false);
        setSize(HUD_WINDOW_WIDTH, HUD_WINDOW_HEIGHT);
        setPosition(0, HeadUpDisplay.BUTTON_SIZE);

        userListContainer.top();
        add(userListScrollPane).grow();
        Chati.CHATI.getScreen().getStage().setScrollFocus(userListScrollPane);
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged()) {
            showCommunicableUsers();
        }
    }

    @Override
    public void close() {
        HeadUpDisplay.getInstance().removeCommunicationWindow();
        super.close();
    }

    private void showCommunicableUsers() {
        communicableUserEntries.clear();
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            Chati.CHATI.getUserManager().getCommunicableUsers().values()
                    .forEach(communicableUser -> communicableUserEntries.add(new UserListEntry(communicableUser)));
            layoutEntries(communicableUserEntries);
        }
    }

    private void layoutEntries(Set<UserListEntry> entries) {
        userListContainer.clearChildren();
        entries.forEach(entry -> userListContainer.add(entry).growX().row());
    }
}
