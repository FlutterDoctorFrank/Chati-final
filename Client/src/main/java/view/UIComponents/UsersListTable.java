package view.UIComponents;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.user.IUserView;

public class UsersListTable extends PopupMenu {
    private String currentList;

    public UsersListTable (DropDownMenu menu) {
        super(menu);
        setName("users-list-table");

        TextButton users = new TextButton("Benutzer", skin);
        users.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ScrollPane list = usersList();
                getCell(findActor(currentList)).setActor(list).expand().top();
                currentList = list.getName();

            }
        });
        TextButton friends = new TextButton("Freunde", skin);
        friends.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ScrollPane list = friendsList();
                getCell(findActor(currentList)).setActor(list).expand().top();
                currentList = list.getName();
            }
        });
        SplitPane splitPane = new SplitPane(users, friends, false, skin);
        add(splitPane).fill();
        row();
        ScrollPane list = usersList();
        add(list).expand().top();
        currentList = list.getName();
    }

    private ScrollPane usersList() {
        Table users = new Table();
        TextButton button = new TextButton("button1", skin);
        users.add(button).width(getWidth());

        ScrollPane scrollPane = new ScrollPane(users);
        scrollPane.setName("users-list");
        return scrollPane;
    }

    private ScrollPane friendsList() {
        Table users = new Table();

        /*
        for( var friend : dropDownMenu.getHud().getApplicationScreen().getGame().getUserManager().getFriends().entrySet()) {
           ListElement element = new ListElement(friend.getValue());
           users.addActor(element);
        } */

        ScrollPane scrollPane = new ScrollPane(users);
        scrollPane.setName("friends-list");
        return scrollPane;
    }

    private class ListElement extends VerticalGroup {

        public ListElement(IUserView user) {
            Label name = new Label(user.getUsername(), skin);
            HorizontalGroup actions = new HorizontalGroup();  // Aktionen hinzufügen

            add(name);
            row();
            add(actions);
        }
    }
}
