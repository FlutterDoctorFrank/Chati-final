package view.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import model.user.IUserView;
import org.lwjgl.system.CallbackI;
import view.Chati;

import java.util.UUID;

public class Avatar extends Sprite {
    private World world;
    private Body body;
    private BodyDef bdef;
    private FixtureDef fdef;
    private UUID user;
    private TextureRegion texture;

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
        shape.setAsBox(0.5f, 0.5f);
        fdef.shape = shape;
        body.createFixture(fdef);

        texture = new TextureRegion(new Texture("skins/Adam_32x32.png"), 0, 0, 32, 64);
        setBounds(0,0,32 / Chati.PPM,32 / Chati.PPM);
        setRegion(texture);
    }

    public Avatar(World world, String avatarPath, UUID id, int posX, int posY) {
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.world = world;
        this.user = id;

        bdef = new BodyDef();
        bdef.position.set(posX, posY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);
        fdef.shape = shape;
        body.createFixture(fdef);

        texture = new TextureRegion(new Texture("skins/Adam_32x32.png"), 0, 0, 32, 64);
        setBounds(0,0,32 / Chati.PPM,32 / Chati.PPM);
        setRegion(texture);
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
