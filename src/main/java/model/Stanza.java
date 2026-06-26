package model;

import java.util.ArrayList;
import java.util.List;
/**
 * Rappresenta una stanza fisica facente parte di un reparto dell'ospedale.
 * <p>
 * Mantiene l'indicazione del numero di stanza e aggrega al suo interno la collezione dei letti,
 * fornendo algoritmi per il computo dinamico della capacità ricettiva residua.
 * </p>
 */
public class Stanza {
    private int numero;
    private List<Letto> quantitaLetti;
    /**
     * Costruisce una Stanza assegnandole un numero identificativo e predisponendo la lista dei posti letto.
     *
     * @param numero il numero identificativo unico della stanza all'interno del reparto
     */
    public Stanza(int numero){
        this.numero = numero;
        this.quantitaLetti = new ArrayList<>();
    }
    /**
     * Aggiunge un posto letto al sotto-elenco della stanza.
     *
     * @param letto l'oggetto modello Letto da associare alla stanza
     */
    public void aggiungiLetto(Letto letto){ this.quantitaLetti.add(letto);}
    /**
     * Calcola e restituisce il numero di posti letto attualmente disponibili (non occupati) nella stanza.
     * <p>
     * Inizializza un contatore intero locale a zero. Avvia un ciclo condizionale {@code for-each} scansionando
     * iterativamente tutti gli elementi della lista {@code quantitaLetti}: per ogni letto, invoca il metodo di
     * controllo {@code letto.isLibero()}. Se il flag del letto risulta vero, incrementa il contatore di un'unità.
     * Al termine della scansione completa della lista, restituisce il totale accumulato.
     * </p>
     *
     * @return il numero intero di letti liberi e prenotabili nella stanza
     */
    public int getQuantitaLettiLiberi(){
        int liberi=0;
        for(Letto letto : quantitaLetti) {
            if (letto.isLibero()) {
                liberi++;
            }
        }
        return liberi;
    }
    /**
     * Restituisce la lista di tutti i letti allocati all'interno della stanza.
     *
     * @return una lista di oggetti {@link Letto}
     */
    public List<Letto> getLetti(){ return quantitaLetti;}
    /**
     * Restituisce il numero identificativo della stanza.
     *
     * @return il numero della stanza
     */
    public int getNumero(){ return numero;}
    /**
     * Modifica il numero identificativo della stanza.
     *
     * @param numero il nuovo numero di stanza da impostare
     */
    public void setNumero(int numero) { this.numero = numero;}
    /**
     * Converte l'istanza in formato stringa leggibile, utile per le viste ad albero o i titoli grafici.
     *
     * @return la stringa formattata "Stanza " seguita dal numero identificativo
     */
    @Override
    public String toString() {
        return "Stanza " + numero;
    }
}