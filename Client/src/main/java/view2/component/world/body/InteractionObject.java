package view2.component.world.body;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import view2.Chati;
import view2.component.world.WorldScreen;

public class InteractionObject {

    private final Rectangle rectangle;

    public InteractionObject(Rectangle rectangle) {
        this.rectangle = rectangle;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / WorldScreen.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / WorldScreen.PPM);
        Body body = Chati.CHATI.getWorldScreen().getWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rectangle.getWidth() / 2) / WorldScreen.PPM, (rectangle.getHeight() / 2) / WorldScreen.PPM);
        fixtureDef.filter.categoryBits = WorldScreen.OBJECT_BIT;
        fixtureDef.filter.maskBits = WorldScreen.INTERN_USER_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        createInteractiveArea();
    }

    private void createInteractiveArea() {
        int tileWidth = ((TiledMapTileLayer) Chati.CHATI.getWorldScreen().getTiledMap().getLayers().get(0)).getTileWidth();

        BodyDef areaBodyDef = new BodyDef();
        areaBodyDef.type = BodyDef.BodyType.StaticBody;
        areaBodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / WorldScreen.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / WorldScreen.PPM);
        Body areaBody = Chati.CHATI.getWorldScreen().getWorld().createBody(areaBodyDef);
        areaBody.setUserData(BodyType.INTERACTION_AREA);

        FixtureDef areaFixtureDef = new FixtureDef();
        PolygonShape areaShape = new PolygonShape();
        areaShape.setAsBox((rectangle.getWidth() / 2 + tileWidth) / WorldScreen.PPM ,
                (rectangle.getHeight() / 2 + tileWidth) / WorldScreen.PPM);
        areaFixtureDef.shape = areaShape;
        areaFixtureDef.isSensor = true;
        areaBody.createFixture(areaFixtureDef);
    }
}
