package view.Sprites;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;
import view.Screens.ApplicationScreen;

import java.util.UUID;

public class UserAvatar extends Avatar{
    public UserAvatar(World world, UUID id, float posX, float posY) {
        super(world, id, posX, posY);
        body.getFixtureList().get(0).setUserData("user-avatar");
    }

    @Override
    protected Direction getDirection() {
        int movementKeyPressed = -2;

        if (!ApplicationScreen.keyPresses.isEmpty()) {
            movementKeyPressed = ApplicationScreen.keyPresses.getLast();
        }

        if (body.getLinearVelocity().y > 0 || movementKeyPressed == Input.Keys.UP)
            return Direction.UP;
        else if (body.getLinearVelocity().y < 0 || movementKeyPressed == Input.Keys.DOWN)
            return Direction.DOWN;
        else if (body.getLinearVelocity().x > 0 || movementKeyPressed == Input.Keys.RIGHT)
            return Direction.RIGHT;
        else if (body.getLinearVelocity().x < 0 || movementKeyPressed == Input.Keys.LEFT)
            return Direction.LEFT;
        else
            return Direction.STAND;
    }
}
