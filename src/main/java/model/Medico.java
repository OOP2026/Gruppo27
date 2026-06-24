package model;


import java.time.LocalDateTime;
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
    public boolean isDisponibile(){
        return disponibile;
    }
    public void setDisponibile(boolean disponibile){
        this.disponibile = disponibile;
    }

    public boolean isPrestazioneValid(LocalDateTime dataPrestazione) {
        for (TurnoLavorativo turno : this.turni) {
            if (!dataPrestazione.isBefore(turno.getInizio()) &&
                    !dataPrestazione.isAfter(turno.getFine())) {
                return true;
            }
        }
        return false;
    }

    public List<TurnoLavorativo> getTurni(){return turni;}
}