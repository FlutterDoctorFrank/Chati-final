package view2.userInterface.hud;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.user.IInternUserView;
import view2.Chati;
import view2.userInterface.ChatiWindow;
import view2.userInterface.hud.userList.UserListEntry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Eine Klasse, welche das Kommunikationsfenster der Anwendung repräsentiert.
 */
public class CommunicationWindow extends ChatiWindow {

    private static final float HUD_WINDOW_WIDTH = 450;
    private static final float HUD_WINDOW_HEIGHT = 550;

    private final Set<UserListEntry> currentEntries;
    private final ScrollPane userListScrollPane;
    private final Table userListContainer;

    /**
     * Erzeugt eine neue Instanz des CommunicationWindow.
     */
    public CommunicationWindow() {
        super("window.title.communication", HUD_WINDOW_WIDTH, HUD_WINDOW_HEIGHT);
        this.currentEntries = new TreeSet<>();

        userListContainer = new Table();
        userListScrollPane = new ScrollPane(userListContainer, Chati.CHATI.getSkin());
        userListScrollPane.setFadeScrollBars(false);
        userListScrollPane.setOverscroll(false, false);
        userListScrollPane.setScrollingDisabled(true, false);

        showCommunicableUsers();

        // Layout
        setMovable(false);

        userListContainer.top();
        add(userListScrollPane).grow();
    }

    @Override
    public void act(final float delta) {
        if (Chati.CHATI.isUserInfoChanged()) {
            showCommunicableUsers();
        }
        super.act(delta);
    }

    @Override
    public void close() {
        Chati.CHATI.getHeadUpDisplay().removeCommunicationWindow();
        super.close();
    }

    @Override
    public void focus() {
        if (getStage() != null) {
            getStage().setScrollFocus(userListScrollPane);
        }
    }

    @Override
    public void invalidate() {
        setPosition(0, HeadUpDisplay.BUTTON_SIZE);
    }

    /**
     * Zeigt die Liste aller Benutzer an, mit denen gerade kommuniziert werden kann.
     */
    private void showCommunicableUsers() {
        currentEntries.clear();
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            Chati.CHATI.getUserManager().getCommunicableUsers().values()
                    .forEach(communicableUser -> currentEntries.add(new UserListEntry(communicableUser)));
            layoutEntries();
        }
    }

    /**
     * Zeigt die übergebenen Einträge in der Liste an.
     */
    private void layoutEntries() {
        userListContainer.clearChildren();
        currentEntries.forEach(entry -> userListContainer.add(entry).growX().row());
    }
}
