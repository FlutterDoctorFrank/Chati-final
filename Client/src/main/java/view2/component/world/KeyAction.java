package view2.component.world;

import com.badlogic.gdx.Input;

public enum KeyAction {

    MOVE_UP(Input.Keys.UP, Input.Keys.W),
    MOVE_LEFT(Input.Keys.LEFT, Input.Keys.A),
    MOVE_DOWN(Input.Keys.DOWN, Input.Keys.S),
    MOVE_RIGHT(Input.Keys.RIGHT, Input.Keys.D),
    SPRINT(Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT),
    INTERACT(Input.Keys.E),
    OPEN_CHAT(Input.Keys.SPACE),
    OPEN_USER_LIST(Input.Keys.U),
    OPEN_NOTIFICATION(Input.Keys.N),
    OPEN_SETTINGS(Input.Keys.S),
    CLOSE(Input.Keys.ESCAPE);


    private final int[] keys;

    KeyAction(int... keys) {
        this.keys = keys;
    }

    public static KeyAction getAction(int inputKey) {
        for (KeyAction keyAction : KeyAction.values()) {
            for (int key : keyAction.keys) {
                if (inputKey == key) {
                    return keyAction;
                }
            }
        }
        return null;
    }
}
