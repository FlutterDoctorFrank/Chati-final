package view2.component.world.body;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import view2.Chati;
import view2.component.world.WorldCamera;

public class InteractionObject {

    public InteractionObject(Rectangle rectangle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / WorldCamera.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / WorldCamera.PPM);
        Body body = Chati.CHATI.getWorldScreen().getWorld().createBody(bodyDef);
        body.setUserData(BodyType.INTERACTION_AREA);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rectangle.getWidth() / 2) / WorldCamera.PPM, (rectangle.getHeight() / 2) / WorldCamera.PPM);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
    }

    private void createInteractiveArea() {
        /*
        int tileWidth = ((TiledMapTileLayer) Chati.CHATI.getWorldScreen().getTiledMap().getLayers().get(0)).getTileWidth();

        BodyDef areaBodyDef = new BodyDef();
        areaBodyDef.type = BodyDef.BodyType.StaticBody;
        areaBodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / WorldCamera.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / WorldCamera.PPM);
        Body areaBody = Chati.CHATI.getWorldScreen().getWorld().createBody(areaBodyDef);
        areaBody.setUserData(BodyType.INTERACTION_AREA);

        FixtureDef areaFixtureDef = new FixtureDef();
        PolygonShape areaShape = new PolygonShape();
        areaShape.setAsBox((rectangle.getWidth() / 2 + tileWidth) / WorldCamera.PPM ,
                (rectangle.getHeight() / 2 + tileWidth) / WorldCamera.PPM);
        areaFixtureDef.shape = areaShape;
        areaFixtureDef.isSensor = true;
        areaBody.createFixture(areaFixtureDef);

         */
    }
}
