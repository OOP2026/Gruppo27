package model;

public class Letto {
    private String codiceInventario;
    private boolean statoAttuale;

    public Letto(String codice, boolean stato){
        this.codiceInventario = codice;
        this.statoAttuale = stato;
    }

    public boolean checkStato(boolean statoAttuale){
        if(statoAttuale == true){
            System.out.println("Il letto è occupato");
        } else{
            System.out.println("Il letto è libero");
        }
        return statoAttuale;
    }
    public void setStatoAttuale(boolean stato){ this.statoAttuale = stato;}

    public String getCodiceInventario(){ return codiceInventario;}
    public void setCodiceInventario(String codice){ this.codiceInventario = codice;}
}
