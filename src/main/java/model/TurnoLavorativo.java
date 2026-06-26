package model;

import java.time.LocalDateTime;
/**
 * Rappresenta uno slot orario di presenza o di reperibilità lavorativa pianificato per un medico.
 * <p>
 * Definisce il giorno della settimana (tramite Enum) e gli estremi temporali (inizio e fine) del servizio,
 * offrendo funzionalità di validazione rispetto all'orario corrente.
 * </p>
 */
public class TurnoLavorativo {
    public enum GiornoSettimana{LUNEDI, MARTEDI,MERCOLEDI, GIOVEDI, VENERDI, SABATO, DOMENICA};
    private GiornoSettimana giorno;
    private LocalDateTime inizio;
    private LocalDateTime fine;
    /**
     * Costruisce uno slot di TurnoLavorativo configurandone il giorno ed i limiti temporali precisi.
     *
     * @param giorno l'indicazione del giorno della settimana (Enum)
     * @param inizio l'oggetto {@link LocalDateTime} rappresentante l'avvio del turno
     * @param fine   l'oggetto {@link LocalDateTime} rappresentante il termine del turno
     */
    public TurnoLavorativo(GiornoSettimana giorno, LocalDateTime inizio, LocalDateTime fine){
        this.giorno = giorno;
        this.inizio = inizio;
        this.fine = fine;
    }

    /**
     * Restituisce il giorno della settimana associato al turno.
     *
     * @return la costante dell'Enum {@link GiornoSettimana}
     */
    public GiornoSettimana getGiorno(){ return giorno;}
    /**
     * Restituisce la data e l'ora di inizio del turno di servizio.
     *
     * @return l'oggetto {@link LocalDateTime} di avvio
     */
    public LocalDateTime getInizio(){ return inizio;}
    /**
     * Restituisce la data e l'ora di fine del turno di servizio.
     *
     * @return l'oggetto {@link LocalDateTime} di termine
     */
    public LocalDateTime getFine(){ return fine;}
}