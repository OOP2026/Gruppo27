package model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Medico extends Utente {
    private String nome;
    private String cognome;
    private boolean disponibile;
    private List<TurnoLavorativo> turni = new ArrayList<>();
    private List<PrestazioneMedica> prestazioniErogate = new ArrayList<>();

    public Medico(String login, String password, String nome, String cognome){
        super(login, password);
        this.nome = nome;
        this.cognome = cognome;

    }public boolean registraPrestazione(PrestazioneMedica p) {
        if (p != null) {
            return this.prestazioniErogate.add(p);
        }
        return false;
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
    public List<PrestazioneMedica> getPrestazioniErogate() {
        return prestazioniErogate;
    }

    public boolean isPrestazioneValid(LocalDateTime dataPrestazione) {

        LocalDateTime fineSlotOrario = dataPrestazione.plusHours(1).minusMinutes(1);

        for (TurnoLavorativo turno : this.turni) {
            LocalDateTime inizioTurno = turno.getInizio();
            LocalDateTime fineTurno = turno.getFine();

            boolean isIntersezione = (inizioTurno.isBefore(fineSlotOrario) || inizioTurno.isEqual(fineSlotOrario)) &&
                    (fineTurno.isAfter(dataPrestazione) || fineTurno.isEqual(dataPrestazione));

            if (isIntersezione) {
                return true;
            }
        }
        return false;
    }

    public List<TurnoLavorativo> getTurni(){return turni;}
}