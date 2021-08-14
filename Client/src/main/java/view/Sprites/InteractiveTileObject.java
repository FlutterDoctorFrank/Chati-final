package view.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import view.Chati;

public class InteractiveTileObject {
    private World world;
    private Body body;
    private BodyDef bdef;
    private FixtureDef fdef;
    private PolygonShape shape;

    public InteractiveTileObject (World world, int tileWidth, Rectangle rect) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;

        //Interactive Object Def.
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.getX() + rect.getWidth() / 2) / Chati.PPM, (rect.getY() + rect.getHeight() / 2) / Chati.PPM);

        body = world.createBody(bdef);

        shape = new PolygonShape();
        shape.setAsBox((rect.getWidth() / 2) / Chati.PPM, (rect.getHeight() / 2) / Chati.PPM);
        fdef.filter.categoryBits = Chati.OBJECT_BIT;
        fdef.filter.maskBits = Chati.AVATAR_BIT;
        fdef.shape = shape;
        body.createFixture(fdef);

        //Interaction Area Def
        new InteractiveArea(world, tileWidth, rect);

    }

    private class InteractiveArea {
        private Body areaBody;
        private BodyDef areaBodyDef;
        private FixtureDef areaFixture;
        private PolygonShape areaShape;

        public InteractiveArea(World world, int tileWidth, Rectangle rect) {
            areaBodyDef = new BodyDef();
            areaFixture = new FixtureDef();
            areaBodyDef.type = BodyDef.BodyType.StaticBody;
            areaBodyDef.position.set((rect.getX() + rect.getWidth() / 2) / Chati.PPM, (rect.getY() + rect.getHeight() / 2) / Chati.PPM);

            areaBody = world.createBody(areaBodyDef);

            areaShape = new PolygonShape();
            areaShape.setAsBox((rect.getWidth() / 2 + tileWidth) / Chati.PPM , (rect.getHeight() / 2 + tileWidth) / Chati.PPM);
            areaFixture.shape = areaShape;
            areaFixture.isSensor = true;
            areaBody.createFixture(areaFixture).setUserData("interactive-area");
        }
    }
}
