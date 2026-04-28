package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Medico extends Utente {
    private String nome;
    private String cognome;
    private boolean disponibile;
    private List<LocalDateTime> turni = new ArrayList<>();

    public Medico(String login, String password, String nome, String cognome){
        super(login, password);
        this.nome = nome;
        this.cognome = cognome;
    }
    public void registraPrestazione(){
        System.out.println("Prestazione registrato");
    }
    public void visualizzaTurni(){
        System.out.println("Turni Dottore "+ cognome);
    }
    public void aggiungiTurno(){
        System.out.println("Turno aggiunto");
    }

    public void rimuoviTurno(){
        System.out.println("Turno rimosso");
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