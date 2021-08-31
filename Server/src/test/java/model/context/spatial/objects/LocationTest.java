package model.context.spatial.objects;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.context.spatial.Direction;
import model.context.spatial.Location;
import model.context.spatial.ContextMap;
import model.context.spatial.World;
import org.junit.*;

public class LocationTest {

    private Location test_location;
    private World test_world;

    @BeforeClass
    public static void openGdx() {
        new HeadlessApplication(new ApplicationAdapter() {
            @Override
            public void create() {
                Gdx.gl = new MockGL20();
            }
        });
    }

    @AfterClass
    public static void closeGdx() {
        Gdx.app.exit();
    }

    @Before
    public void setUp() {
        this.test_world = new World("test_world", ContextMap.PUBLIC_ROOM_MAP);
        this.test_location = new Location(test_world.getPublicRoom(), Direction.UP, 1, 1);
    }

    @Test
    public void distanceTest() {
        Location second_location = new Location(test_world.getPublicRoom(), Direction.UP, 4.0f, 5.0f);

        Assert.assertEquals(4.0f, second_location.getPosX(), 0.0f);
        Assert.assertEquals(5.0f, second_location.getPosY(), 0.0f);

        Assert.assertEquals(test_world.getPublicRoom(), this.test_location.getRoom());

        Assert.assertEquals(5, this.test_location.distance(second_location));
    }
}
