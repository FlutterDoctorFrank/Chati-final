package view.Sprites;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import model.user.IUserView;
import org.lwjgl.system.CallbackI;
import view.Chati;

import java.util.UUID;

public class Avatar {
    private World world;
    private Body body;
    private BodyDef bdef;
    private FixtureDef fdef;
    private UUID user;

    public Avatar(World world) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;

        bdef = new BodyDef();
        bdef.position.set(40, 40);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);
        fdef.shape = shape;
        body.createFixture(fdef);
    }

    public Avatar(World world, IUserView userView) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;
        this.user = userView.getUserId();

        bdef = new BodyDef();
        bdef.position.set(userView.getCurrentLocation().getPosX(), userView.getCurrentLocation().getPosY());
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

    public BodyDef getBdef() {
        return bdef;
    }

    public UUID getUser() {
        return user;
    }
}
