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

    public boolean check(){
        if(true){
            System.out.println("Medico disponibile");
            return true;
        }else{
            System.out.println("Medico non disponibile");
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