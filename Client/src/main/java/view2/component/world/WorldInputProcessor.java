package view2.component.world;

import com.badlogic.gdx.InputProcessor;
import view2.Chati;
import view2.component.KeyAction;
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
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_UP) {
            moveUpPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_LEFT) {
            moveLeftPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_DOWN) {
            moveDownPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_RIGHT) {
            moveRightPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SPRINT) {
            sprintPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SHOW_NAMES) {
            showNamesPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.INTERACT) {
            Chati.CHATI.getWorldScreen().getInternUserAvatar().interact();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_UP) {
            moveUpPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_LEFT) {
            moveLeftPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_DOWN) {
            moveDownPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_RIGHT) {
            moveRightPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SPRINT) {
            sprintPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SHOW_NAMES) {
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
                return isMoveUpPressed();
            case LEFT:
                return isMoveLeftPressed();
            case DOWN:
                return isMoveDownPressed();
            case RIGHT:
                return isMoveRightPressed();
            default:
                return false;
        }
    }

    public boolean isMoveUpPressed() {
        return moveUpPressed && KeyAction.MOVE_UP.isPressed();
    }

    public boolean isMoveLeftPressed() {
        return moveLeftPressed && KeyAction.MOVE_LEFT.isPressed();
    }

    public boolean isMoveDownPressed() {
        return moveDownPressed && KeyAction.MOVE_DOWN.isPressed();
    }

    public boolean isMoveRightPressed() {
        return moveRightPressed && KeyAction.MOVE_RIGHT.isPressed();
    }

    public boolean isSprintPressed() {
        return sprintPressed && KeyAction.SPRINT.isPressed();
    }

    public boolean isShowNamesPressed() {
        return showNamesPressed && KeyAction.SHOW_NAMES.isPressed();
    }
}
