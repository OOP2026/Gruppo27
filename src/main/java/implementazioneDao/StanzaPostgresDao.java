package implementazioneDao;

import dao.LettoDAO;
import dao.StanzaDAO;
import database_connection.ConnessioneDatabase;
import model.Letto;
import model.Stanza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementazione Postgres dell'interfaccia StanzaDAO.
 * Coordina l'estrazione delle stanze collegandovi i rispettivi letti disponibili tramite
 * composizione ed interazione con il componente LettoDAO.
 */
public class StanzaPostgresDao implements StanzaDAO {
    /**
     * Recupera tutte le stanze associate a un reparto, popolandone i letti inclusi.
     * <p>
     * Il metodo interroga la tabella delle stanze filtrando per "reparto_num".
     * Per ciascuna stanza intercettata dal cursore del {@link ResultSet}, istanzia un nuovo oggetto Stanza.
     * Successivamente, istanzia localmente un componente {@link LettoPostgresDao} ed avvia un ciclo nidificato
     * richiamando il metodo {@code findByStanza(numeroStanza, repartoNum)}. Questa sotto-operazione estrae dal
     * database i letti configurati e li inserisce uno ad uno nella stanza tramite {@code stanza.aggiungiLetto(letto)},
     * strutturando l'albero dei dati prima dell'inserimento nella lista finale.
     * </p>
     *
     * @param repartoNum il numero identificativo del reparto di riferimento
     * @return una lista di oggetti Stanza pronti e popolati con i rispettivi letti
     * @throws RuntimeException incapsula una {@link SQLException} in caso di interruzioni di rete o di query SQL errate
     */
    @Override
    public List<Stanza> findByReparto(int repartoNum) {
        List<Stanza> stanze = new ArrayList<>();
        String sql = "SELECT numero FROM stanze WHERE reparto_num = ?";
        Connection conn = ConnessioneDatabase.getInstance();
        LettoDAO lettoDao = new LettoPostgresDao();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, repartoNum);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int numeroStanza = rs.getInt("numero");
                    Stanza stanza = new Stanza(numeroStanza);

                    for (Letto letto : lettoDao.findByStanza(numeroStanza, repartoNum)) {
                        stanza.aggiungiLetto(letto);
                    }

                    stanze.add(stanza);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero delle stanze del reparto " + repartoNum, e);
        }

        return stanze;
    }
    /**
     * Registra una nuova stanza associandola al rispettivo reparto.
     * <p>
     * Prepara un comando formale {@code INSERT INTO stanze}.
     * Associa l'identificativo numerico della stanza e quello del reparto ricevuto come parametro agli indici
     * del statement ed esegue l'aggiornamento strutturale richiamando {@code executeUpdate()}.
     * </p>
     *
     * @param stanza     l'oggetto modello Stanza da registrare a sistema
     * @param repartoNum il codice numerico del reparto proprietario
     * @throws RuntimeException incapsula una {@link SQLException} in caso di fallimento della scrittura
     */
    @Override
    public void save(Stanza stanza, int repartoNum) {
        String sql = "INSERT INTO stanze (numero, reparto_num) VALUES (?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stanza.getNumero());
            stmt.setInt(2, repartoNum);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della stanza " + stanza.getNumero(), e);
        }
    }
    /**
     * Rimuove una stanza dal database basandosi sulla sua chiave primaria composta.
     * <p>
     * Invia una direttiva di rimozione {@code DELETE FROM stanze}.
     * Localizza in modo univoco il record configurando entrambi i parametri discriminanti della clausola
     * {@code WHERE} ("numero" e "reparto_num") per impedire la rimozione accidentale di stanze omonime in altri reparti.
     * </p>
     *
     * @param numero     il numero della stanza da eliminare
     * @param repartoNum il numero del reparto in cui è allocata la stanza
     * @throws RuntimeException incapsula una {@link SQLException} se l'eliminazione viene rifiutata dal database
     */
    @Override
    public void delete(int numero, int repartoNum) {
        String sql = "DELETE FROM stanze WHERE numero = ? AND reparto_num = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            stmt.setInt(2, repartoNum);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione della stanza " + numero, e);
        }
    }

}