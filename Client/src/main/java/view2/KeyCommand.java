package view2;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public enum KeyCommand {

    MOVE_UP,
    MOVE_LEFT,
    MOVE_DOWN,
    MOVE_RIGHT,
    SPRINT,
    SHOW_NAMES,
    INTERACT,
    OPEN_CHAT,
    SEND_CHAT_MESSAGE,
    PUSH_TO_TALK,
    OPEN_USER_MENU,
    OPEN_NOTIFICATION_MENU,
    OPEN_SETTINGS_MENU,
    OPEN_COMMUNICATION_MENU,
    TOGGLE_MICROPHONE,
    TOGGLE_SOUND,
    CLOSE;

    private final List<Integer> keyBindings;

    KeyCommand() {
        this.keyBindings = new ArrayList<>();
    }

    public void addKeyBindings(List<Integer> keyBindings) {
        this.keyBindings.addAll(keyBindings);
    }

    public boolean matches(int inputKeycode) {
        return keyBindings.contains(inputKeycode);
    }

    public boolean isPressed() {
        return keyBindings.stream().anyMatch(Gdx.input::isKeyPressed);
    }
}
