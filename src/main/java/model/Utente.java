package model;

import util.PasswordHasher;

/**
 * Superclasse del modello preposta alla gestione delle credenziali di sicurezza e dell'autenticazione.
 * <p>
 * Fornisce le proprietà di base per gli account utente del sistema (medici ed amministratori)
 * ed i meccanismi di verifica delle chiavi d'accesso basati su algoritmi di hashing crittografico.
 * </p>
 */
public class Utente {
    private String login;
    private String password;
    /**
     * Costruisce un profilo Utente configurandone i dati di accesso fondamentali.
     *
     * @param login    l'username o stringa identificativa univoca di login dell'account
     * @param password la chiave d'accesso protetta (hashata) memorizzata a sistema
     */
    public Utente(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * Svolge il processo di autenticazione confrontando le credenziali digitate con quelle memorizzate.
     * <p>
     * Esegue una verifica condizionale composta. Controlla inizialmente l'uguaglianza testuale esatta dell'username
     * inserito con quello dell'istanza ({@code login.equals(this.login)}). Per motivi di sicurezza, la password immessa
     * in chiaro non viene mai confrontata direttamente: il metodo invoca la funzione statica di utilità crittografica
     * {@link PasswordHasher#verifica(String, String)}, passando la password digitata in chiaro e l'hash di sicurezza
     * memorizzato nel campo {@code this.password}. Se entrambi i controlli (username e corrispondenza crittografica
     * della password) hanno successo, restituisce true, convalidando l'accesso.
     * </p>
     *
     * @param login    l'username digitato dall'utente nella maschera di accesso
     * @param password la password in chiaro digitata dall'utente nella maschera di accesso
     * @return true se le credenziali corrispondono autorizzando il login, false in caso di credenziali errate
     */
    public boolean login(String login, String password) {
        return (login.equals(this.login) && PasswordHasher.verifica(password, this.password));
    }
    /**
     * Restituisce la stringa di login (username) dell'utente.
     *
     * @return l'identificativo di login
     */
    public String getLogin() {
        return login;
    }
    /**
     * Restituisce la stringa crittografata (hash) della password dell'utente.
     *
     * @return la password hashata
     */
    public String getPassword() {
        return password;
    }
}