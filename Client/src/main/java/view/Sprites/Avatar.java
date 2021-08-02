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

    public Avatar(World world) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;
        defineAvatar();
    }

    private void defineAvatar() {
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
