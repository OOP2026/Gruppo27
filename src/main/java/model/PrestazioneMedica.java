package model;

import java.time.LocalDateTime;

public class PrestazioneMedica {
    public enum Prestazione { INTERVENTO_CHIRURGICO, VISITA}
    private Prestazione tipo;
    private LocalDateTime dataOra;
    private String esito;
    private String ssnPaziente;
    private String descrizione;

    public PrestazioneMedica(Prestazione tipo, LocalDateTime dataora, String esito) {
        this.dataOra = dataora;
        this.esito = esito;
        this.tipo = tipo;

    }

    public void setEsito(String esito) { this.esito = esito;}
    public String getEsito(){ return this.esito; }

    public void setTipo(Prestazione tipo){ this.tipo = tipo;}
    public Prestazione getTipo(){ return tipo;}

    public void setDataOra(LocalDateTime dataOra){ this.dataOra = dataOra;}
    public LocalDateTime getDataOra(){ return dataOra;}

    public void setSsnPaziente(String ssnPaziente){ this.ssnPaziente = ssnPaziente; }
    public String getSsnPaziente(){ return ssnPaziente; }

    public void setDescrizione(String descrizione){ this.descrizione = descrizione; }
    public String getDescrizione(){ return descrizione; }
}
