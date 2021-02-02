import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Implements the <code>Singleton</code>-Pattern and object pooling. Handles connections to the
 * database by queuing them. Also checks the database driver upon initialization and tries to
 * refresh connections once if they do not work when returned.
 *
 * <p>Adapted from the original ConnectionPool of Code Defenders.
 */
public final class ConnectionPool {

    private List<Connection> connections;
    private Queue<Connection> availableConnections;
    private Logger logger = Logger.getLogger("ConnectionPool");

    /** Number of constantly open connections. */
    private int nbConnections;

    public ConnectionPool(Integer in) {
        Connection newConnection;
        nbConnections = 5;
        availableConnections = new LinkedList<>();
        connections = new ArrayList<>();

        for (int i = 0; i < nbConnections; i++) {
            newConnection = refresh();
            connections.add(newConnection);
            availableConnections.add(connections.get(i));
        }
        logger.info("ConnectionPool initialized");
    }

    public void closeDBConnections() {
        logger.info("Closing ConnectionPool connections...");
        List<Connection> closedConnections = new ArrayList<>();
        try {
            for (Connection connection : connections) {
                connection.close();
                availableConnections.remove(connection);
                closedConnections.add(connection);
            }
        } catch (SQLException e) {
            logger.warning("SQL exception while closing connections.");
            throw new StorageException();
        } finally {
            connections.removeAll(closedConnections);
        }
        logger.info("Closed ConnectionPool connections successfully.");
    }



    /**
     * Returns an unused connection from the queue. Tries to refresh connections once they do not
     * work.
     *
     * @return a free connection or null of none is available.
     * @throws NoMoreConnectionsException if there are no free connections
     */
    public synchronized Connection getDBConnection() throws NoMoreConnectionsException {
        Connection returnConn;
        if (availableConnections.peek() != null) {
            returnConn = availableConnections.poll();
        } else {
            logger.warning("Threw NoMoreConnectionsException.");
            throw new NoMoreConnectionsException();
        }

        try {
            returnConn.createStatement().execute("SELECT 1;");
        } catch (SQLException e) {
            logger.info("Refreshing SQL connection: " + returnConn + ".");
            logger.warning("SQL exception while refreshing connection: " + returnConn + ".");
            throw new StorageException();
        }
        return returnConn;
    }

    private Connection refresh() {
        return DatabaseConnection.getConnection();
    }

    /**
     * Releases a previously retrieved connection such that it is usable for other objects again.
     *
     * @param connection to be released
     */
    public synchronized void releaseDBConnection(Connection connection) {
        if (connection != null) {
            availableConnections.add(connection);
        }
    }

    public int getNbConnections() {
        return nbConnections;
    }

    public class NoMoreConnectionsException extends Exception {
        NoMoreConnectionsException() {}
    }

    public static class StorageException extends RuntimeException {
        private static final long serialVersionUID = -6071779646574124576L;
    }

    public static class DatabaseConnection {
        static Connection getConnection() {
            // return null as we do not need a real Connection object but only mocks that should be
            // configured and injected in the tests anyway!
            return null;
        }
    }
}