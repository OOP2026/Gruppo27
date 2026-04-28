package model;

public class Amministratore extends Utente {

    public Amministratore(String login, String password) {
        super(login, password);
    }

    public void registraPaziente() {
        System.out.println("Registrando Paziente");
    }

    public void gestioneAnagraficaPaziente() {
        System.out.println("Modifica anagrafica Paziente");
    }

    public void dimissionePaziente(){
        System.out.println("Dimissione Paziente");
    }

    }