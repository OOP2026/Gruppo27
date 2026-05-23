package model;


import java.util.ArrayList;
import java.util.List;

public class Medico extends Utente {
    private String nome;
    private String cognome;
    private boolean disponibile;
    private List<TurnoLavorativo> turni = new ArrayList<>();

    public Medico(String login, String password, String nome, String cognome){
        super(login, password);
        this.nome = nome;
        this.cognome = cognome;

    }public boolean registraPrestazione(PrestazioneMedica p){
        return true;
    }
    public void aggiungiTurno(TurnoLavorativo t){
        this.turni.add(t);
    }
    public void rimuoviTurno(TurnoLavorativo t){
        this.turni.remove(t);
    }

    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public String getCognome(){
        return cognome;
    }
    public void setCognome(String cognome){
        this.cognome = cognome;
    }
}