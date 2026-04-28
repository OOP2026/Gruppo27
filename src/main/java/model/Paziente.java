package model;

public class Paziente {
    private final String cf;
    private String nome;
    private String cognome;
    private String recapito;

    public Paziente(String cf, String nome, String cognome, String recapito) {
        this.cf = cf;
        this.nome = nome;
        this.cognome = cognome;
        this.recapito = recapito;
    }

    public String getNome(){ return nome; }
    public void setNome(String nome){
        this.nome = nome;
    }

    public String getCognome(){
        return cognome;
    }
    public void setCognome(String cognome){
        this.cognome = cognome;
    }

    public String getCf(){
        return cf;
    }

    public String getRecapito(){
        return recapito;
    }
    public void setRecapito(String recapito){
        this.recapito = recapito;
    }
}
