package controller.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class NetworkManagerTest extends RandomTest {

    private NetworkManager<EndPoint> network;

    @Before
    public void setup() {
        final EndPoint point = Mockito.mock(EndPoint.class);

        Mockito.when(point.getKryo()).thenReturn(Mockito.mock(Kryo.class));

        this.network = new NetworkManager<>(point) {
            @Override
            public void start() {
                this.active = true;
            }

            @Override
            public void stop() {
                this.active = false;
            }
        };

        Assert.assertEquals(point, this.network.getEndPoint());
    }

    @Test
    public void startStopTest() {
        Assert.assertFalse(this.network.isActive());

        this.network.start();

        Assert.assertTrue(this.network.isActive());

        this.network.stop();

        Assert.assertFalse(this.network.isActive());
    }

    @Test
    public void setHostTest() {
        final String host = randomString();

        this.network.setHost(host);

        Assert.assertEquals(host, this.network.host);
    }

    @Test
    public void setTcpPortTest() {
        final int tcp = randomInt(65536);
        final int udp = this.network.udp;

        this.network.setPorts(tcp, -1);

        Assert.assertEquals(tcp, this.network.tcp);
        Assert.assertEquals(udp, this.network.udp);
    }

    @Test
    public void setUdpPortTest() {
        final int tcp = this.network.tcp;
        final int udp = randomInt(65536);

        this.network.setPorts(-1, udp);

        Assert.assertEquals(tcp, this.network.tcp);
        Assert.assertEquals(udp, this.network.udp);
    }
}
