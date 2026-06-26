package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Classe di gestione della connessione al database PostgreSQL (ospitato su Supabase).
 * Implementa il pattern Singleton per garantire la presenza di un'unica connessione attiva
 * globale in tutto il ciclo di vita dell'applicazione, ottimizzando il riuso delle risorse.
 */
public class ConnessioneDatabase {

    private static Connection connection = null;

    private static final Logger LOGGER = Logger.getLogger(ConnessioneDatabase.class.getName());
    private static final String URL = "jdbc:postgresql://aws-0-eu-west-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String USER = "postgres.nyvvdoqwgsxhjwqtgafi";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    /**
     * Costruttore privato per impedire l'istanziazione diretta della classe dall'esterno.
     * Garantisce l'integrità del pattern Singleton.
     */
    private ConnessioneDatabase() {
    }

    /**
     * Restituisce l'istanza corrente della connessione al database.
     * Se la connessione non esiste, è stata chiusa o non è più valida (timeout di 3 secondi),
     * provvede a inizializzarne una nuova leggendo la password dalle variabili d'ambiente.
     *
     * @return l'oggetto {@link Connection} attivo e pronto all'uso, oppure null se la variabile
     * d'ambiente 'DB_PASSWORD' manca o si verifica un errore SQL critico
     */
    public static Connection getInstance() {
        try {
            if (connection == null || connection.isClosed()|| !connection.isValid(3)) {

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

    /**
     * Esegue più operazioni sul database come un'unica transazione atomica: se una qualsiasi
     * di esse lancia un'eccezione, tutte le modifiche fatte finora nella transazione vengono
     * annullate (rollback) e l'eccezione viene rilanciata al chiamante.
     * La connessione condivisa di questa classe lavora normalmente in autocommit (ogni singola
     * query si conferma da sola). Questo metodo disattiva temporaneamente l'autocommit solo per
     * la durata dell'operazione passata, e lo ripristina sempre alla fine — sia in caso di
     * successo che di errore — così il resto dell'applicazione continua a funzionare come prima.
     */
    public static void eseguiInTransazione(Runnable operazioni) {
        Connection conn = getInstance();
        if (conn == null) {
            throw new IllegalStateException("Connessione al database non disponibile: impossibile avviare la transazione.");
        }

        boolean autoCommitOriginale;
        try {
            autoCommitOriginale = conn.getAutoCommit();
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'avvio della transazione", e);
        }

        try {
            operazioni.run();
            conn.commit();
        } catch (RuntimeException | Error e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Errore durante il rollback della transazione", rollbackEx);
            }
            throw e;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Errore durante il rollback della transazione", rollbackEx);
            }
            throw new RuntimeException("Errore durante il commit della transazione", e);
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginale);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Errore durante il ripristino dell'autocommit dopo la transazione", e);
            }
        }
    }
}