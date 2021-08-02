package controller.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import model.Context.Global.IGlobalContextController;
import model.User.IUserManagerController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Screens.ViewControllerInterface;
import java.io.IOException;

/**
 * Verwaltet die Verbindung zum Server und hält die Referenzen auf das Model und die View.
 */
public class ClientNetworkManager extends NetworkManager<Client> {

    private ViewControllerInterface view;
    private final IGlobalContextController global;
    private final IUserManagerController userManager;

    private ServerConnection connection;

    public ClientNetworkManager(/*@NotNull final ViewControllerInterface view,*/
                                @NotNull final IGlobalContextController global,
                                @NotNull final IUserManagerController userManager) {
        super(new Client());

        this.global = global;
        this.userManager = userManager;
        //this.view = view;
    }

    public void setView(ViewControllerInterface view) {
        this.view = view;
    }

    public ViewControllerInterface getView() {
        return view;
    }

    public @Nullable ServerConnection getConnection() {
        return this.connection;
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
        }
    }

    /*
     * Gibt die Instanz auf die View zurück.
     * @return die ViewControllerInterface-Instanz
     */

    /*
    public @NotNull ViewControllerInterface getView() {
        return view;
    }
     */

    /**
     * Gibt die Instanz auf den globalen Kontext zurück.
     * @return die IGlobalContextController-Instanz
     */
    public @NotNull IGlobalContextController getGlobal() {
        return global;
    }

    /**
     * Gibt die Instanz des User-Managers zurück.
     * @return die IUserManagerController-Instanz.
     */
    public @NotNull IUserManagerController getUserManager() {
        return userManager;
    }

    @Override
    public void start() {
        try {
            this.endPoint.start();
            this.endPoint.connect(5000, HOST_IP, HOST_TCP_PORT, HOST_UDP_PORT);

            System.out.println("Connected to Server.");
        } catch (IOException ex) {
            System.out.println("Failed to connect to server");
            ex.fillInStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
