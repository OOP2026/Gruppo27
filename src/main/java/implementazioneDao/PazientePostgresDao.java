package implementazioneDao;

import dao.PazienteDAO;
import database_connection.ConnessioneDatabase;
import model.Paziente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione Postgres dell'interfaccia PazienteDAO.
 * Gestisce l'anagrafica dei pazienti sul database relazionale, applicando controlli preventivi
 * di formattazione sui codici fiscali scambiati.
 */
public class PazientePostgresDao implements PazienteDAO {

    /**
     * Cerca un paziente sul database a partire dal suo codice fiscale (CF).
     * <p>
     * Per garantire l'accuratezza della ricerca ed evitare falsi negativi dovuti
     * a formattazioni eterogenee, il parametro stringa in ingresso viene prima elaborato dal metodo privato
     * {@code normalizeCf(cf)}, che elimina gli spazi vuoti e forza i caratteri in maiuscolo. La stringa
     * normalizzata viene passata come parametro a una query {@code SELECT *}. Se viene trovata una riga,
     * il record viene convertito in un oggetto Paziente mediante il metodo di mapping dedicato
     * {@code mappaPaziente(rs)} e restituito all'interno di un contenitore {@link Optional}.
     * </p>
     *
     * @param cf il codice fiscale del paziente da ricercare
     * @return un Optional contenente il Paziente mappato se presente, altrimenti un Optional vuoto
     * @throws RuntimeException incapsula una {@link SQLException} in caso di errori di esecuzione SQL
     */
    @Override
    public Optional<Paziente> findByCf(String cf) {
        String sql = "SELECT * FROM pazienti WHERE cf = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeCf(cf));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mappaPaziente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del paziente con cf " + cf, e);
        }

        return Optional.empty();
    }
    /**
     * Recupera la lista completa di tutti i pazienti registrati nel sistema.
     * <p>
     * Il metodo esegue un'interrogazione totale {@code SELECT * FROM pazienti}.
     * Attraverso un costrutto ciclico {@code while(rs.next())}, scorre l'intero set di dati restituito,
     * estrae in modo sequenziale le informazioni di ciascun record riutilizzando internamente la logica di
     * {@code mappaPaziente(rs)}, e accumula le istanze all'interno di una lista {@link ArrayList}.
     * </p>
     *
     * @return una lista contenente tutti gli oggetti Paziente registrati a database
     * @throws RuntimeException incapsula una {@link SQLException} se il recupero dei dati fallisce
     */
    @Override
    public List<Paziente> findAll() {
        List<Paziente> pazienti = new ArrayList<>();
        String sql = "SELECT * FROM pazienti";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pazienti.add(mappaPaziente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei pazienti", e);
        }

        return pazienti;
    }
    /**
     * Registra un nuovo paziente nel database memorizzandone i dati anagrafici essenziali.
     * <p>
     * Compila un comando di inserimento {@code INSERT INTO}.
     * Estrae le proprietà dall'oggetto modello Paziente fornito in input e ne mappa i valori
     * sul {@link PreparedStatement}. Prima di mappare la chiave primaria "cf", invoca la funzione
     * di normalizzazione per garantire l'allineamento dei caratteri maiuscoli sul database.
     * Rende persistente il record invocando {@code executeUpdate()}.
     * </p>
     *
     * @param paziente l'oggetto modello Paziente da registrare
     * @throws RuntimeException incapsula una {@link SQLException} se l'inserimento viola i vincoli di chiave primaria unica
     */
    @Override
    public void save(Paziente paziente) {
        String sql = "INSERT INTO pazienti (cf, nome, cognome, recapito) VALUES (?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeCf(paziente.getCf()));
            stmt.setString(2, paziente.getNome());
            stmt.setString(3, paziente.getCognome());
            stmt.setString(4, paziente.getRecapito());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del paziente " + paziente.getCf(), e);
        }
    }
    /**
     * Modifica i dati di contatto o anagrafici di un paziente esistente individuato tramite il suo CF.
     * <p>
     * Esegue una direttiva {@code UPDATE} sulla tabella "pazienti".
     * Mappa i nuovi valori estratti dal modello (nome, cognome, recapito) come parametri di modifica
     * e applica la normalizzazione sul codice fiscale usato come filtro discriminante nella clausola
     * {@code WHERE}, aggiornando in modo atomico ed esclusivo la riga associata al paziente selezionato.
     * </p>
     *
     * @param paziente l'oggetto Paziente contenente i dati aggiornati da sovrascrivere
     * @throws RuntimeException incapsula una {@link SQLException} in caso di problemi di comunicazione con il DB
     */
    @Override
    public void update(Paziente paziente) {
        String sql = "UPDATE pazienti SET nome = ?, cognome = ?, recapito = ? WHERE cf = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paziente.getNome());
            stmt.setString(2, paziente.getCognome());
            stmt.setString(3, paziente.getRecapito());
            stmt.setString(4, normalizeCf(paziente.getCf()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento del paziente " + paziente.getCf(), e);
        }
    }
    /**
     * Cancella la scheda anagrafica di un paziente dal database in base al codice fiscale.
     * <p>
     * Il metodo invia una richiesta di rimozione {@code DELETE FROM} condizionata.
     * Applica preventivamente la normalizzazione al parametro "cf" ricevuto e lo imposta sul statement
     * preparato, rimuovendo definitivamente dal sistema la riga anagrafica corrispondente.
     * </p>
     *
     * @param cf il codice fiscale del paziente da rimuovere
     * @throws RuntimeException incapsula una {@link SQLException} se l'eliminazione fallisce (es. per violazione di vincoli di integrità referenziale)
     */
    @Override
    public void delete(String cf) {
        String sql = "DELETE FROM pazienti WHERE cf = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeCf(cf));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del paziente " + cf, e);
        }
    }
    /**
     * Rimuove gli spazi bianchi esterni e converte in lettere maiuscole la stringa del codice fiscale.
     * <p>
     * Sfrutta i metodi nativi {@link String#trim()} e {@link String#toUpperCase()}
     * previa verifica di sicurezza sulla nullità del riferimento per prevenire eccezioni di tipo NullPointerException.
     * </p>
     *
     * @param cf il codice fiscale grezzo
     * @return la stringa normalizzata pronta per il database, o null se l'input era nullo
     */
    private String normalizeCf(String cf) {
        return cf == null ? null : cf.trim().toUpperCase();
    }
    /**
     * Effettua il mapping dei campi della riga corrente del ResultSet in un oggetto modello Paziente.
     * <p>
     * Accede ai dati estratti dal cursore del database richiedendo
     * esplicitamente le stringhe associate alle etichette di colonna ("cf", "nome", "cognome", "recapito")
     * e le passa direttamente al costruttore della classe Paziente, restituendo l'istanza configurata.
     * </p>
     *
     * @param rs il ResultSet posizionato sulla riga da mappare
     * @return un'istanza di Paziente valorizzata con i dati del record
     * @throws SQLException se si verifica un errore durante il recupero dei dati dalle colonne
     */
    private Paziente mappaPaziente(ResultSet rs) throws SQLException {
        return new Paziente(
                rs.getString("cf"),
                rs.getString("nome"),
                rs.getString("cognome"),
                rs.getString("recapito")
        );
    }
}