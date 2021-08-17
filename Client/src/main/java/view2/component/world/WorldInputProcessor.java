package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import view2.component.hud.ChatWindow;

public class WorldInputProcessor implements InputProcessor {

    private boolean moveUpPressed;
    private boolean moveLeftPressed;
    private boolean moveDownPressed;
    private boolean moveRightPressed;
    private boolean sprintPressed;

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
        // TODO other keys...

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

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ChatWindow chatWindow = ChatWindow.getInstance();
        if (screenX < chatWindow.getX() || screenX > chatWindow.getX() + chatWindow.getWidth()
                || Gdx.graphics.getHeight() - screenY < chatWindow.getY()
                || Gdx.graphics.getHeight() - screenY > chatWindow.getY() + chatWindow.getHeight()) {
            chatWindow.getStage().unfocus(chatWindow);
        }
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
        return true;
    }

    public boolean isMoveUpPressed() {
        return moveUpPressed;
    }

    public boolean isMoveLeftPressed() {
        return moveLeftPressed;
    }

    public boolean isMoveDownPressed() {
        return moveDownPressed;
    }

    public boolean isMoveRightPressed() {
        return moveRightPressed;
    }

    public boolean isSprintPressed() {
        return sprintPressed;
    }
}
