package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnessioneDatabase {

    private static Connection connection = null;

    private static final Logger LOGGER = Logger.getLogger(ConnessioneDatabase.class.getName());
    private static final String URL = "jdbc:postgresql://localhost:5432/ospedale27";
    private static final String USER = "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private ConnessioneDatabase() {
    }

    public static Connection getInstance() {
        try {
            if (connection == null || connection.isClosed()) {

                if (PASSWORD == null) {
                    LOGGER.severe("La password del database non è stata trovata. Imposta la variabile d'ambiente 'DB_PASSWORD'.");
                    return null;
                }

                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                LOGGER.info("Connessione al database PostgreSQL stabilita con successo!");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore critico durante la connessione al database", e);
        }

        return connection;
    }
}