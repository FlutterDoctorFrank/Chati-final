package view2.world;

import com.badlogic.gdx.InputProcessor;
import view2.Chati;
import view2.userInterface.KeyAction;
import model.context.spatial.Direction;

public class WorldInputProcessor implements InputProcessor {

    private boolean moveUpPressed;
    private boolean moveLeftPressed;
    private boolean moveDownPressed;
    private boolean moveRightPressed;
    private boolean sprintPressed;
    private boolean showNamesPressed;

    @Override
    public boolean keyDown(int keycode) {
        if (KeyAction.MOVE_UP.matches(keycode)) {
            moveUpPressed = true;
        }
        if (KeyAction.MOVE_LEFT.matches(keycode)) {
            moveLeftPressed = true;
        }
        if (KeyAction.MOVE_DOWN.matches(keycode)) {
            moveDownPressed = true;
        }
        if (KeyAction.MOVE_RIGHT.matches(keycode)) {
            moveRightPressed = true;
        }
        if (KeyAction.SPRINT.matches(keycode)) {
            sprintPressed = true;
        }
        if (KeyAction.SHOW_NAMES.matches(keycode)) {
            showNamesPressed = true;
        }
        if (KeyAction.INTERACT.matches(keycode)) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().interact();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (KeyAction.MOVE_UP.matches(keycode)) {
            moveUpPressed = false;
        }
        if (KeyAction.MOVE_LEFT.matches(keycode)) {
            moveLeftPressed = false;
        }
        if (KeyAction.MOVE_DOWN.matches(keycode)) {
            moveDownPressed = false;
        }
        if (KeyAction.MOVE_RIGHT.matches(keycode)) {
            moveRightPressed = false;
        }
        if (KeyAction.SPRINT.matches(keycode)) {
            sprintPressed = false;
        }
        if (KeyAction.SHOW_NAMES.matches(keycode)) {
            showNamesPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Chati.CHATI.getWorldScreen().getCamera().zoom(amountY > 0);
        return true;
    }

    public boolean isDirectionPressed(Direction direction) {
        switch (direction) {
            case UP:
                return moveUpPressed && KeyAction.MOVE_UP.isPressed();
            case LEFT:
                return moveLeftPressed && KeyAction.MOVE_LEFT.isPressed();
            case DOWN:
                return moveDownPressed && KeyAction.MOVE_DOWN.isPressed();
            case RIGHT:
                return moveRightPressed && KeyAction.MOVE_RIGHT.isPressed();
            default:
                return false;
        }
    }

    public boolean isSprintPressed() {
        return sprintPressed && KeyAction.SPRINT.isPressed();
    }

    public boolean isShowNamesPressed() {
        return showNamesPressed && KeyAction.SHOW_NAMES.isPressed();
    }
}
