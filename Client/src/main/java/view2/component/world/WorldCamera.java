package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import view2.Chati;

public class WorldCamera extends OrthographicCamera {

    public static final float PPM = 10;

    private static final float DEFAULT_ZOOM = 0.6f;
    private static final float MIN_ZOOM = 0.4f;
    private static final float MAX_ZOOM = 0.8f;
    private static final float ZOOM_STEP = 0.01f;

    public WorldCamera() {
        this.zoom = DEFAULT_ZOOM;
    }

    @Override
    public void update() {
        WorldScreen worldScreen = Chati.CHATI.getWorldScreen();
        if (worldScreen.getTiledMap() != null) {
            TiledMapTileLayer layer = (TiledMapTileLayer) worldScreen.getTiledMap().getLayers().get(0);
            float mapWidth = layer.getWidth() * layer.getTileWidth();
            float mapHeight = layer.getHeight() * layer.getTileHeight();

            float cameraTopBoundary = (mapHeight - Gdx.graphics.getHeight() * zoom / 2f) / PPM;
            float cameraLeftBoundary = (Gdx.graphics.getWidth() / 2f * zoom) / PPM;
            float cameraBottomBoundary = (Gdx.graphics.getHeight() * zoom / 2f) / PPM;
            float cameraRightBoundary = (mapWidth - Gdx.graphics.getWidth() * zoom / 2f) / PPM;

            Vector2 internUserPosition = worldScreen.getInternUserAvatar().getPosition();

            if (mapWidth < Gdx.graphics.getWidth() * zoom) {
                position.x = (cameraLeftBoundary + cameraRightBoundary) / 2f;
            } else if (internUserPosition.x >= cameraLeftBoundary && internUserPosition.x <= cameraRightBoundary) {
                position.x = internUserPosition.x;
            } else if (internUserPosition.x < cameraLeftBoundary) {
                position.x = cameraLeftBoundary;
            } else if (internUserPosition.x > cameraRightBoundary) {
                position.x = cameraRightBoundary;
            }

            if (mapHeight < Gdx.graphics.getHeight() * zoom) {
                position.y = (cameraTopBoundary + cameraBottomBoundary) / 2f;
            } else if (internUserPosition.y >= cameraBottomBoundary && internUserPosition.y <= cameraTopBoundary) {
                position.y = internUserPosition.y;
            } else if (internUserPosition.y > cameraTopBoundary) {
                position.y = cameraTopBoundary;
            } else if (internUserPosition.y < cameraBottomBoundary) {
                position.y = cameraBottomBoundary;
            }
        }

        super.update();
    }

    public void zoom(boolean in) {
        if (in && zoom <= MAX_ZOOM) {
            zoom += ZOOM_STEP;
        } else if (!in && zoom >= MIN_ZOOM) {
            zoom -= ZOOM_STEP;
        }
    }
}
