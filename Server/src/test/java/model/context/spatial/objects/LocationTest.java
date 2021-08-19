package model.context.spatial.objects;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backend.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import model.context.global.GlobalContext;
import model.context.spatial.Location;
import model.context.spatial.SpatialMap;
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
        this.test_world = new World("test_world", SpatialMap.MAP);
        this.test_location = new Location(test_world.getPublicRoom(), 1, 1);
    }

    @Test
    public void distanceTest() {
        Location second_location = new Location(test_world.getPublicRoom(), 4, 5);

        Assert.assertEquals(4, second_location.getPosX());
        Assert.assertEquals(5, second_location.getPosY());

        Assert.assertEquals(test_world.getPublicRoom(), this.test_location.getRoom());

        Assert.assertEquals(5, this.test_location.distance(second_location));
    }
}