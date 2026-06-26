package model;

import java.util.Date;
/**
 * Rappresenta un evento di degenza o di day hospital di un paziente all'interno della struttura.
 * <p>
 * Modella il ciclo di vita completo del ricovero ospedaliero, tracciando le date di ingresso,
 * le dimissioni previste ed effettive, il posto letto occupato e l'evoluzione della diagnosi e della terapia.
 * </p>
 */
public class Ricovero {
    private final String ssn;
    private final Date dataRicovero;
    private Date dataDimissionePrevista;
    private Date dataDimissioneEffettiva;
    private Letto lettoAssegnato;
    private String diagnosiEntrata;
    private String diagnosiUscita;
    private boolean inCorso;
    private boolean dayHospital;
    private String descrizione;
    private String terapia;
    private static final String DEFAULT_DIAGNOSI = "-";

    /**
     * Costruisce e attiva un nuovo evento di ricovero ospedaliero per un paziente.
     *
     * @param ssn                   il codice fiscale del paziente da ricoverare
     * @param dataRicovero          la data {@link Date} di ammissione e ingresso in reparto
     * @param dataDimissionePrevista la data stimata di rilascio del paziente
     * @param lettoAssegnato        il modello {@link Letto} bloccato e assegnato per la degenza
     * @param diagnosiEntrata       la descrizione della patologia riscontrata all'accettazione
     */
    public Ricovero(String ssn, Date dataRicovero, Date dataDimissionePrevista,
                    Letto lettoAssegnato, String diagnosiEntrata) {
        this.ssn                   = ssn;
        this.dataRicovero          = dataRicovero;
        this.dataDimissionePrevista = dataDimissionePrevista;
        this.lettoAssegnato        = lettoAssegnato;
        this.diagnosiEntrata       = diagnosiEntrata;
        this.diagnosiUscita        = DEFAULT_DIAGNOSI;
        this.inCorso               = true;
    }


    /** @return la stringa del codice fiscale del paziente */
    public String getSsn(){ return ssn; }

    /** @return la data di ammissione del ricovero */
    public Date getDataRicovero(){ return dataRicovero; }

    /** @return la data di dimissione prevista stimata */
    public Date getDataDimissionePrevista(){ return dataDimissionePrevista; }
    /** @param dataDimissionePrevista imposta o ricalcola la data di uscita programmata */
    public void setDataDimissionePrevista(Date dataDimissionePrevista){ this.dataDimissionePrevista = dataDimissionePrevista; }

    /** @return la data effettiva di rilascio, o null se il ricovero è ancora attivo */
    public Date getDataDimissioneEffettiva(){ return dataDimissioneEffettiva; }
    /** @param dataDimissioneEffettiva imposta la data in cui il paziente lascia la struttura */
    public void setDataDimissioneEffettiva(Date dataDimissioneEffettiva){ this.dataDimissioneEffettiva = dataDimissioneEffettiva; }

    /** @return l'oggetto {@link Letto} occupato dal paziente */
    public Letto getLettoAssegnato(){ return lettoAssegnato; }
    /** @param lettoAssegnato modifica o azzera il posto letto associato */
    public void setLettoAssegnato(Letto lettoAssegnato){ this.lettoAssegnato = lettoAssegnato; }

    /** @return il testo descrittivo della diagnosi d'ingresso */
    public String getDiagnosiEntrata(){ return diagnosiEntrata; }
    /** @param diagnosiEntrata aggiorna la diagnosi di accettazione */
    public void setDiagnosiEntrata(String diagnosiEntrata){ this.diagnosiEntrata = diagnosiEntrata; }

    /** @return il testo del referto di uscita */
    public String getDiagnosiUscita(){ return diagnosiUscita; }
    /** @param diagnosiUscita imposta la diagnosi finale alla dimissione */
    public void setDiagnosiUscita(String diagnosiUscita){ this.diagnosiUscita = diagnosiUscita; }

    /** @return true se il paziente si trova ancora in reparto, false se è già stato dimesso */
    public boolean isInCorso(){ return inCorso; }
    /** @param inCorso cambia lo stato logico di attività della degenza */
    public void setInCorso(boolean inCorso){ this.inCorso = inCorso; }

    /** @return true se il ricovero avviene in regime ambulatoriale di Day Hospital, false per degenza ordinaria */
    public boolean isDayHospital(){ return dayHospital; }
    /** @param dayHospital modifica il regime di degenza del ricovero */
    public void setDayHospital(boolean dayHospital){ this.dayHospital = dayHospital; }

    /** @return la stringa delle note descrittive o dei sintomi segnalati */
    public String getDescrizione(){ return descrizione; }
    /** @param descrizione imposta il testo delle note d'ingresso */
    public void setDescrizione(String descrizione){ this.descrizione = descrizione; }

    /** @return la terapia farmacologica o riabilitativa prescritta */
    public String getTerapia(){ return terapia; }
    /** @param terapia imposta le specifiche della cura da somministrare */
    public void setTerapia(String terapia){ this.terapia = terapia; }
}