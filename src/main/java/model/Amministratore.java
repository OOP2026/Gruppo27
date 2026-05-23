package model;

public class Amministratore extends Utente {

    public Amministratore(String login, String password) {
        super(login, password);
    }

    public boolean registraPaziente(Paziente p) {
        return true; // Logica di inserimento
    }

    public boolean modificaAnagraficaPaziente(Paziente p) {
        return true;
    }

    public boolean dimettiPaziente(Paziente p){
        return true;
    }

    }