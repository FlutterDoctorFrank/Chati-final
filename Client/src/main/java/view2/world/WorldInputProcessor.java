package view2.world;

import com.badlogic.gdx.InputProcessor;
import view2.Chati;
import view2.KeyCommand;
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
        if (KeyCommand.MOVE_UP.matches(keycode)) {
            moveUpPressed = true;
        }
        if (KeyCommand.MOVE_LEFT.matches(keycode)) {
            moveLeftPressed = true;
        }
        if (KeyCommand.MOVE_DOWN.matches(keycode)) {
            moveDownPressed = true;
        }
        if (KeyCommand.MOVE_RIGHT.matches(keycode)) {
            moveRightPressed = true;
        }
        if (KeyCommand.SPRINT.matches(keycode)) {
            sprintPressed = true;
        }
        if (KeyCommand.SHOW_NAMES.matches(keycode)) {
            showNamesPressed = true;
        }
        if (KeyCommand.INTERACT.matches(keycode)) {
            System.out.println("Pressed " + KeyCommand.INTERACT);
            Chati.CHATI.getWorldScreen().getInternUserAvatar().interact();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (KeyCommand.MOVE_UP.matches(keycode)) {
            moveUpPressed = false;
        }
        if (KeyCommand.MOVE_LEFT.matches(keycode)) {
            moveLeftPressed = false;
        }
        if (KeyCommand.MOVE_DOWN.matches(keycode)) {
            moveDownPressed = false;
        }
        if (KeyCommand.MOVE_RIGHT.matches(keycode)) {
            moveRightPressed = false;
        }
        if (KeyCommand.SPRINT.matches(keycode)) {
            sprintPressed = false;
        }
        if (KeyCommand.SHOW_NAMES.matches(keycode)) {
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
                return moveUpPressed && KeyCommand.MOVE_UP.isPressed();
            case LEFT:
                return moveLeftPressed && KeyCommand.MOVE_LEFT.isPressed();
            case DOWN:
                return moveDownPressed && KeyCommand.MOVE_DOWN.isPressed();
            case RIGHT:
                return moveRightPressed && KeyCommand.MOVE_RIGHT.isPressed();
            default:
                return false;
        }
    }

    public boolean isSprintPressed() {
        return sprintPressed && KeyCommand.SPRINT.isPressed();
    }

    public boolean isShowNamesPressed() {
        return showNamesPressed && KeyCommand.SHOW_NAMES.isPressed();
    }
}
