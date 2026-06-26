package implementazioneDao;

import dao.PrestazioneMedicaDAO;
import database_connection.ConnessioneDatabase;
import model.PrestazioneMedica;
import model.Ricovero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * Implementazione Postgres dell'interfaccia PrestazioneMedicaDAO.
 * Gestisce la persistenza e l'interrogazione delle prestazioni sanitarie (visite, interventi),
 * implementando strategie ottimizzate di risoluzione batch per identificare i medici curanti assegnati.
 */
public class PrestazioneMedicaPostgresDao implements PrestazioneMedicaDAO {
    /**
     * Restituisce la lista di tutte le prestazioni registrate o effettuate da un determinato medico.
     * <p>
     * Esegue una query {@code SELECT} mirata sulla tabella
     * "prestazioni_mediche" filtrando per "medico_login". Nel ciclo di lettura dei record, estrae il valore
     * testuale del tipo prestazione e lo mappa sull'Enum corrispondente mediante {@link Enum#valueOf(Class, String)}.
     * Converte il {@link java.sql.Timestamp} del database nel moderno formato di data {@link java.time.LocalDateTime}
     * tramite il metodo {@code toLocalDateTime()}, configura i campi opzionali (SSN e descrizione) e popola
     * una lista cumulativa restituita al termine dell'operazione.
     * </p>
     *
     * @param medicoLogin l'username o identificativo di login del medico esaminato
     * @return una lista di oggetti PrestazioneMedica associati al medico
     * @throws RuntimeException incapsula una {@link SQLException} in caso di anomalie di lettura
     */
    @Override
    public List<PrestazioneMedica> findByMedico(String medicoLogin) {
        List<PrestazioneMedica> prestazioni = new ArrayList<>();
        String sql = "SELECT tipo, data_ora, esito, ssn_paziente, descrizione FROM prestazioni_mediche WHERE medico_login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PrestazioneMedica.Prestazione tipo = PrestazioneMedica.Prestazione.valueOf(rs.getString("tipo"));
                    PrestazioneMedica prestazione = new PrestazioneMedica(
                            tipo,
                            rs.getTimestamp("data_ora").toLocalDateTime(),
                            rs.getString("esito")
                    );
                    prestazione.setSsnPaziente(rs.getString("ssn_paziente"));
                    prestazione.setDescrizione(rs.getString("descrizione"));
                    prestazioni.add(prestazione);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero delle prestazioni del medico " + medicoLogin, e);
        }

        return prestazioni;
    }
    /**
     * Identifica l'ultimo medico che ha eseguito una prestazione sul paziente a partire dalla data di inizio ricovero.
     * <p>
     * Il metodo effettua una query di giunzione {@code JOIN} tra la tabella
     * "prestazioni_mediche" e la tabella "utenti" per combinare le credenziali cliniche. Applica un doppio filtro
     * restrittivo: l'SSN del paziente (previa normalizzazione) e la data di sbarramento temporale, convertendo la
     * {@link java.util.Date} in un {@link java.sql.Timestamp} compatibile con il database. Sfrutta le clausole
     * {@code ORDER BY data_ora DESC} e {@code LIMIT 1} per forzare il database a restituire esclusivamente ed
     * efficientemente l'unico record che rappresenta l'atto medico più recente in ordine cronologico.
     * </p>
     *
     * @param ssn          il codice fiscale (SSN) del paziente in degenza
     * @param dataRicovero la data di inizio del ricovero da usare come sbarramento inferiore
     * @return un Optional contenente la stringa formattata "Nome Cognome" del medico se presente, altrimenti vuoto
     * @throws RuntimeException incapsula una {@link SQLException} in caso di errori nell'interrogazione del database
     */
    @Override
    public Optional<String> findMedicoAssegnato(String ssn, java.util.Date dataRicovero) {
        // La query ora cerca solo le prestazioni avvenute DOPO l'inizio del ricovero
        String sql = "SELECT u.nome, u.cognome " +
                "FROM prestazioni_mediche p " +
                "JOIN utenti u ON p.medico_login = u.login " +
                "WHERE p.ssn_paziente = ? AND p.data_ora >= ? " +
                "ORDER BY p.data_ora DESC " +
                "LIMIT 1";

        Connection conn = database_connection.ConnessioneDatabase.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeSsn(ssn));

            // Convertiamo la java.util.Date del ricovero in java.sql.Timestamp per il database
            stmt.setTimestamp(2, new java.sql.Timestamp(dataRicovero.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nomeCompleto = rs.getString("nome") + " " + rs.getString("cognome");
                    return Optional.of(nomeCompleto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del medico assegnato al paziente " + ssn, e);
        }

        return Optional.empty();
    }
    /**
     * Risolve in un'unica operazione batch i medici correnti assegnati per un elenco di ricoveri attivi, ottimizzando i tempi di risposta.
     * <p>
     * Per evitare il problema delle performance dovuto all'esecuzione di query ripetitive nei cicli,
     * il metodo estrae gli SSN univoci dai ricoveri e genera un array nativo JDBC mappato tramite {@code conn.createArrayOf("varchar", ...)}.
     * Questo viene inserito in una query che sfrutta l'operatore {@code WHERE p.ssn_paziente = ANY(?)} per estrarre massivamente i record ordinati
     * cronologicamente per paziente. Sfruttando l'ordinamento decrescente {@code ORDER BY data_ora DESC}, il codice inserisce nella mappa
     * di output la prima occorrenza valida riscontrata per ciascun paziente (ossia la più recente), a patto che l'orario della prestazione
     * sia successivo alla rispettiva data di ammissione recuperata dalla struttura di supporto {@code dataRicoveroPerSsn}. Le righe obsolete
     * successive vengono scartate mediante il controllo {@code containsKey(ssn)}.
     * </p>
     *
     * @param ricoveri la lista dei modelli di ricovero di cui mappare i medici curanti
     * @return una mappa associativa strutturata come [Codice Fiscale Paziente, Nome Completo Medico Assegnato]
     * @throws RuntimeException incapsula una {@link SQLException} in caso di anomalie di esecuzione SQL di blocco o di array mapping
     */
    @Override
    public Map<String, String> findMedicoAssegnatoBatch(List<Ricovero> ricoveri) {
        Map<String, String> risultato = new HashMap<>();
        if (ricoveri == null || ricoveri.isEmpty()) {
            return risultato;
        }

        // Raccogliamo gli ssn distinti coinvolti, normalizzati, per costruire la clausola IN
        List<String> ssnDistinti = new ArrayList<>();
        for (Ricovero r : ricoveri) {
            String ssn = normalizeSsn(r.getSsn());
            if (ssn != null && !ssnDistinti.contains(ssn)) {
                ssnDistinti.add(ssn);
            }
        }
        if (ssnDistinti.isEmpty()) {
            return risultato;
        }


        String sql = "SELECT p.ssn_paziente, p.data_ora, u.nome, u.cognome " +
                "FROM prestazioni_mediche p " +
                "JOIN utenti u ON p.medico_login = u.login " +
                "WHERE p.ssn_paziente = ANY(?) " +
                "ORDER BY p.ssn_paziente, p.data_ora DESC";

        Connection conn = ConnessioneDatabase.getInstance();
        // Per ogni ssn conserviamo solo la prestazione più recente già incontrata (grazie
        // all'ORDER BY la prima riga vista per ogni ssn è già la più recente in assoluto;
        // qui applichiamo anche il vincolo "successiva alla data di inizio ricovero").
        Map<String, java.sql.Timestamp> dataRicoveroPerSsn = new HashMap<>();
        for (Ricovero r : ricoveri) {
            String ssn = normalizeSsn(r.getSsn());
            if (ssn != null && r.getDataRicovero() != null) {
                dataRicoveroPerSsn.put(ssn, new java.sql.Timestamp(r.getDataRicovero().getTime()));
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            java.sql.Array ssnArray = conn.createArrayOf("varchar", ssnDistinti.toArray());
            stmt.setArray(1, ssnArray);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ssn = rs.getString("ssn_paziente");

                    // Se per questo ssn abbiamo già trovato un medico (la riga più recente
                    // grazie all'ORDER BY), le righe successive per lo stesso ssn vanno ignorate
                    if (risultato.containsKey(ssn)) {
                        continue;
                    }

                    java.sql.Timestamp dataRicovero = dataRicoveroPerSsn.get(ssn);
                    java.sql.Timestamp dataPrestazione = rs.getTimestamp("data_ora");

                    if (dataRicovero != null && dataPrestazione.before(dataRicovero)) {
                        // Questa prestazione è precedente all'inizio del ricovero corrente:
                        // non è valida come "medico assegnato" per questo ricovero.
                        continue;
                    }

                    risultato.put(ssn, rs.getString("nome") + " " + rs.getString("cognome"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero batch dei medici assegnati", e);
        }

        return risultato;
    }
    /**
     * Archivia una nuova prestazione medica sul database legandola all'account del medico operante.
     * <p>
     * Prepara un'istruzione di inserimento {@code INSERT INTO}.
     * Converte l'oggetto {@link java.time.LocalDateTime} della prestazione in un {@link java.sql.Timestamp}
     * tramite {@code Timestamp.valueOf()} per renderlo digeribile dal driver di Postgres, normalizza l'SSN
     * del paziente e mappa i testi descrittivi ed il nome dell'Enum (usando {@code name()}) sugli indici del statement.
     * Invia infine i dati al database invocando {@code executeUpdate()}.
     * </p>
     *
     * @param prestazione l'atto o prestazione medica da salvare
     * @param medicoLogin l'username/login del medico incaricato dell'erogazione
     * @throws RuntimeException incapsula una {@link SQLException} se l'inserimento fallisce
     */
    @Override
    public void save(PrestazioneMedica prestazione, String medicoLogin) {
        String sql = "INSERT INTO prestazioni_mediche (medico_login, ssn_paziente, tipo, data_ora, descrizione, esito) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);
            stmt.setString(2, normalizeSsn(prestazione.getSsnPaziente()));
            stmt.setString(3, prestazione.getTipo().name());
            stmt.setTimestamp(4, Timestamp.valueOf(prestazione.getDataOra()));
            stmt.setString(5, prestazione.getDescrizione());
            stmt.setString(6, prestazione.getEsito());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della prestazione del medico " + medicoLogin, e);
        }
    }
    /**
     * Modifica l'esito diagnostico o il referto di una prestazione medica identificata univocamente dai suoi parametri chiave.
     * <p>
     * Esegue un comando di {@code UPDATE} condizionato. Il record esatto viene localizzato
     * combinando ben quattro chiavi nella clausola {@code WHERE} ("medico_login", "tipo", "data_ora" convertito in Timestamp).
     * Il metodo cattura il valore intero restituito da {@code executeUpdate()}: se questo è pari a zero, significa che nessuna riga
     * corrisponde ai criteri inseriti (es. lo slot orario è stato alterato) e lancia intenzionalmente una contromisura di
     * errore per prevenire un mancato aggiornamento silenzioso.
     * </p>
     *
     * @param prestazione l'oggetto contenente il referto aggiornato
     * @param medicoLogin la login del medico proprietario della prestazione
     * @throws RuntimeException incapsula una {@link SQLException} o segnala un fallimento logico se non viene aggiornato alcun record
     */
    @Override
    public void updateEsito(PrestazioneMedica prestazione, String medicoLogin) {
        String sql = "UPDATE prestazioni_mediche SET esito = ? WHERE medico_login = ? AND tipo = ? AND data_ora = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prestazione.getEsito());
            stmt.setString(2, medicoLogin);
            stmt.setString(3, prestazione.getTipo().name());
            stmt.setTimestamp(4, Timestamp.valueOf(prestazione.getDataOra()));

            int righeAggiornate = stmt.executeUpdate();
            if (righeAggiornate == 0) {
                throw new RuntimeException("Nessuna prestazione trovata per medico=" + medicoLogin
                        + ", tipo=" + prestazione.getTipo() + ", data_ora=" + prestazione.getDataOra()
                        + ": l'aggiornamento dell'esito non ha avuto effetto.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dell'esito della prestazione del medico " + medicoLogin, e);
        }
    }

    private String normalizeSsn(String ssn) {
        return (ssn == null || ssn.isBlank()) ? null : ssn.trim().toUpperCase();
    }
}