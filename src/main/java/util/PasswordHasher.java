package util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String passwordInChiaro) {
        return BCrypt.hashpw(passwordInChiaro, BCrypt.gensalt());
    }

    public static boolean verifica(String passwordInChiaro, String passwordHashata) {
        return BCrypt.checkpw(passwordInChiaro, passwordHashata);
    }
}
