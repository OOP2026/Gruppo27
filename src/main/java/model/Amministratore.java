package model;
/**
 * Rappresenta un utente con privilegi amministrativi all'interno del sistema ospedaliero.
 * <p>
 * Questa classe estende {@link Utente} e definisce i metodi di sbarramento logici per le
 * operazioni CRUD sui pazienti e per le procedure amministrative di ammissione e rilascio.
 * </p>
 */
public class Amministratore extends Utente {
    /**
     * Costruisce un nuovo profilo Amministratore richiamando il costruttore della superclasse.
     * <p>
     * Inizializza le credenziali di accesso (username e password)
     * delegando l'assegnazione dei campi privati direttamente a {@link Utente#Utente(String, String)}
     * tramite l'istruzione {@code super}.
     * </p>
     *
     * @param login    l'identificativo di login univoco dell'amministratore
     * @param password la chiave d'accesso dell'account
     */
    public Amministratore(String login, String password) {
        super(login, password);
    }
}