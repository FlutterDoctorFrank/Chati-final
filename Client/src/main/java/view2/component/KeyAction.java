package view2.component;

import com.badlogic.gdx.Input;

public enum KeyAction {

    MOVE_UP(Input.Keys.UP, Input.Keys.W),
    MOVE_LEFT(Input.Keys.LEFT, Input.Keys.A),
    MOVE_DOWN(Input.Keys.DOWN, Input.Keys.S),
    MOVE_RIGHT(Input.Keys.RIGHT, Input.Keys.D),
    SPRINT(Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT),
    INTERACT(Input.Keys.E, Input.Keys.ENTER),
    OPEN_CHAT(Input.Keys.SPACE),
    SWITCH_MICROPHONE(Input.Keys.Q),
    OPEN_USER_LIST(Input.Keys.NUM_1, Input.Keys.NUMPAD_1, Input.Keys.G),
    OPEN_NOTIFICATION(Input.Keys.NUM_2, Input.Keys.NUMPAD_2, Input.Keys.H),
    OPEN_SETTINGS(Input.Keys.NUM_3, Input.Keys.NUMPAD_3, Input.Keys.J),
    CLOSE(Input.Keys.ESCAPE);

    private final int[] keycodes;

    KeyAction(int... keycodes) {
        this.keycodes = keycodes;
    }

    public static KeyAction getAction(int inputKeycode) {
        for (KeyAction keyAction : KeyAction.values()) {
            for (int keycode : keyAction.keycodes) {
                if (inputKeycode == keycode) {
                    return keyAction;
                }
            }
        }
        return null;
    }
}
