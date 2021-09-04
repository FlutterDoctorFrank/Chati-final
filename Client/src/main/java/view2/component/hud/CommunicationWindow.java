package view2.component.hud;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.IInternUserView;
import view2.Assets;
import view2.Chati;
import view2.component.AbstractWindow;
import view2.component.hud.userList.UserListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CommunicationWindow extends AbstractWindow {

    private static final float HUD_WINDOW_WIDTH = 400;
    private static final float HUD_WINDOW_HEIGHT = 500;

    private final Set<UserListEntry> communicableUserEntries;
    private final List<Image> communicationMediaImages;

    private Label communicationFormLabel;
    private Label communicationMediaLabel;
    private ScrollPane userListScrollPane;
    private Table userListContainer;
    private TextButton closeButton;

    public CommunicationWindow() {
        super("Kommunikation");
        this.communicableUserEntries = new TreeSet<>();
        this.communicationMediaImages = new ArrayList<>();
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged()) {
            showCommunicableUsers();
        }
    }

    protected void create() {
        communicationFormLabel = new Label("Kommunikationsform:", Assets.SKIN);
        communicationMediaLabel = new Label("Kommunikationsmedien:", Assets.SKIN);

        userListContainer = new Table();
        userListScrollPane = new ScrollPane(userListContainer, Assets.SKIN);

        closeButton = new TextButton("X", Assets.SKIN);
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                close();
            }
        });

        showCommunicableUsers();
    }

    protected void setLayout() {
        setMovable(false);
        setSize(HUD_WINDOW_WIDTH, HUD_WINDOW_HEIGHT);
        setPosition(0, HeadUpDisplay.BUTTON_SIZE);

        userListContainer.top();
        add(userListScrollPane).grow();
        Chati.CHATI.getScreen().getStage().setScrollFocus(userListScrollPane);

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
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

    @Override
    public void open() {
        Chati.CHATI.getScreen().getStage().addActor(this);
    }

    @Override
    public void close() {
        remove();
    }
}
