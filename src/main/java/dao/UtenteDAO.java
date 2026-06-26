package dao;

import model.Utente;

import java.util.List;
import java.util.Optional;

/**
 * Definisce le operazioni di persistenza per gli utenti del sistema (Medico e Amministratore,
 * entrambe sottoclassi di {@link Utente}).
 * <p>
 * È il DAO usato per l'autenticazione: {@link #findByLogin(String)} è il punto di accesso
 * principale durante il login, dove l'oggetto {@link Utente} restituito viene poi interrogato
 * con {@link Utente#login(String, String)} per verificare la password.
 * <p>
 * Le implementazioni sono responsabili di determinare il tipo concreto da istanziare
 * (Medico o Amministratore) in base al ruolo salvato, dato che questa interfaccia lavora
 * sul tipo base {@link Utente}.
 */
public interface UtenteDAO {

    /**
     * Cerca un utente per login, indipendentemente dal suo ruolo.
     *
     * @param login il login da cercare
     * @return un {@link Optional} contenente l'utente (istanza concreta di Medico o
     *         Amministratore) se trovato, oppure {@link Optional#empty()} se nessun utente
     *         ha quel login
     */
    Optional<Utente> findByLogin(String login);

    /**
     * Restituisce tutti gli utenti registrati nel sistema, sia medici che amministratori.
     *
     * @return la lista completa degli utenti; lista vuota se non ce ne sono
     */
    List<Utente> findAll();

    /**
     * Salva un nuovo utente, determinando il ruolo da persistere in base al tipo concreto
     * dell'oggetto passato (Medico o Amministratore).
     * <p>
     * La password deve essere passata in chiaro tramite {@link Utente#getPassword()}:
     * l'implementazione si occupa di applicare l'hashing prima di scriverla a database.
     * Questo metodo non deve essere usato per aggiornare un utente già esistente che porta
     * con sé una password già hashata (altrimenti l'hash verrebbe ri-hashato, rompendo
     * il login) — per quel caso vedere {@link #update(Utente)} e {@link #updatePassword}.
     *
     * @param utente l'utente da creare, con la password in chiaro
     */
    void save(Utente utente);

    /**
     * Aggiorna i dati anagrafici di un utente già esistente (nome, cognome, disponibilità
     * per i medici), individuandolo tramite il suo login.
     * <p>
     * Questo metodo non modifica mai la password, anche se l'oggetto passato ne contiene
     * una: per cambiare la password usare {@link #updatePassword(String, String)}.
     *
     * @param utente l'utente con i nuovi dati anagrafici da salvare
     */
    void update(Utente utente);

    /**
     * Cambia la password di un utente esistente, individuato tramite il login.
     *
     * @param login                 il login dell'utente di cui cambiare la password
     * @param nuovaPasswordInChiaro la nuova password in chiaro; viene hashata
     *                              dall'implementazione prima di essere salvata
     */
    void updatePassword(String login, String nuovaPasswordInChiaro);

    /**
     * Elimina un utente dal sistema.
     *
     * @param login il login dell'utente da eliminare
     */
    void delete(String login);
}