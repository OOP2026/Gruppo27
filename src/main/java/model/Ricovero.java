package model;

import java.time.LocalDateTime;

public class Ricovero {
    private final String codice;
    private final LocalDateTime dataInizio;
    private LocalDateTime dimissionePrevista;
    private LocalDateTime dimissioneEffettiva;

    public Ricovero(String codice, LocalDateTime dataInizio){
        this.codice = codice;
        this.dataInizio = dataInizio;
    }
    public String getCodice(){
        return codice;
    }

    public LocalDateTime getDataInizio(){
        return dataInizio;
    }

    public LocalDateTime dimissionePrevista(){
        return dimissionePrevista;
    }
    public void setDimissionePrevista(LocalDateTime dimissionePrevista){
        this.dimissionePrevista = dimissionePrevista;
    }

    public LocalDateTime getDimissioneEffettiva(){
        return dimissioneEffettiva;
    }
    public void setDimissioneEffettiva(LocalDateTime dimissioneEffettiva){
        this.dimissioneEffettiva = dimissioneEffettiva;
    }
}
