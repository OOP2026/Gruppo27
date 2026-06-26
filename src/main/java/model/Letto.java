package model;
/**
 * Rappresenta un letto dislocato in una stanza di un reparto ospedaliero.
 * <p>
 * Tiene traccia del codice identificativo unico del cespite e del suo stato cromatico/logico
 * di disponibilità (libero o occupato da un paziente ricoverato).
 * </p>
 */
public class Letto {
    private final String codiceInventario;
    private boolean statoAttuale;
    /**
     * Costruisce un oggetto Letto inizializzandone i parametri di identificazione e disponibilità.
     * <p>
     * Assegna direttamente i parametri ricevuti in input alle variabili d'istanza interne del modulo.
     * </p>
     *
     * @param codice il codice identificativo unico stampato sul letto (es. codice inventario)
     * @param stato  lo stato di disponibilità iniziale (true per disponibile/libero, false per occupato)
     */
    public Letto(String codice, boolean stato){
        this.codiceInventario = codice;
        this.statoAttuale = stato;
    }
    /**
     * Aggiorna lo stato logico di disponibilità corrente del letto.
     *
     * @param stato true per contrassegnare il letto come libero, false per impostarlo come occupato
     */
    public void setStatoAttuale(boolean stato){ this.statoAttuale = stato;}
    /**
     * Restituisce il codice identificativo unico del letto.
     *
     * @return la stringa contenente il codice d'inventario
     */
    public String getCodiceInventario(){ return codiceInventario;}
    /**
     * Verifica se il letto è correntemente disponibile per ospitare un nuovo ricovero.
     * <p>
     * Restituisce il valore nativo della variabile booleana {@code statoAttuale},
     * dove true indica l'assenza di un paziente assegnato.
     * </p>
     *
     * @return true se il letto è disponibile, false se risulta occupato
     */
    public boolean isLibero(){ return this.statoAttuale;}
    /**
     * Converte l'istanza del letto in formato stringa per la visualizzazione nei componenti grafici.
     * <p>
     * Sovrascrive il metodo {@link Object#toString()} per restituire direttamente il codice d'inventario
     * del letto, permettendo alle JComboBox di visualizzarlo correttamente.
     * </p>
     *
     * @return il codice d'inventario del letto
     */
    @Override
    public String toString() {
        return codiceInventario;
    }

}
