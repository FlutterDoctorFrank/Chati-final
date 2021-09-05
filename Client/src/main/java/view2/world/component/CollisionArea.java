package view2.world.component;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import view2.Chati;
import view2.world.WorldCamera;
import view2.world.WorldScreen;

public class CollisionArea {

    public CollisionArea(Rectangle rectangle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / WorldCamera.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / WorldCamera.PPM);
        Body body = Chati.CHATI.getWorldScreen().getWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = WorldScreen.COLLISION_AREA_BIT;
        fixtureDef.filter.maskBits = WorldScreen.INTERN_USER_BIT;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rectangle.getWidth() / 2) / WorldCamera.PPM,
                (rectangle.getHeight() / 2) / WorldCamera.PPM);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }
}
