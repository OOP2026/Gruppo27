package model;

import java.util.ArrayList;
import java.util.List;

public class Reparto {
    private String nome;
    private List<Stanza> stanze = new ArrayList<>();

    public Reparto(String nomeR) {
        this.nome = nomeR;
    }

    public void creaStanza() {
    System.out.println("Stanza creata");
}

    public Stanza getStanza(int numero){
        for(Stanza stanza : stanze){
            if (stanza.getNumero() == numero){return stanza;}
        }
        return null;
    }

    public void disponibilitaLetti() { System.out.println("Letti disponibili:"); }

    public int getQuantitaStanze(){ return this.stanze.size();}

    public String getNome(){ return nome;}
    public void setNome(String nomeO){ this.nome = nomeO;}

    }
