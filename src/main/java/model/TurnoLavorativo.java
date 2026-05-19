package model;

import java.time.LocalDateTime;

public class TurnoLavorativo {
    public enum GiornoSettimana{LUNEDI, MARTEDI,MERCOLEDI, GIOVEDI, VENERDI, SABATO, DOMENICA};
    private GiornoSettimana giorno;
    private LocalDateTime inizio;
    private LocalDateTime fine;

    public TurnoLavorativo(GiornoSettimana giorno, LocalDateTime inizio, LocalDateTime fine){
        this.giorno = giorno;
        this.inizio = inizio;
        this.fine = fine;
    }

    public boolean check(){
        LocalDateTime ora = LocalDateTime.now();
        return !ora.isBefore(inizio) && !ora.isAfter(fine);
    }

    public GiornoSettimana getGiorno(){ return giorno;}
    public void setGiorno(GiornoSettimana giorno){ this.giorno = giorno;}

    public LocalDateTime getInizio(){ return inizio;}
    public void setInizio(LocalDateTime inizio){ this.inizio = inizio;}

    public LocalDateTime getFine(){ return fine;}
    public void setFine(LocalDateTime fine){ this.fine = fine;}
}