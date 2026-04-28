package model;

import java.time.LocalDateTime;

public class Ricovero {
    private final String codice;
    private final LocalDateTime dataInizio;
    private LocalDateTime dimissionePrevista;
    private LocalDateTime dimissioneEffettiva;
    private final Paziente paziente;

    public Ricovero(String codice, LocalDateTime dataInizio, Paziente paziente) {
        this.codice = codice;
        this.dataInizio = dataInizio;
        this.paziente = paziente;
    }
    public String getCodice(){
        return codice;
    }
    public LocalDateTime getDataInizio(){
        return dataInizio;
    }

    public void dimissionePrevista(){
        System.out.println("Dimissione prevista: ");
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
    public Paziente getPaziente(){
        return paziente;
    }
}
