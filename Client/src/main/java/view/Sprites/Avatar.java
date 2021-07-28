package view.Sprites;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.*;
import org.lwjgl.system.CallbackI;
import view.Chati;

public class Avatar {
    private World world;
    private Body body;
    private BodyDef bdef;
    private FixtureDef fdef;

    public Avatar(World world, TiledMap map) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;
        defineAvatar(map);
    }

    private void defineAvatar(TiledMap map) {
        MapProperties prop = map.getProperties();
        int mapLeft = 0;
        int mapBottom = 0;
        float mapTop = prop.get("height", Integer.class); // dimension in tiles
        float mapRight = prop.get("width", Integer.class); // dimension in tiles
        float tilePixelWidth = prop.get("tilewidth", Integer.class);
        float tilePixelHeight = prop.get("tileheight", Integer.class);
        float mapPixelsTop = mapTop * tilePixelHeight;
        float mapPixelRight = mapRight * tilePixelWidth;

        float cameraHalfWidth = Chati.V_WIDTH / 2 ;
        float cameraHalfHeight = Chati.V_HEIGHT / 2;

        float cameraLeftBoundary = (mapLeft + cameraHalfWidth) / Chati.PPM;
        float cameraBottomBoundary = (mapBottom + cameraHalfHeight) / Chati.PPM;
        float cameraRightBoundary = (mapPixelRight - cameraHalfWidth) / Chati.PPM;
        float cameraTopBoundary = (mapPixelsTop - cameraHalfHeight) / Chati.PPM;

        //texture = new TextureAtlas("avatar/avatar.pack");

        bdef = new BodyDef();
        bdef.position.set(20, 21);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);
        fdef.shape = shape;
        body.createFixture(fdef);
    }

    public Body getBody() {
        return body;
    }
}
