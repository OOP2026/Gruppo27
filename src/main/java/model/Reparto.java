package model;

import java.util.ArrayList;
import java.util.List;

public class Reparto {
    private int num;
    private String nome;
    private List<Stanza> stanze;

    public Reparto(int numero, String nomeR) {
        this.num = numero;
        this.nome = nomeR;
        this.stanze = new ArrayList<>();
    }

    public void aggiungiStanza(Stanza stanza) {
        this.stanze.add(stanza);
        System.out.println("Stanza creata");
}

    /*public Stanza getStanza(int numero){
        for(Stanza stanza : stanze){
            if (stanza.getNumero() == numero){return stanza;}
        }
        return null;
    }*/

    public void disponibilitaLetti() { System.out.println("Letti disponibili:"); }

    public int getQuantitaStanze(){ return this.stanze.size();}
    public int getNum(){ return num;}
    public String getNome(){ return nome;}
    public List<Stanza> getStanze(){ return stanze;}

    @Override
    public String toString() {
        return num + " - " + nome;
    }
    }
