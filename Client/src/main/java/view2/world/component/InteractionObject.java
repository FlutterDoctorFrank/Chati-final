package view2.world.component;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import view2.Chati;
import view2.world.WorldCamera;

public class InteractionObject {

    public InteractionObject(Rectangle rectangle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(WorldCamera.scaleToUnit((rectangle.getX() + rectangle.getWidth() / 2)),
                WorldCamera.scaleToUnit((rectangle.getY() + rectangle.getHeight() / 2)));
        Body body = Chati.CHATI.getWorldScreen().getWorld().createBody(bodyDef);
        body.setUserData(ContactType.INTERACTION_AREA);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WorldCamera.scaleToUnit((rectangle.getWidth() / 2)),
                WorldCamera.scaleToUnit((rectangle.getHeight() / 2)));
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
    }
}
