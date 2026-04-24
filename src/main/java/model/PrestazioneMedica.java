package model;

import java.time.LocalDateTime;

public class PrestazioneMedica {
    public enum Prestazione{InterventoChirurgico, Visita}
    private Prestazione tipo;
    private LocalDateTime dataOra;
    private String esito;

    public PrestazioneMedica(Prestazione tipo, LocalDateTime dataora, String esito) {
        this.dataOra = dataora;
        this.esito = esito;
        this.tipo = tipo;
    }

    public void setEsito(String esito) { this.esito = esito;}
    public String getEsito(String esito){ return esito;}

    public void setTipo(Prestazione tipo){ this.tipo = tipo;}
    public Prestazione getTipo(){ return tipo;}

    public void setDataOra(LocalDateTime dataOra){ this.dataOra = dataOra;}
    public LocalDateTime getDataOra(){ return dataOra;}
}
