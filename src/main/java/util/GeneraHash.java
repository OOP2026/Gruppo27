package util;


/**
 * Utility eseguibile a sé stante, non parte dell'applicazione, usata per generare offline
 * l'hash BCrypt di una password in chiaro da inserire manualmente in uno script SQL di seed
 * o in un INSERT diretto sulla tabella {@code utenti}.
 * <p>
 * Uso: modificare il valore della variabile {@code password} con quella desiderata, eseguire
 * la classe, e copiare la stringa stampata in console come valore della colonna
 * {@code password} nell'INSERT.
 * <p>
 * Non collegata a nessun flusso dell'applicazione: serve solo come comodità per chi scrive
 * gli script SQL, dato che {@link PasswordHasher#hash(String)} non può essere invocato
 * direttamente da uno script SQL.
 */
public class GeneraHash {
    public static void main(String[] args) {
        String password = "scriviQui";
        System.out.println(PasswordHasher.hash(password));
    }
}
