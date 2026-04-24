package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public String getDataFineFormattata() {
        if (dimissioneEffettiva == null) return "Non ancora dimesso";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dimissioneEffettiva.format(formatter);
    }
    public Paziente getPaziente(){
        return paziente;
    }
}
