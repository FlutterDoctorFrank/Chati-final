package controller.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import model.user.IUserManagerController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Screens.ViewControllerInterface;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Verwaltet die Verbindung zum Server und hält die Referenzen auf das Model und die View.
 */
public class ClientNetworkManager extends NetworkManager<Client> {

    private static final Logger LOGGER = Logger.getLogger("Chati-Network");

    private final IUserManagerController userManager;
    private ViewControllerInterface view;

    private ServerConnection connection;

    public ClientNetworkManager(/*@NotNull final ViewControllerInterface view,*/
                                @NotNull final IUserManagerController userManager) {
        super(new Client());

        this.userManager = userManager;
        //this.view = view;
    }

    @Override
    public void connected(@NotNull final Connection connection) {
        this.connection = new ServerConnection(this);
    }

    @Override
    public void disconnected(@NotNull final Connection connection) {
        if (connection.equals(this.endPoint)) {
            this.endPoint.removeListener(this.connection);
            this.connection = null;
            this.userManager.logout();
            this.view.logout();
        }
    }

    /**
     * Gibt die Instanz auf die View zurück.
     * @return die ViewControllerInterface-Instanz
     */
    public @NotNull ViewControllerInterface getView() {
        return this.view;
    }

    public void setView(ViewControllerInterface view) {
        this.view = view;
    }

    /**
     * Gibt die Instanz des User-Managers zurück.
     * @return die IUserManagerController-Instanz.
     */
    public @NotNull IUserManagerController getUserManager() {
        return userManager;
    }

    public @Nullable ServerConnection getConnection() {
        return this.connection;
    }

    @Override
    public void start() {
        this.endPoint.start();
        this.active = true;

        while (this.active && !this.endPoint.isConnected()) {
            try {
                this.endPoint.connect(60000, HOST_IP, HOST_TCP_PORT, HOST_UDP_PORT);

                LOGGER.info(String.format("Connect to server over ip %s and ports: %d, %d (TCP, UDP)",
                        HOST_IP, HOST_TCP_PORT, HOST_UDP_PORT));
            } catch (IOException ex) {
                LOGGER.warning(String.format("Failed to connect to server with ip %s and ports: %d, %d (TCP, UDP)",
                        HOST_IP, HOST_TCP_PORT, HOST_UDP_PORT));
            }
        }
    }

    @Override
    public void stop() {
        this.active = false;
        this.endPoint.stop();
    }
}
