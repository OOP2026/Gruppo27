package implementazioneDao;

import dao.LettoDAO;
import database_connection.ConnessioneDatabase;
import model.Letto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione Postgres dell'interfaccia LettoDAO.
 * Gestisce la persistenza dei letti ospedalieri interfacciandosi con la tabella "letti"
 * attraverso query JDBC tradizionali e mappature manuali dei record.
 */
public class LettoPostgresDao implements LettoDAO {
    /**
     * Cerca un letto sul database tramite il suo codice inventario unico.
     * <p>
     * Il metodo interroga il database mediante una query
     * {@code SELECT} mirata sulla tabella "letti". Sfrutta un {@link PreparedStatement} per
     * iniettare in sicurezza il codice inventario ed evitare SQL Injection. Utilizza un blocco
     * try-with-resources per garantire la chiusura automatica di statement e {@link ResultSet}.
     * Se il record esiste, estrae i campi "codice_inventario" e "libero" per istanziare e
     * restituire l'oggetto modello racchiuso in un {@link Optional}; in caso contrario,
     * ritorna un {@link Optional#empty()}.
     * </p>
     *
     * @param codiceInventario il codice identificativo univoco del letto da cercare
     * @return un Optional contenente l'oggetto Letto se trovato, altrimenti un Optional vuoto
     * @throws RuntimeException incapsula una {@link SQLException} se si verifica un errore di connessione o di esecuzione della query
     */
    @Override
    public Optional<Letto> findByCodice(String codiceInventario) {
        String sql = "SELECT codice_inventario, libero FROM letti WHERE codice_inventario = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceInventario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Letto(rs.getString("codice_inventario"), rs.getBoolean("libero")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del letto " + codiceInventario, e);
        }

        return Optional.empty();
    }
    /**
     * Recupera l'elenco di tutti i letti associati a una stanza e a un reparto specifici.
     * <p>
     * Il metodo esegue una {@code SELECT} filtrata con una
     * clausola {@code WHERE} a due parametri ("stanza_numero" e "reparto_num"). Tramite un ciclo
     * {@code while} sul {@link ResultSet}, scorre tutte le righe restituite dal database,
     * istanzia un oggetto Letto per ciascun record trovato e lo aggiunge a una lista di tipo
     * {@link ArrayList}. Statement e ResultSet vengono gestiti tramite try-with-resources
     * per prevenire memory leak di connessione.
     * </p>
     *
     * @param numeroStanza il numero identificativo della stanza
     * @param repartoNum   il numero identificativo del reparto
     * @return una lista (ArrayList) contenente tutti i letti configurati per quella stanza
     * @throws RuntimeException incapsula una {@link SQLException} in caso di anomalie nell'interrogazione del database
     */
    @Override
    public List<Letto> findByStanza(int numeroStanza, int repartoNum) {
        List<Letto> letti = new ArrayList<>();
        String sql = "SELECT codice_inventario, libero FROM letti WHERE stanza_numero = ? AND reparto_num = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroStanza);
            stmt.setInt(2, repartoNum);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    letti.add(new Letto(rs.getString("codice_inventario"), rs.getBoolean("libero")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei letti della stanza " + numeroStanza, e);
        }

        return letti;
    }
    /**
     * Inserisce un nuovo record letto nel database associandolo a una determinata stanza e reparto.
     * <p>
     * Prepara un comando d'inserimento {@code INSERT INTO}
     * specificando le quattro colonne necessarie. Estrae i valori nativi dall'oggetto modello Letto
     * (codice e stato booleano di disponibilità) e, insieme ai parametri numerici passati in input,
     * li mappa sugli indici del {@link PreparedStatement}. Infine, invoca {@code executeUpdate()}
     * per rendere persistente la modifica sul database relazionale.
     * </p>
     *
     * @param letto        l'oggetto modello Letto da inserire a sistema
     * @param numeroStanza il numero della stanza in cui posizionare il letto
     * @param repartoNum   il codice numerico del reparto di appartenenza
     * @throws RuntimeException incapsula una {@link SQLException} se l'inserimento fallisce per violazione di vincoli o problemi di rete
     */
    @Override
    public void save(Letto letto, int numeroStanza, int repartoNum) {
        String sql = "INSERT INTO letti (codice_inventario, libero, stanza_numero, reparto_num) VALUES (?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, letto.getCodiceInventario());
            stmt.setBoolean(2, letto.isLibero());
            stmt.setInt(3, numeroStanza);
            stmt.setInt(4, repartoNum);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del letto " + letto.getCodiceInventario(), e);
        }
    }
    /**
     * Aggiorna lo stato di disponibilità (libero o occupato) di un letto specifico tramite codice inventario.
     * <p>
     * Il metodo esegue un comando {@code UPDATE} mirato sulla tabella "letti".
     * Imposta la colonna booleana "libero" basandosi sul parametro fornito e applica il filtro di
     * sbarramento della clausola {@code WHERE} basato sul codice inventario del letto, per localizzare
     * ed aggiornare unicamente la riga desiderata sul database.
     * </p>
     *
     * @param codiceInventario il codice identificativo del letto da aggiornare
     * @param libero           lo stato booleano da assegnare (true per libero, false per occupato)
     * @throws RuntimeException incapsula una {@link SQLException} se l'aggiornamento non va a buon fine
     */
    @Override
    public void updateStato(String codiceInventario, boolean libero) {
        String sql = "UPDATE letti SET libero = ? WHERE codice_inventario = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, libero);
            stmt.setString(2, codiceInventario);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dello stato del letto " + codiceInventario, e);
        }
    }
    /**
     * Rimuove definitivamente un letto dal database.
     * <p>
     * Il metodo effettua un'operazione distruttiva inviando un comando
     * {@code DELETE FROM} condizionato dal codice inventario del letto. La riga viene individuata univocamente
     * grazie all'indice della chiave primaria mappato tramite il {@link PreparedStatement}.
     * </p>
     *
     * @param codiceInventario il codice identificativo del letto da eliminare definitivamente
     * @throws RuntimeException incapsula una {@link SQLException} in caso di fallimento del comando di rimozione
     */
    @Override
    public void delete(String codiceInventario) {
        String sql = "DELETE FROM letti WHERE codice_inventario = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceInventario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del letto " + codiceInventario, e);
        }
    }
}
