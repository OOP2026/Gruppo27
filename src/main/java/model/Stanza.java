package model;

import java.util.ArrayList;
import java.util.List;

public class Stanza {
    private int numero;
    private List<Letto> quantitaLetti = new ArrayList<>();

    public Stanza(int numero){ this.numero = numero;}
    public void aggiungiLetto(Letto letto){ this.quantitaLetti.add(letto);}

    public int getQuantitaLettiLiberi(){
        int liberi=0;
        for(Letto letto : quantitaLetti) {
            if (letto.isLibero()) {
                liberi++;
            }
        }
        return liberi;
    }

    public int getNumero(){ return numero;}
    public void setNumero(int numero) { this.numero = numero;}
}
