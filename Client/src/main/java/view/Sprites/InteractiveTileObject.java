package view.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class InteractiveTileObject {
    private World world;
    private Body body;
    private BodyDef bdef;
    private FixtureDef fdef;
    private PolygonShape shape;

    public InteractiveTileObject (World world, Rectangle rect) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

        body = world.createBody(bdef);

        shape = new PolygonShape();
        shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
        fdef.shape = shape;
        body.createFixture(fdef);

    }

    private void createInteractiveArea(World world, Rectangle rectangle) {
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(rectangle.getWidth() / 2, rectangle.getHeight() / 2);

        body = world.createBody(bdef);

        shape = new PolygonShape();
        shape.setAsBox(rectangle.getWidth() / 2, rectangle.getHeight() / 2);
        fdef.shape = shape;
        fdef.isSensor = true;
        body.createFixture(fdef);
    }

}
