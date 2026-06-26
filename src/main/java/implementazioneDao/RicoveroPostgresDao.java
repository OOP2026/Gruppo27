package implementazioneDao;

import dao.RicoveroDAO;
import database_connection.ConnessioneDatabase;
import model.Letto;
import model.Ricovero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementazione Postgres dell'interfaccia RicoveroDAO.
 * Sovraintende ai flussi informativi clinici legati all'ospedalizzazione dei pazienti,
 * implementando routine centralizzate di query mapping ed isolamento dei dati per i letti assegnati.
 */
public class RicoveroPostgresDao implements RicoveroDAO {
    /**
     * Estrae lo storico completo di tutti i ricoveri (attivi e d'archivio) legati all'SSN di un determinato paziente.
     * <p>
     * Applica una query condizionata delegando l'esecuzione alla funzione
     * protetta centralizzata {@code eseguiQuery(sql, ssn)}, iniettando l'SSN preventivamente normalizzato (in maiuscolo).
     * </p>
     *
     * @param ssn il codice fiscale (SSN) del paziente in esame
     * @return una lista di oggetti Ricovero mappati
     */
    @Override
    public List<Ricovero> findByPaziente(String ssn) {
        String sql = "SELECT * FROM ricoveri WHERE ssn = ?";
        return eseguiQuery(sql, normalizeSsn(ssn));
    }
    /**
     * Ritorna l'elenco di tutte le degenze attualmente attive in struttura (in_corso = TRUE).
     * <p>
     * Esegue la routine centralizzata impostando il parametro di filtro
     * a {@code null} ed eseguendo una stringa SQL dotata del filtro booleano preimpostato {@code WHERE in_corso = TRUE}.
     * </p>
     *
     * @return la lista di tutti i ricoveri attivi presenti in struttura
     */
    @Override
    public List<Ricovero> findInCorso() {
        String sql = "SELECT * FROM ricoveri WHERE in_corso = TRUE";
        return eseguiQuery(sql, null);
    }
    /**
     * Metodo di supporto centralizzato per la preparazione ed esecuzione controllata delle query di selezione.
     * <p>
     * Incapsula l'apertura del statement e del cursore dati. Se il parametro "ssn"
     * risulta valorizzato e non nullo, provvede a impostarlo dinamicamente sull'indice del {@link PreparedStatement},
     * cicla i record estratti invvocando per ciascuno il metodo di scomposizione ed instradamento {@code mappaRicovero(rs)}
     * e restituisce la collezione memorizzata in una {@link ArrayList}.
     * </p>
     */
    private List<Ricovero> eseguiQuery(String sql, String ssn) {
        List<Ricovero> ricoveri = new ArrayList<>();
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (ssn != null) {
                stmt.setString(1, ssn);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ricoveri.add(mappaRicovero(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei ricoveri", e);
        }

        return ricoveri;
    }
    /**
     * Inserisce un nuovo record di ricovero o di day hospital a sistema.
     * <p>
     * Struttura un comando completo {@code INSERT INTO} composto da 10 parametri.
     * Per mappare in modo conforme i dati cronologici, estrae gli oggetti {@link java.util.Date} e li incapsula
     * in oggetti {@link java.sql.Timestamp} mediante il metodo {@code toTimestamp(data)}. Estrae l'istanza Letto
     * legata al ricovero e, se presente, ne immette a sistema il codice inventario univoco testuale, altrimenti
     * imposta esplicitamente un valore SQL {@code null}. Esegue l'operazione mediante {@code executeUpdate()}.
     * </p>
     *
     * @param ricovero l'oggetto modello Ricovero da salvare
     * @throws RuntimeException incapsula una {@link SQLException} in caso di violazione dei vincoli relazionali
     */
    @Override
    public void save(Ricovero ricovero) {
        String sql = "INSERT INTO ricoveri (ssn, data_ricovero, data_dimissione_prevista, letto_codice, day_hospital, descrizione, terapia, diagnosi_entrata, diagnosi_uscita, in_corso) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeSsn(ricovero.getSsn()));
            stmt.setTimestamp(2, new Timestamp(ricovero.getDataRicovero().getTime()));
            stmt.setTimestamp(3, toTimestamp(ricovero.getDataDimissionePrevista()));

            Letto letto = ricovero.getLettoAssegnato();
            stmt.setString(4, letto != null ? letto.getCodiceInventario() : null);

            stmt.setBoolean(5, ricovero.isDayHospital());
            stmt.setString(6, ricovero.getDescrizione());
            stmt.setString(7, ricovero.getTerapia());
            stmt.setString(8, ricovero.getDiagnosiEntrata());
            stmt.setString(9, ricovero.getDiagnosiUscita());
            stmt.setBoolean(10, ricovero.isInCorso());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del ricovero per il paziente " + ricovero.getSsn(), e);
        }
    }
    /**
     * Aggiorna lo stato clinico e le date effettive/previste di rilascio di un ricovero alla chiusura della degenza.
     * <p>
     * Invia una direttiva {@code UPDATE} condizionata da una **chiave composta logica** * nella clausola {@code WHERE} composta dall'accoppiata "ssn" e "data_ricovero" (convertito in Timestamp). Questo garantisce
     * l'isolamento della singola degenza nel caso in cui lo stesso paziente abbia più ricoveri storici. Controlla il contatore
     * delle righe modificate rimandando un'eccezione esplicita guidata se il record primario non viene intercettato.
     * </p>
     *
     * @param ricovero l'oggetto Ricovero aggiornato da sincronizzare nel database
     * @throws RuntimeException incapsula una {@link SQLException} o lancia un errore logico se nessuna riga subisce variazioni
     */
    @Override
    public void update(Ricovero ricovero) {
        String sql = "UPDATE ricoveri SET data_dimissione_prevista = ?, data_dimissione_effettiva = ?, "
                + "letto_codice = ?, day_hospital = ?, descrizione = ?, terapia = ?, diagnosi_entrata = ?, diagnosi_uscita = ?, in_corso = ? "
                + "WHERE ssn = ? AND data_ricovero = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, toTimestamp(ricovero.getDataDimissionePrevista()));
            stmt.setTimestamp(2, toTimestamp(ricovero.getDataDimissioneEffettiva()));

            Letto letto = ricovero.getLettoAssegnato();
            stmt.setString(3, letto != null ? letto.getCodiceInventario() : null);

            stmt.setBoolean(4, ricovero.isDayHospital());
            stmt.setString(5, ricovero.getDescrizione());
            stmt.setString(6, ricovero.getTerapia());
            stmt.setString(7, ricovero.getDiagnosiEntrata());
            stmt.setString(8, ricovero.getDiagnosiUscita());
            stmt.setBoolean(9, ricovero.isInCorso());
            stmt.setString(10, normalizeSsn(ricovero.getSsn()));
            stmt.setTimestamp(11, new Timestamp(ricovero.getDataRicovero().getTime()));

            int righeAggiornate = stmt.executeUpdate();
            if (righeAggiornate == 0) {
                throw new RuntimeException("Nessun ricovero trovato per ssn=" + ricovero.getSsn()
                        + " e data_ricovero=" + ricovero.getDataRicovero() + ": l'aggiornamento non ha avuto effetto.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento del ricovero per il paziente " + ricovero.getSsn(), e);
        }
    }

    private Timestamp toTimestamp(java.util.Date data) {
        return data != null ? new Timestamp(data.getTime()) : null;
    }

    private String normalizeSsn(String ssn) {
        return ssn == null ? null : ssn.trim().toUpperCase();
    }

    /**
     * Mappa i campi di riga del database all'interno di una nuova istanza modello di tipo Ricovero.
     * <p>
     * Estrae i codici identificativi principali dal cursore. Se rileva la presenza
     * di un codice legato alla colonna "letto_codice", esegue una sotto-funzione protetta di caricamento chiamata
     * {@code caricaLetto(codiceLetto)} per istanziare l'oggetto Letto da associare. Popola poi in sequenza tutti i campi
     * accessori (terapia, note, stato booleano di chiusura) prima di restituire il modello configurato.
     * </p>
     */
    private Ricovero mappaRicovero(ResultSet rs) throws SQLException {
        Letto letto = null;
        String codiceLetto = rs.getString("letto_codice");

        if (codiceLetto != null) {
            letto = caricaLetto(codiceLetto);
        }

        Ricovero ricovero = new Ricovero(
                rs.getString("ssn"),
                rs.getTimestamp("data_ricovero"),
                rs.getTimestamp("data_dimissione_prevista"),
                letto,
                rs.getString("diagnosi_entrata")
        );

        ricovero.setDataDimissioneEffettiva(rs.getTimestamp("data_dimissione_effettiva"));
        ricovero.setDiagnosiUscita(rs.getString("diagnosi_uscita"));
        ricovero.setInCorso(rs.getBoolean("in_corso"));
        ricovero.setDayHospital(rs.getBoolean("day_hospital"));
        ricovero.setDescrizione(rs.getString("descrizione"));
        ricovero.setTerapia(rs.getString("terapia"));

        return ricovero;
    }
    /**
     * Esegue una query interna isolata per caricare le specifiche del letto inserito a sistema.
     * <p>
     * Interroga la tabella dei letti tramite codice. Per prevenire anomalie bloccanti
     * causate da record rimossi orfani (casi anomali di database), se la query non restituisce righe, il metodo non lancia
     * eccezioni ma intercetta lo scenario instanziando un oggetto Letto minimale fittizio segnato come occupato, preservando
     * l'integrità del ricovero.
     * </p>
     */
    private Letto caricaLetto(String codiceInventario) throws SQLException {
        String sql = "SELECT codice_inventario, libero FROM letti WHERE codice_inventario = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceInventario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Letto(rs.getString("codice_inventario"), rs.getBoolean("libero"));
                }
            }
        }

        // Il letto referenziato non esiste più nella tabella letti (caso anomalo):
        // restituiamo comunque un riferimento minimale per non perdere il collegamento al ricovero.
        return new Letto(codiceInventario, false);
    }
}