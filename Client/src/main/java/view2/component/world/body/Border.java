package view2.component.world.body;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import view2.component.world.WorldScreen;

public class Border {

    public Border(Rectangle rectangle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / WorldScreen.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / WorldScreen.PPM);
        Body body = WorldScreen.getInstance().getWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rectangle.getWidth() / 2) / WorldScreen.PPM,
                (rectangle.getHeight() / 2) / WorldScreen.PPM);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }
}
