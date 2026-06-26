package model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Rappresenta un profilo utente appartenente al personale Medico.
 * <p>
 * Estende {@link Utente} e aggrega la pianificazione dei turni di servizio e lo storico
 * delle prestazioni sanitarie erogate, fornendo algoritmi per il calcolo delle intersezioni orarie.
 * </p>
 */
public class Medico extends Utente {
    private String nome;
    private String cognome;
    private boolean disponibile;
    private List<TurnoLavorativo> turni = new ArrayList<>();
    private List<PrestazioneMedica> prestazioniErogate = new ArrayList<>();
    /**
     * Costruisce un'istanza Medico configurandone i dati anagrafici e le credenziali di accesso.
     *
     * @param login    l'username/login univoco del medico
     * @param password la chiave d'accesso (hashata) dell'account
     * @param nome     il nome del medico
     * @param cognome  il cognome del medico
     */
    public Medico(String login, String password, String nome, String cognome){
        super(login, password);
        this.nome = nome;
        this.cognome = cognome;
    }
    /**
     * Registra un atto clinico o un intervento all'interno del diario delle attività del medico.
     * <p>
     * Verifica preventivamente che l'oggetto passato non sia nullo per evitare anomalie e lo inserisce
     * nella lista dinamica {@code prestazioniErogate} tramite {@link List#add(Object)}.
     * </p>
     *
     * @param p la prestazione medica da refertare
     * @return true se la prestazione viene aggiunta con successo, false se l'oggetto in input è nullo
     */
    public boolean registraPrestazione(PrestazioneMedica p) {
        if (p != null) {
            return this.prestazioniErogate.add(p);
        }
        return false;
    }
    /**
     * Aggiunge un nuovo blocco di orario lavorativo al calendario dei turni del medico.
     *
     * @param t il {@link TurnoLavorativo} da pianificare
     */
    public void aggiungiTurno(TurnoLavorativo t){
        this.turni.add(t);
    }
    /**
     * Rimuove uno slot orario pianificato dal calendario dei turni del medico.
     *
     * @param t il {@link TurnoLavorativo} da cancellare
     */
    public void rimuoviTurno(TurnoLavorativo t){
        this.turni.remove(t);
    }
    /**
     * Restituisce il nome del medico.
     *
     * @return il nome testuale
     */
    public String getNome(){
        return nome;
    }
    /**
     * Modifica il nome del medico.
     *
     * @param nome il nuovo nome da impostare
     */
    public void setNome(String nome){
        this.nome = nome;
    }
    /**
     * Restituisce il cognome del medico.
     *
     * @return il cognome testuale
     */
    public String getCognome(){
        return cognome;
    }
    /**
     * Modifica il cognome del medico.
     *
     * @param cognome il nuovo cognome da impostare
     */
    public void setCognome(String cognome){
        this.cognome = cognome;
    }
    /**
     * Verifica lo stato di reperibilità o disponibilità del medico.
     *
     * @return true se disponibile, false altrimenti
     */
    public boolean isDisponibile(){
        return disponibile;
    }
    /**
     * Imposta lo stato di disponibilità generale del medico.
     *
     * @param disponibile true per impostare il medico come attivo/disponibile
     */
    public void setDisponibile(boolean disponibile){
        this.disponibile = disponibile;
    }
    /**
     * Restituisce la lista di tutte le prestazioni e referti firmati dal medico.
     *
     * @return una lista di tipo {@link List} contenente le prestazioni erogate
     */
    public List<PrestazioneMedica> getPrestazioniErogate() {
        return prestazioniErogate;
    }
    /**
     * Algoritmo di controllo che convalida l'erogazione di una prestazione in base ai turni del medico.
     * <p>
     * Calcola la fine teorica dello slot orario della prestazione aggiungendo un'ora e sottraendo un minuto
     * all'orario d'inizio (es. slot 08:00 scade alle 08:59). Successivamente, avvia un ciclo {@code for-each}
     * sulla lista dei turni assegnati al medico: per ogni turno, estrae i timestamp di inizio e fine turno e calcola
     * l'intersezione logica. Una prestazione è considerata valida se l'inizio del turno è antecedente o coincidente
     * alla fine dello slot *E* la fine del turno è posteriore o coincidente all'inizio dello slot. Se la condizione è soddisfatta,
     * interrompe immediatamente il ciclo e restituisce true; se nessun turno copre la fascia oraria, restituisce false.
     * </p>
     *
     * @param dataPrestazione l'orario {@link LocalDateTime} in cui si desidera pianificare la visita
     * @return true se la data cade all'interno di un turno lavorativo del medico, false in caso di mancata copertura oraria
     */
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
    /**
     * Restituisce il calendario completo dei turni lavorativi associati al medico.
     *
     * @return una lista di oggetti {@link TurnoLavorativo}
     */
    public List<TurnoLavorativo> getTurni(){return turni;}
}