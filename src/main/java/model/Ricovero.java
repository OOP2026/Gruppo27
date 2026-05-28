package model;



import java.util.Date;

public class Ricovero {
    private final String ssn;
    private final Date dataRicovero;
    private Date dataDimissionePrevista;
    private Date dataDimissioneEffettiva;
    private Letto lettoAssegnato;
    private String diagnosiEntrata;
    private String diagnosiUscita;
    private boolean inCorso;
    private static final String DEFAULT_DIAGNOSI = "-";


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


    public String getSsn(){
        return ssn;
    }

    public Date getDataRicovero(){
        return dataRicovero;
    }

    public Date getDataDimissionePrevista(){ return dataDimissionePrevista; }
    public void setDataDimissionePrevista(Date dataDimissionePrevista){
        this.dataDimissionePrevista = dataDimissionePrevista; }
    public Date getDataDimissioneEffettiva(){
        return dataDimissioneEffettiva;
    }
    public void setDataDimissioneEffettiva(Date dataDimissioneEffettiva){
        this.dataDimissioneEffettiva = dataDimissioneEffettiva; }
    public Letto getLettoAssegnato(){ return lettoAssegnato; }
    public void setLettoAssegnato(Letto lettoAssegnato){
       this.lettoAssegnato = lettoAssegnato; }
    public String getDiagnosiEntrata(){ return diagnosiEntrata; }
    public void setDiagnosiEntrata(String diagnosiEntrata){
       this.diagnosiEntrata = diagnosiEntrata; }
    public String getDiagnosiUscita(){ return diagnosiUscita; }
    public void setDiagnosiUscita(String diagnosiUscita){
       this.diagnosiUscita = diagnosiUscita; }
    public boolean isInCorso(){ return inCorso; }
    public void setInCorso(boolean inCorso){ this.inCorso = inCorso; }


}
