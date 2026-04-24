package model;

import java.time.LocalDateTime;

public class TurnoLavorativo {
    public enum GiornoSettimana{Lunedì, Martedì, Mercoledì, Giovedì, Venerdì};
    private GiornoSettimana giorno;
    private LocalDateTime inizio;
    private LocalDateTime fine;

    public TurnoLavorativo(GiornoSettimana giorno, LocalDateTime inizio, LocalDateTime fine){
        this.giorno = giorno;
        this.inizio = inizio;
        this.fine = fine;
    }

    public boolean check(GiornoSettimana g, LocalDateTime i, LocalDateTime f){
        if(g == giorno && i == inizio && f == fine){
            System.out.println("Il medico è presente in ospedale");
            return true;
        } else{
            System.out.println("Il medico è assente");
            return false;
        }
    }

    public GiornoSettimana getGiorno(){ return giorno;}
    public void setGiorno(GiornoSettimana giorno){ this.giorno = giorno;}

    public LocalDateTime getInizio(){ return inizio;}
    public void setInizio(LocalDateTime inizio){ this.inizio = inizio;}

    public LocalDateTime getFine(){ return fine;}
    public void setFine(LocalDateTime fine){ this.fine = fine;}
}