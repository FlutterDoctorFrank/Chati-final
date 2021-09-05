package view2.world.component;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import view2.Chati;
import view2.world.WorldCamera;

public class InteractionObject {

    public InteractionObject(Rectangle rectangle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / WorldCamera.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / WorldCamera.PPM);
        Body body = Chati.CHATI.getWorldScreen().getWorld().createBody(bodyDef);
        body.setUserData(ContactType.INTERACTION_AREA);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rectangle.getWidth() / 2) / WorldCamera.PPM, (rectangle.getHeight() / 2) / WorldCamera.PPM);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
    }
}
