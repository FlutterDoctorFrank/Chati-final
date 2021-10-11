package view.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import view.Chati;

/**
 * Eine Klasse, welche die Kamera innerhalb einer Welt repräsentiert.
 */
public class WorldCamera extends OrthographicCamera {

    private static final float UNIT_SCALE = 0.1f;

    private static final float DEFAULT_ZOOM = 0.6f;
    private static final float MIN_ZOOM = 0.4f;
    private static final float MAX_ZOOM = 0.8f;
    private static final float ZOOM_STEP = 0.01f;

    /**
     * Erzeugt eine neue Instanz einer WorldCamera.
     */
    public WorldCamera() {
        this.zoom = DEFAULT_ZOOM;
    }

    @Override
    public void update() {
        WorldScreen worldScreen = Chati.CHATI.getWorldScreen();
        if (worldScreen.getTiledMap() != null && worldScreen.getInternUserAvatar() != null) {
            TiledMapTileLayer layer = (TiledMapTileLayer) worldScreen.getTiledMap().getLayers().get(0);
            float mapWidth = layer.getWidth() * layer.getTileWidth();
            float mapHeight = layer.getHeight() * layer.getTileHeight();

            float cameraTopBoundary = scaleToUnit(mapHeight - Gdx.graphics.getHeight() * zoom / 2f);
            float cameraLeftBoundary = scaleToUnit(Gdx.graphics.getWidth() / 2f * zoom);
            float cameraBottomBoundary = scaleToUnit(Gdx.graphics.getHeight() * zoom / 2f);
            float cameraRightBoundary = scaleToUnit(mapWidth - Gdx.graphics.getWidth() * zoom / 2f);

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

    /**
     * Zoomt innerhalb festgelegter Grenzen um einen festgelegten Schritt herein oder heraus.
     * @param in Falls true, wird hereingezoomt, sonst herausgezoomt.
     */
    public void zoom(final boolean in) {
        if (Chati.CHATI.getWorldScreen().getWorldInputProcessor().isZoomPressed()) {
            if (in && zoom <= MAX_ZOOM) {
                zoom += ZOOM_STEP;
            } else if (!in && zoom >= MIN_ZOOM) {
                zoom -= ZOOM_STEP;
            }
        }
    }

    /**
     * Gibt zu gegebener Größe in Pixel die entsprechende Größe in den innerhalb der Welt verwendeten Einheit zurück.
     * @param value Größe in Pixel.
     * @return Größe in der verwendeten Einheit.
     */
    public static float scaleToUnit(final float value) {
        return value * UNIT_SCALE;
    }

    /**
     * Gibt zu gegebener Größe in der innerhalb der Welt verwendeten Einheit die entsprechende Größe in Pixel zurück.
     * @param value Größe in der verwendeten Einheit.
     * @return Größe in Pixel.
     */
    public static float scaleFromUnit(final float value) {
        return value / UNIT_SCALE;
    }
}
