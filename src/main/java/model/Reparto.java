package model;

import java.util.ArrayList;
import java.util.List;

public class Reparto {
    private String nome;
    private List<Stanza> stanze = new ArrayList<>();

    public Reparto(String nomeR){ this.nome = nomeR;}

    public void aggiungiStanza(Stanza stanza){ this.stanze.add(stanza);}

    public Stanza getStanza(int numero){
        for(Stanza stanza : stanze){
            if (stanza.getNumero() == numero){return stanza;}
        }
        return null;
    }

    public int disponibilitaLetti() {
        int lettiLiberi = 0;
        for (Stanza s : stanze) {
            lettiLiberi += s.getQuantitaLettiLiberi();
        }
        System.out.println("Nel reparto " + nome + " sono disponibili " + lettiLiberi + " letti liberi.");
        return lettiLiberi;
    }

    public int getQuantitaStanze(){ return this.stanze.size();}

    public String getNome(){ return nome;}
    public void setNome(String nomeO){ this.nome = nomeO;}

    }
