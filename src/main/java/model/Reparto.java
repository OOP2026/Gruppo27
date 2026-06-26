package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un reparto della struttura ospedaliera (es. Cardiologia, Chirurgia).
 * <p>
 * Questa classe funge da nodo principale nell'organizzazione logistica, aggregando
 * al suo interno una collezione dinamica di stanze e offrendo metodi per il calcolo delle capacità.
 * </p>
 */
public class Reparto {
    private int num;
    private String nome;
    private List<Stanza> stanze;
    /**
     * Costruisce un Reparto ospedaliero assegnandogli un codice numerico, un nome e inizializzando la lista delle stanze.
     *
     * @param numero il codice numerico identificativo unico del reparto
     * @param nomeR  il nome testuale descrittivo del reparto
     */
    public Reparto(int numero, String nomeR) {
        this.num = numero;
        this.nome = nomeR;
        this.stanze = new ArrayList<>();
    }
    /**
     * Aggrega una nuova stanza all'interno del reparto ospedaliero.
     * <p>
     * Inserisce l'oggetto stanza ricevuto come parametro
     * nella lista dinamica interna sfruttando il metodo {@link List#add(Object)}.
     * </p>
     *
     * @param stanza l'oggetto {@link Stanza} da associare al reparto
     */
    public void aggiungiStanza(Stanza stanza) {
        this.stanze.add(stanza);
}
    /**
     * Restituisce il numero totale di stanze attualmente configurate ed attive nel reparto.
     * <p>
     * <b>Meccanismo di funzionamento:</b> Calcola la cardinalità della collezione richiamando
     * il metodo {@link List#size()} sulla lista delle stanze.
     * </p>
     *
     * @return il conteggio intero delle stanze aggregate
     */
    public int getQuantitaStanze(){ return this.stanze.size();}
    /**
     * Restituisce il codice numerico identificativo del reparto.
     *
     * @return il numero del reparto
     */
    public int getNum(){ return num;}
    /**
     * Restituisce il nome del reparto.
     *
     * @return la stringa del nome
     */
    public String getNome(){ return nome;}
    /**
     * Restituisce l'elenco completo di tutte le stanze facenti parte del reparto.
     *
     * @return una lista di oggetti {@link Stanza}
     */
    public List<Stanza> getStanze(){ return stanze;}
    /**
     * Converte il reparto in formato testuale standardizzato per i componenti grafici (come le JComboBox).
     *
     * @return la stringa formattata descrittiva del reparto
     */
    @Override
    public String toString() {
        return num + " - " + nome;
    }
}