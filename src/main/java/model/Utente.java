package model;

import util.PasswordHasher;

public class Utente {
    private String login;
    private String password;

    public Utente(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public boolean login(String login, String password) {
        return (login.equals(this.login) && PasswordHasher.verifica(password, this.password));
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
