package controller.network;

import com.esotericsoftware.kryonet.Connection;
import controller.network.mock.MockHandler;
import model.user.IUserManagerController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import util.RandomTest;
import view2.ViewControllerInterface;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ClientNetworkManagerTest extends RandomTest {

    private ClientNetworkManager network;
    private MockHandler handler;

    public ClientNetworkManagerTest() {

    }

    @Before
    public void setup() {
        final IUserManagerController manager = Mockito.mock(IUserManagerController.class);
        final ViewControllerInterface view = Mockito.mock(ViewControllerInterface.class);

        this.network = new ClientNetworkManager(manager);
        this.network.setView(view);
        this.handler = new MockHandler("chati.network");

        Assert.assertEquals(manager, this.network.getUserManager());
        Assert.assertEquals(view, this.network.getView());
    }

    @Test(expected = IllegalStateException.class)
    public void alreadyViewTest() {
        this.network.setView(this.network.getView());
    }

    @Test(expected = IllegalStateException.class)
    public void missingViewTest() {
        new ClientNetworkManager(this.network.getUserManager()).getView();
    }

    @Test
    public void connectionTest() {
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.when(connection.getID()).thenReturn(randomInt(100));

        this.handler.reset();
        this.network.connected(connection);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.INFO, "Connected to server"));

        this.handler.reset();
        this.network.disconnected(connection);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.INFO, "Disconnected from server"));
    }

    @Test
    public void startStopTest() {
        Assert.assertFalse(this.network.isActive());

        this.handler.reset();
        this.network.start();

        try {
            TimeUnit.MILLISECONDS.sleep(5000);

            Assert.assertTrue(this.network.isActive());
            Assert.assertTrue(this.handler.logged());
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }

        this.handler.reset();
        this.network.stop();

        Assert.assertFalse(this.network.isActive());

        this.handler.reset();
        this.network.start();

        Assert.assertFalse(this.handler.logged());

        this.network.start();
        this.network.stop();
    }
}
