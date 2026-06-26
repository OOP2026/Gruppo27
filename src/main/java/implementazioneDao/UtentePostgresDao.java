package implementazioneDao;

import dao.UtenteDAO;
import database_connection.ConnessioneDatabase;
import model.Amministratore;
import model.Medico;
import model.Utente;
import util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Implementazione Postgres dell'interfaccia UtenteDAO.
 * Gestisce l'autenticazione, la sicurezza tramite hashing delle password e la scomposizione
 * polimorfica delle categorie utente (Medici/Amministratori) in fase di mapping.
 */
public class UtentePostgresDao implements UtenteDAO {
    /**
     * Ricerca un utente sul database tramite username per convalidare le procedure di login.
     * <p>
     * Esegue una query {@code SELECT} mirata filtrando sulla colonna della
     * chiave primaria "login". Se viene riscontrata corrispondenza, il record viene intercettato dal cursore
     * ed elaborato dal metodo privato di scomposizione ed instradamento polimorfico {@code mappaUtente(rs)},
     * che restituisce l'oggetto modello corretto racchiuso all'interno di un contenitore di sicurezza {@link Optional}.
     * </p>
     *
     * @param login la stringa di username inserita dall'utente da cercare
     * @return un Optional contenente l'oggetto Utente (o sottoclasse) se registrato, altrimenti un Optional vuoto
     * @throws RuntimeException incapsula una {@link SQLException} in caso di errori di lettura dal database
     */
    @Override
    public Optional<Utente> findByLogin(String login) {
        String sql = "SELECT * FROM utenti WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mappaUtente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca dell'utente con login " + login, e);
        }

        return Optional.empty();
    }
    /**
     * Estrae la lista completa di tutti i profili utente configurati nel sistema.
     * <p>
     * Il metodo effettua un'estrazione totale mediante {@code SELECT * FROM utenti}.
     * Attraverso un costrutto ciclico {@code while(rs.next())}, intercetta ogni riga, ne demanda la scomposizione
     * polimorfica al metodo {@code mappaUtente(rs)} e accumula gli oggetti istanziati in una lista {@link ArrayList}.
     * </p>
     *
     * @return una lista contenente tutti i profili Utente mappati presenti nel database
     * @throws RuntimeException incapsula una {@link SQLException} se il recupero massivo fallisce
     */
    @Override
    public List<Utente> findAll() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utenti";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                utenti.add(mappaUtente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero degli utenti", e);
        }

        return utenti;
    }
    /**
     * Registra una nuova utenza nel database, applicando la cifratura irreversibile sulla password.
     * <p>
     * Il metodo prepara un comando d'inserimento {@code INSERT INTO}.
     * Per garantire i requisiti di sicurezza e non salvare credenziali in chiaro, intercetta il testo della password
     * e lo cifra richiamando la classe di utilità crittografica {@link PasswordHasher#hash(String)}.
     * Successivamente, analizza polimorficamente il tipo specifico dell'oggetto tramite l'operatore {@code instanceof}:
     * <ul>
     * <li>Se l'utente è un **Medico**, imposta la stringa di ruolo a "MEDICO", estrae nome, cognome e lo stato
     * booleano di disponibilità di servizio per mapparlo sul statement.</li>
     * <li>Se l'utente è un **Amministratore**, imposta la stringa di ruolo a "AMMINISTRATORE", compila forzatamente
     * a {@code null} le colonne anagrafiche non necessarie indicando il tipo SQL {@link Types#VARCHAR} e imposta la
     * disponibilità di default a true.</li>
     * </ul>
     * Rende persistente la riga invocando {@code executeUpdate()}.
     * </p>
     *
     * @param utente l'oggetto modello Utente (o sua sottoclasse) contenente i dati in chiaro da inserire
     * @throws RuntimeException         incapsula una {@link SQLException} se la chiave utente risulta già esistente
     * @throws IllegalArgumentException se l'istanza passata appartiene a una tipologia di utenza non supportata
     */
    @Override
    public void save(Utente utente) {
        // NOTA: questo metodo presuppone che utente.getPassword() contenga la password in CHIARO
        // (es. un Utente appena creato in fase di registrazione). Viene hashata qui prima di salvarla.
        String sql = "INSERT INTO utenti (login, password, ruolo, nome, cognome, disponibile) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utente.getLogin());
            stmt.setString(2, PasswordHasher.hash(utente.getPassword()));

            if (utente instanceof Medico medico) {
                stmt.setString(3, "MEDICO");
                stmt.setString(4, medico.getNome());
                stmt.setString(5, medico.getCognome());
                stmt.setBoolean(6, medico.isDisponibile());
            } else if (utente instanceof Amministratore) {
                stmt.setString(3, "AMMINISTRATORE");
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
                stmt.setBoolean(6, true);
            } else {
                throw new IllegalArgumentException("Tipo di utente non supportato: " + utente.getClass());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'utente " + utente.getLogin(), e);
        }
    }
    /**
     * Aggiorna esclusivamente i dati anagrafici e lo stato di disponibilità del profilo utente selezionato, escludendo la password.
     * <p>
     * Esegue una direttiva {@code UPDATE} condizionata dall'username. Per evitare di compromettere
     * o ri-hashare accidentalmente stringhe crittografate già presenti a sistema, la colonna password viene deliberatamente esclusa
     * dalla query. Il metodo controlla il tipo di istanza: se individua un Medico aggiorna i dati e la reperibilità, altrimenti
     * imposta a {@code null} le colonne anagrafiche per gli amministratori, preservando l'integrità dei record.
     * </p>
     *
     * @param utente l'oggetto Utente contenente le modifiche da salvare nel database
     * @throws RuntimeException incapsula una {@link SQLException} in caso di fallimento di rete o vincoli SQL
     */
    @Override
    public void update(Utente utente) {
        // NOTA: questo metodo aggiorna solo i dati anagrafici, MAI la password,
        // per evitare di rihashare accidentalmente un hash già salvato.

        String sql = "UPDATE utenti SET nome = ?, cognome = ?, disponibile = ? WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (utente instanceof Medico medico) {
                stmt.setString(1, medico.getNome());
                stmt.setString(2, medico.getCognome());
                stmt.setBoolean(3, medico.isDisponibile());
            } else {
                stmt.setNull(1, Types.VARCHAR);
                stmt.setNull(2, Types.VARCHAR);
                stmt.setBoolean(3, true);
            }

            stmt.setString(4, utente.getLogin());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dell'utente " + utente.getLogin(), e);
        }
    }
    /**
     * Modifica e sovrascrive in sicurezza la credenziale di accesso (password) applicando l'hashing crittografico.
     * <p>
     * Il metodo isola l'aggiornamento della credenziale eseguendo una query di {@code UPDATE} dedicata.
     * Prende il parametro stringa in chiaro ricevuto in input, ne esegue l'offuscamento irreversibile tramite la classe
     * {@link PasswordHasher#hash(String)} e aggiorna in modo mirato la colonna "password" filtrando per l'username dell'account.
     * </p>
     *
     * @param login                 l'username identificativo dell'account da aggiornare
     * @param nuovaPasswordInChiaro la nuova stringa di testo digitata dall'utente
     * @throws RuntimeException incapsula una {@link SQLException} se l'operazione fallisce
     */
    @Override
    public void updatePassword(String login, String nuovaPasswordInChiaro) {
        String sql = "UPDATE utenti SET password = ? WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordHasher.hash(nuovaPasswordInChiaro));
            stmt.setString(2, login);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento della password dell'utente " + login, e);
        }
    }
    /**
     * Elimina in modo permanente un account utente tramite username.
     * <p>
     * Invia ed esegue un comando {@code DELETE FROM utenti} condizionato,
     * specificando l'username identificativo come parametro restrittivo della clausola {@code WHERE} per eliminare la riga associata.
     * </p>
     *
     * @param login l'identificativo dell'utenza da rimuovere dal sistema
     * @throws RuntimeException incapsula una {@link SQLException} se l'operazione viene respinta per violazione di vincoli relazionali
     */
    @Override
    public void delete(String login) {
        String sql = "DELETE FROM utenti WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione dell'utente " + login, e);
        }
    }
    /**
     * Discrimina il ruolo testuale memorizzato sul record ed istanzia il corretto modello polimorfico (Medico o Amministratore).
     * <p>
     * Estrae il valore stringa memorizzato nella colonna "ruolo". Effettua un controllo condizionale:
     * se il testo equivale esattamente a "MEDICO", estrae le colonne d'anagrafica dedicate ("nome", "cognome", "disponibile") e richiama
     * il costruttore della classe {@link Medico}. In tutti gli altri casi (ruolo "AMMINISTRATORE"), istanzia e restituisce un oggetto
     * modello di tipo {@link Amministratore}, garantendo il corretto comportamento polimorfico del software.
     * </p>
     */
    private Utente mappaUtente(ResultSet rs) throws SQLException {
        String login = rs.getString("login");
        String password = rs.getString("password");
        String ruolo = rs.getString("ruolo");

        if ("MEDICO".equals(ruolo)) {
            Medico medico = new Medico(login, password, rs.getString("nome"), rs.getString("cognome"));
            medico.setDisponibile(rs.getBoolean("disponibile"));
            return medico;
        }

        return new Amministratore(login, password);
    }
}
