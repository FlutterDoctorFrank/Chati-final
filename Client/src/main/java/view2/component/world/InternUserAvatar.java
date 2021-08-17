package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import model.user.IInternUserView;

import java.util.*;

public class InternUserAvatar extends UserAvatar {

    private static final float SPRINT_SPEED_FACTOR = 2f;
    private final LinkedList<Direction> currentDirectionalInputs;

    public InternUserAvatar(IInternUserView internUser) {
        super(internUser);
        body.getFixtureList().get(0).setUserData("user-avatar");
        currentDirectionalInputs = new LinkedList<>();
    }

    public void handleInput() {
        Direction currentDirection = getCurrentDirection();
        int velocity = BODY_MOVING_FORCE;
        if (WorldScreen.getInstance().getWorldInputProcessor().isSprintPressed()) {
            velocity *= SPRINT_SPEED_FACTOR;
        }
        if (currentDirection == null) {
            body.setLinearVelocity(0, 0);
        } else {
            switch (currentDirection) {
                case UP:
                    body.setLinearVelocity(0, velocity);
                    break;
                case LEFT:
                    body.setLinearVelocity(-velocity, 0);
                    break;
                case DOWN:
                    body.setLinearVelocity(0, -velocity);
                    break;
                case RIGHT:
                    body.setLinearVelocity(velocity, 0);
                    break;
                default:
                    body.setLinearVelocity(0, 0);
            }
        }
        positionCamera();

        //sendPositionToServer(body.getPosition().x * Chati.PPM, body.getPosition().y * Chati.PPM);
    }

    private Direction getCurrentDirection() {
        Arrays.stream(Direction.values()).forEach(direction -> {
            if (!currentDirectionalInputs.contains(direction) && direction.isPressed()) {
                currentDirectionalInputs.add(direction);
            }
            if (currentDirectionalInputs.contains(direction) && !direction.isPressed()) {
                currentDirectionalInputs.remove(direction);
            }
        });
        return currentDirectionalInputs.peekLast();
    }

    private void positionCamera() {
        TiledMapTileLayer layer = (TiledMapTileLayer) WorldScreen.getInstance().getTiledMap().getLayers().get(0);
        float mapWidth = layer.getWidth() * layer.getTileWidth();
        float mapHeight = layer.getHeight() * layer.getTileHeight();

        float cameraTopBoundary = (mapHeight - Gdx.graphics.getHeight() / 2f) / WorldScreen.PPM;
        float cameraLeftBoundary = (Gdx.graphics.getWidth() / 2f) / WorldScreen.PPM;
        float cameraBottomBoundary = (Gdx.graphics.getHeight() / 2f) / WorldScreen.PPM;
        float cameraRightBoundary = (mapWidth - Gdx.graphics.getWidth() / 2f) / WorldScreen.PPM;

        Vector2 bodyPosition = body.getPosition();
        if (bodyPosition.x >= cameraLeftBoundary && bodyPosition.x <= cameraRightBoundary) {
            WorldScreen.getInstance().getGameViewport().getCamera().position.x = body.getPosition().x;
        } else if (bodyPosition.x < cameraLeftBoundary) {
            WorldScreen.getInstance().getGameViewport().getCamera().position.x = cameraLeftBoundary;
        } else if (bodyPosition.x > cameraRightBoundary) {
            WorldScreen.getInstance().getGameViewport().getCamera().position.x = cameraRightBoundary;
        }

        if (bodyPosition.y >= cameraBottomBoundary && bodyPosition.y <= cameraTopBoundary) {
            WorldScreen.getInstance().getGameViewport().getCamera().position.y = body.getPosition().y;
        } else if (bodyPosition.y > cameraTopBoundary) {
            WorldScreen.getInstance().getGameViewport().getCamera().position.y = cameraTopBoundary;
        } else if (bodyPosition.y < cameraBottomBoundary) {
            WorldScreen.getInstance().getGameViewport().getCamera().position.y = cameraBottomBoundary;
        }
    }
}
