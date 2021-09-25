package view2.userInterface.hud;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.user.IInternUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiWindow;
import view2.userInterface.hud.userList.UserListEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Eine Klasse, welche das Kommunikationsfenster der Anwendung repräsentiert.
 */
public class CommunicationWindow extends ChatiWindow {

    private static final float HUD_WINDOW_WIDTH = 425;
    private static final float HUD_WINDOW_HEIGHT = 550;

    private final Set<UserListEntry> communicableUserEntries;
    private final Table userListContainer;

    /**
     * Erzeugt eine neue Instanz des CommunicationWindow.
     */
    public CommunicationWindow() {
        super("window.title.communication", HUD_WINDOW_WIDTH, HUD_WINDOW_HEIGHT);
        this.communicableUserEntries = new TreeSet<>();
        List<Image> communicationMediaImages = new ArrayList<>(); // TODO

        ChatiLabel communicationFormLabel = new ChatiLabel("menu.label.communication-form"); // TODO
        ChatiLabel communicationMediaLabel = new ChatiLabel("menu.label.communication-media"); // TODO

        userListContainer = new Table();
        ScrollPane userListScrollPane = new ScrollPane(userListContainer, Chati.CHATI.getSkin());

        showCommunicableUsers();

        // Layout
        setMovable(false);

        userListContainer.top();
        add(userListScrollPane).grow();
        Chati.CHATI.getScreen().getStage().setScrollFocus(userListScrollPane);

        // Translatable register
        translates.add(communicationFormLabel);
        translates.add(communicationMediaLabel);
        translates.trimToSize();
    }

    @Override
    public void act(final float delta) {
        if (Chati.CHATI.isUserInfoChanged()) {
            showCommunicableUsers();
        }
    }

    @Override
    public void close() {
        Chati.CHATI.getHeadUpDisplay().removeCommunicationWindow();
        super.close();
    }

    @Override
    public void invalidate() {
        setPosition(0, HeadUpDisplay.BUTTON_SIZE);
    }

    /**
     * Zeigt die Liste aller Benutzer an, mit denen gerade kommuniziert werden kann.
     */
    private void showCommunicableUsers() {
        communicableUserEntries.clear();
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            Chati.CHATI.getUserManager().getCommunicableUsers().values()
                    .forEach(communicableUser -> communicableUserEntries.add(new UserListEntry(communicableUser)));
            layoutEntries(communicableUserEntries);
        }
    }

    /**
     * Zeigt die übergebenen Einträge in der Liste an.
     * @param entries Anzuzeigende Einträge.
     */
    private void layoutEntries(@NotNull final Set<UserListEntry> entries) {
        userListContainer.clearChildren();
        entries.forEach(entry -> userListContainer.add(entry).growX().row());
    }
}
