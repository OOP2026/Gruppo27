package model;

import java.util.ArrayList;
import java.util.List;

public class Stanza {
    private int numero;
    private List<Letto> quantitaLetti = new ArrayList<>();

    public Stanza(int numero){ this.numero = numero;}

    public int getQuantitaLetti(){ return this.quantitaLetti.size();}

    public int getNumero(){ return numero;}
    public void setNumero(int numero) { this.numero = numero;}
}
