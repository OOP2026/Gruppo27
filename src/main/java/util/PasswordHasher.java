package util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String passwordInChiaro) {
        if (passwordInChiaro == null) {
            throw new IllegalArgumentException("La password da hashare non può essere null.");
        }
        return BCrypt.hashpw(passwordInChiaro, BCrypt.gensalt());
    }

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