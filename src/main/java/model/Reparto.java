package model;

import java.util.ArrayList;
import java.util.List;

public class Reparto {
    private String nome;
    private List<Stanza> stanze = new ArrayList<>();
    private List<Letto> letti = new ArrayList<>();

    public Reparto(String nomeR){ this.nome = nomeR;}
    public int getQuantitaStanze(){ return this.stanze.size();}

    public String getNome(){ return nome;}
    public void setNome(String nomeO){ this.nome = nomeO;}

    public int disponibilitaLetti(){
        System.out.println("Nel reparto "+ nome +" sono disponibili " + this.letti.size());
        return this.letti.size();
    }
}
