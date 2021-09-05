package view2.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.Arrays;

public enum KeyAction {

    MOVE_UP(Input.Keys.UP, Input.Keys.W),
    MOVE_LEFT(Input.Keys.LEFT, Input.Keys.A),
    MOVE_DOWN(Input.Keys.DOWN, Input.Keys.S),
    MOVE_RIGHT(Input.Keys.RIGHT, Input.Keys.D),
    SPRINT(Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT),
    SHOW_NAMES(Input.Keys.TAB),
    INTERACT(Input.Keys.E),
    OPEN_CHAT(Input.Keys.SPACE),
    SEND_CHAT_MESSAGE(Input.Keys.ENTER, Input.Keys.NUMPAD_ENTER),
    PUSH_TO_TALK(Input.Keys.P),
    OPEN_COMMUNICATION_MENU(Input.Keys.NUM_1, Input.Keys.NUMPAD_1),
    TOGGLE_MICROPHONE(Input.Keys.NUM_2, Input.Keys.NUMPAD_2),
    TOGGLE_SOUND(Input.Keys.NUM_3, Input.Keys.NUMPAD_3),
    OPEN_USER_LIST(Input.Keys.NUM_4, Input.Keys.NUMPAD_4),
    OPEN_NOTIFICATION(Input.Keys.NUM_5, Input.Keys.NUMPAD_5),
    OPEN_SETTINGS(Input.Keys.NUM_6, Input.Keys.NUMPAD_6),
    CLOSE(Input.Keys.ESCAPE);

    private final int[] keycodes;

    KeyAction(int... keycodes) {
        this.keycodes = keycodes;
    }

    public boolean matches(int inputKeycode) {
        return Arrays.stream(keycodes).anyMatch(keycode -> keycode == inputKeycode);
    }

    public boolean isPressed() {
        for (int keycode : keycodes) {
            if (Gdx.input.isKeyPressed(keycode)) {
                return true;
            }
        }
        return false;
    }
}
