package model;

public class Letto {
    private String codiceInventario;
    private boolean statoAttuale;

    public Letto(String codice, boolean stato){
        this.codiceInventario = codice;
        this.statoAttuale = stato;
    }
    public void setStatoAttuale(boolean stato){ this.statoAttuale = stato;}
    public String getCodiceInventario(){ return codiceInventario;}
    public void setCodiceInventario(String codice){ this.codiceInventario = codice;}

    public boolean isLibero(){ return !this.statoAttuale;}

    public boolean checkStato(){
        System.out.println("Check stato");
        return this.statoAttuale;
    }

}
