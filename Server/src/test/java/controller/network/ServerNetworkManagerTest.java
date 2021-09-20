package controller.network;

import com.esotericsoftware.kryonet.Connection;
import controller.network.mock.MockHandler;
import model.context.global.IGlobalContext;
import model.user.account.IUserAccountManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import util.RandomTest;
import java.util.logging.Level;

public class ServerNetworkManagerTest extends RandomTest {

    private ServerNetworkManager network;
    private MockHandler handler;

    @Before
    public void setup() {
        final IUserAccountManager manager = Mockito.mock(IUserAccountManager.class);
        final IGlobalContext global = Mockito.mock(IGlobalContext.class);

        this.network = new ServerNetworkManager(manager, global);
        this.handler = new MockHandler("chati.network");

        Assert.assertEquals(manager, this.network.getAccountManager());
        Assert.assertEquals(global, this.network.getGlobal());
    }

    @Test(expected = IllegalStateException.class)
    public void illegalPortsTest() {
        this.network.setPorts(65000, 65000);
        this.network.start();
    }

    @Test
    public void connectionTest() {
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.when(connection.getID()).thenReturn(randomInt(100));

        this.handler.reset();
        this.network.connected(connection);

        Assert.assertEquals(1, this.network.getConnections().size());

        this.handler.reset();
        this.network.connected(connection);

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.WARNING, "Previous connection"));
        Assert.assertEquals(1, this.network.getConnections().size());

        this.handler.reset();
        this.network.disconnected(connection);

        Assert.assertEquals(0, this.network.getConnections().size());
    }

    @Test
    public void startStopTest() {
        Assert.assertFalse(this.network.isActive());

        this.handler.reset();
        this.network.start();

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.INFO, "Hosted Server"));
        Assert.assertTrue(this.network.isActive());

        this.handler.reset();
        this.network.stop();

        Assert.assertTrue(this.handler.logged());
        Assert.assertTrue(this.handler.logged(Level.INFO, "Closed Server"));
        Assert.assertFalse(this.network.isActive());
    }
}
