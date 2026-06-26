package implementazioneDao;

import dao.TurnoLavorativoDAO;
import database_connection.ConnessioneDatabase;
import model.TurnoLavorativo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementazione Postgres dell'interfaccia TurnoLavorativoDAO.
 * Sovraintende alla persistenza e all'estrazione del calendario dei turni lavorativi settimanali dei medici.
 */
public class TurnoLavorativoPostgresDao implements TurnoLavorativoDAO {
    /**
     * Estrae l'intera pianificazione dei turni di servizio legati all'account di un medico specifico.
     * <p>
     * Invia un comando {@code SELECT} filtrato per "medico_login".
     * All'interno del ciclo di lettura del cursore dei dati, estrae la stringa del giorno e la mappa
     * sulla costante Enum interna {@code TurnoLavorativo.GiornoSettimana.valueOf()} per garantire la coerenza
     * tipologica del software. Scompone i campi {@link java.sql.Timestamp} trasformandoli nei moderni oggetti
     * java temporali {@link java.time.LocalDateTime} mediante il metodo {@code toLocalDateTime()} e inserisce
     * le istanze create in una lista di output.
     * </p>
     *
     * @param medicoLogin l'username/login dell'account del medico da controllare
     * @return una lista di tipo ArrayList contenente i turni settimanali mappati
     * @throws RuntimeException incapsula una {@link SQLException} in caso di malfunzionamenti o errori SQL
     */
    @Override
    public List<TurnoLavorativo> findByMedico(String medicoLogin) {
        List<TurnoLavorativo> turni = new ArrayList<>();
        String sql = "SELECT giorno, inizio, fine FROM turni_lavorativi WHERE medico_login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TurnoLavorativo.GiornoSettimana giorno = TurnoLavorativo.GiornoSettimana.valueOf(rs.getString("giorno"));
                    turni.add(new TurnoLavorativo(
                            giorno,
                            rs.getTimestamp("inizio").toLocalDateTime(),
                            rs.getTimestamp("fine").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei turni del medico " + medicoLogin, e);
        }

        return turni;
    }
    /**
     * Archivia un nuovo slot di turno lavorativo associandolo a un medico.
     * <p>
     * Struttura un'istruzione {@code INSERT INTO}. Richiede il nome letterale
     * della costante dell'Enum del giorno mediante {@code name()} e converte i parametri temporali di tipo
     * {@link java.time.LocalDateTime} in oggetti compatibili con la base dati relazionale invocando la funzione
     * statica {@code Timestamp.valueOf(turno.getInizio())}. Esegue infine la scrittura mediante {@code executeUpdate()}.
     * </p>
     *
     * @param turno       il modello del turno lavorativo da inserire a database
     * @param medicoLogin la stringa di login del medico assegnatario dello slot
     * @throws RuntimeException incapsula una {@link SQLException} se l'archiviazione fallisce
     */
    @Override
    public void save(TurnoLavorativo turno, String medicoLogin) {
        String sql = "INSERT INTO turni_lavorativi (medico_login, giorno, inizio, fine) VALUES (?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);
            stmt.setString(2, turno.getGiorno().name());
            stmt.setTimestamp(3, Timestamp.valueOf(turno.getInizio()));
            stmt.setTimestamp(4, Timestamp.valueOf(turno.getFine()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del turno del medico " + medicoLogin, e);
        }
    }
    /**
     * Cancella un determinato slot orario di turno lavorativo precedentemente programmato per un medico.
     * <p>
     * Effettua una rimozione di sicurezza impostando un comando {@code DELETE FROM}
     * pesantemente blindato nella clausola {@code WHERE} da ben quattro filtri simultanei ("medico_login", "giorno",
     * "inizio" e "fine" convertiti in Timestamp). Raccoglie il numero di righe rimosse restituito dal driver JDBC:
     * se questo contatore è pari a zero, intercetta l'anomalia scatenando un'eccezione mirata per segnalare che lo slot
     * inserito non esisteva o era già stato modificato.
     * </p>
     *
     * @param turno       il modello del turno lavorativo da rimuovere
     * @param medicoLogin l'username/login del medico associato
     * @throws RuntimeException incapsula una {@link SQLException} o lancia un fallimento logico guidato se non viene eliminata alcuna riga
     */
    @Override
    public void delete(TurnoLavorativo turno, String medicoLogin) {
        String sql = "DELETE FROM turni_lavorativi WHERE medico_login = ? AND giorno = ? AND inizio = ? AND fine = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);
            stmt.setString(2, turno.getGiorno().name());
            stmt.setTimestamp(3, Timestamp.valueOf(turno.getInizio()));
            stmt.setTimestamp(4, Timestamp.valueOf(turno.getFine()));

            int righeEliminate = stmt.executeUpdate();
            if (righeEliminate == 0) {
                throw new RuntimeException("Nessun turno trovato per medico=" + medicoLogin
                        + ", giorno=" + turno.getGiorno() + ": l'eliminazione non ha avuto effetto.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del turno del medico " + medicoLogin, e);
        }
    }
}