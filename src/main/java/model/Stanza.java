package model;

import java.util.ArrayList;
import java.util.List;

public class Stanza {
    private int numero;
    private List<Letto> quantitaLetti;

    public Stanza(int numero){
        this.numero = numero;
        this.quantitaLetti = new ArrayList<>();
    }

    public void aggiungiLetto(Letto letto){ this.quantitaLetti.add(letto);}

    //Restituisce il numero di letti liberi nella stanza
    public int getQuantitaLettiLiberi(){
        int liberi=0;
        for(Letto letto : quantitaLetti) {
            if (letto.isLibero()) {
                liberi++;
            }
        }
        return liberi;
    }

    public List<Letto> getLetti(){ return quantitaLetti;} //Restituisce tutti i letti
    public int getNumero(){ return numero;}
    public void setNumero(int numero) { this.numero = numero;}

    @Override
    public String toString() {
        return "Stanza " + numero;
    }
}
