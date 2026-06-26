package model;

import java.time.LocalDateTime;
/**
 * Rappresenta un atto clinico, una visita specialistica o un intervento chirurgico eseguito da un medico.
 * <p>
 * Contiene i dati relativi alla tipologia di prestazione (definita tramite Enum), alla marcatura temporale
 * di esecuzione, all'esito diagnostico testuale e ai riferimenti del paziente.
 * </p>
 */
public class PrestazioneMedica {
    public enum Prestazione { INTERVENTO_CHIRURGICO, VISITA}
    private Prestazione tipo;
    private LocalDateTime dataOra;
    private String esito;
    private String ssnPaziente;
    private String descrizione;
    /**
     * Costruisce una prestazione medica registrandone la classificazione, la data e l'esito iniziale.
     *
     * @param tipo    la tipologia di atto sanitario (Enum)
     * @param dataora la marcatura temporale {@link LocalDateTime} dell'evento
     * @param esito   il referto diagnostico iniziale o lo stato dell'esame
     */
    public PrestazioneMedica(Prestazione tipo, LocalDateTime dataora, String esito) {
        this.dataOra = dataora;
        this.esito = esito;
        this.tipo = tipo;

    }
    /**
     * Imposta o aggiorna il testo del referto diagnostico.
     *
     * @param esito la stringa contenente l'esito o le note del medico
     */
    public void setEsito(String esito) { this.esito = esito;}
    /**
     * Restituisce l'esito diagnostico o le note cliniche registrate per la prestazione.
     *
     * @return la stringa dell'esito
     */
    public String getEsito(){ return this.esito; }
    /**
     * Configura la tipologia di prestazione medica.
     *
     * @param tipo il nuovo valore di tipo {@link Prestazione} da assegnare
     */
    public void setTipo(Prestazione tipo){ this.tipo = tipo;}
    /**
     * Restituisce la tipologia della prestazione medica.
     *
     * @return la costante dell'Enum {@link Prestazione}
     */
    public Prestazione getTipo(){ return tipo;}
    /**
     * Modifica la data e l'ora di pianificazione/esecuzione dell'atto.
     *
     * @param dataOra l'oggetto {@link LocalDateTime} da impostare
     */
    public void setDataOra(LocalDateTime dataOra){ this.dataOra = dataOra;}
    /**
     * Restituisce la data e l'ora in cui è avvenuta o è prevista la prestazione.
     *
     * @return l'oggetto {@link LocalDateTime} associato
     */
    public LocalDateTime getDataOra(){ return dataOra;}
    /**
     * Collega la prestazione clinica al codice fiscale del paziente ricevente.
     *
     * @param ssnPaziente la stringa del codice fiscale (SSN) del paziente
     */
    public void setSsnPaziente(String ssnPaziente){ this.ssnPaziente = ssnPaziente; }
    /**
     * Restituisce il codice fiscale del paziente associato alla prestazione.
     *
     * @return la stringa del codice fiscale (SSN), o null se non ancora agganciato
     */
    public String getSsnPaziente(){ return ssnPaziente; }
    /**
     * Imposta una descrizione testuale estesa dell'intervento o della visita.
     *
     * @param descrizione il testo descrittivo dei dettagli clinici
     */
    public void setDescrizione(String descrizione){ this.descrizione = descrizione; }
    /**
     * Restituisce la descrizione dettagliata della prestazione.
     *
     * @return la stringa della descrizione
     */
    public String getDescrizione(){ return descrizione; }
}
