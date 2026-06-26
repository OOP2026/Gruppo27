package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Fornisce metodi statici per la gestione sicura delle password tramite l'algoritmo BCrypt
 * (libreria org.mindrot.jbcrypt). In conformità con le best practice di sicurezza, le password
 * non vengono mai salvate né confrontate in chiaro: questa classe si occupa di trasformarle in
 * un hash unidirezionale al momento della creazione di un account, e di verificarne la
 * corrispondenza al momento del login, senza mai dover "decifrare" l'hash salvato (operazione
 * impossibile per costruzione con BCrypt).
 * <p>
 * Ogni hash generato incorpora automaticamente un salt casuale, quindi password identiche
 * produrranno hash diversi a ogni chiamata di {@link #hash(String)}.
 */

public final class PasswordHasher {

    private PasswordHasher() {
    }

    /**
     * Genera l'hash BCrypt di una password in chiaro, da salvare nel database al posto della
     * password originale.
     *
     * @param passwordInChiaro la password in chiaro da proteggere; non può essere null
     * @return l'hash BCrypt risultante (formato {@code $2a$<cost>$<salt><hash>})
     * @throws IllegalArgumentException se {@code passwordInChiaro} è null
     */

    public static String hash(String passwordInChiaro) {
        if (passwordInChiaro == null) {
            throw new IllegalArgumentException("La password da hashare non può essere null.");
        }
        return BCrypt.hashpw(passwordInChiaro, BCrypt.gensalt());
    }

    /**
     * Verifica se una password in chiaro corrisponde a un hash BCrypt precedentemente generato,
     * tipicamente quello recuperato dal database durante il login.
     * <p>
     * Se l'hash salvato non è in un formato BCrypt valido (es. dato corrotto o non ancora
     * migrato da un sistema precedente), il metodo restituisce {@code false} invece di
     * propagare un'eccezione, trattando il caso come autenticazione fallita.
     *
     * @param passwordInChiaro la password digitata dall'utente
     * @param passwordHashata  l'hash BCrypt con cui confrontarla
     * @return {@code true} se la password corrisponde all'hash, {@code false} altrimenti
     *         (incluso il caso in cui uno dei due parametri sia null o l'hash non sia valido)
     */

    public static boolean verifica(String passwordInChiaro, String passwordHashata) {
        if (passwordInChiaro == null || passwordHashata == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(passwordInChiaro, passwordHashata);
        } catch (IllegalArgumentException e) {
            // L'hash salvato non è in un formato BCrypt valido (es. dato corrotto o non ancora migrato):
            // trattiamo questo caso come autenticazione fallita, non come errore di sistema.
            return false;
        }
    }
}