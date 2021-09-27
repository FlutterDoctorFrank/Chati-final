package controller.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import model.user.IUserManagerController;
import org.jetbrains.annotations.NotNull;
import view2.ViewControllerInterface;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Verwaltet die Verbindung zum Server und hält die Referenzen auf das Model und die View.
 */
public class ClientNetworkManager extends NetworkManager<Client> implements Runnable {

    private static final int BUFFER_SIZE = (int) Math.pow(2, 24);

    private static final Logger LOGGER = Logger.getLogger("chati.network");

    private final IUserManagerController userManager;
    private ViewControllerInterface view;

    private ServerConnection connection;
    private Thread networkThread;

    public ClientNetworkManager(@NotNull final IUserManagerController userManager) {
        super(new Client(BUFFER_SIZE, BUFFER_SIZE));

        this.userManager = userManager;
    }

    @Override
    public void connected(@NotNull final Connection connection) {
        if (this.connection == null) {
            LOGGER.info(String.format("Connected to server on ip %s over tcp port: %d", this.host, this.tcp));

            this.connection = new ServerConnection(this);
            this.endPoint.addListener(this.connection);
            this.view.setSender(this.connection);
        }
    }

    @Override
    public void disconnected(@NotNull final Connection connection) {
        if (this.connection != null) {
            LOGGER.info(String.format("Disconnected from server on ip: %s", this.host));

            this.view.setSender(null);
            this.endPoint.removeListener(this.connection);
            this.connection = null;
        }
    }

    /**
     * Gibt die Instanz des User-Managers zurück.
     * @return die IUserManagerController-Instanz.
     */
    public @NotNull IUserManagerController getUserManager() {
        return this.userManager;
    }

    /**
     * Gibt die Instanz auf die View zurück.
     * @return die ViewControllerInterface-Instanz.
     */
    public @NotNull ViewControllerInterface getView() {
        if (this.view == null) {
            throw new IllegalStateException("View was called before ist was set");
        }

        return this.view;
    }

    /**
     * Setzt die Instanz auf die View.
     * @param view die zu setzende ViewControllerInterface-Instanz.
     */
    public void setView(@NotNull final ViewControllerInterface view) {
        if (this.view != null) {
            throw new IllegalStateException("View was already set");
        }

        this.view = view;
    }

    @Override
    public void run() {
        while (this.active) {
            try {
                if (this.endPoint.isConnected()) {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    continue;
                }

                this.endPoint.connect(60000, this.host, this.tcp);
            } catch (IOException ex) {
                LOGGER.warning(String.format("Failed to connect to server on ip: %s", this.host));
            } catch (InterruptedException ignored) {

            }
        }
    }

    @Override
    public synchronized void start() {
        if (this.active) {
            try {
                this.active = false;
                this.endPoint.stop();
                this.networkThread.join(5000);
            } catch (InterruptedException ignored) {

            }
        }

        this.endPoint.start();
        this.active = true;
        this.networkThread = new Thread(this, "Network-Thread");
        this.networkThread.setDaemon(true);
        this.networkThread.start();
    }

    @Override
    public synchronized void stop() {
        if (!this.active) {
            return;
        }

        this.active = false;
        this.endPoint.stop();
    }
}
