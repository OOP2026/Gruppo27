package dao;

import model.Stanza;

import java.util.List;

/**
 * Definisce le operazioni di persistenza per le stanze di un reparto.
 * <p>
 * Una stanza è identificata, all'interno di un reparto, dalla coppia
 * (numero stanza, numero reparto): lo stesso numero di stanza può quindi esistere in
 * reparti diversi senza ambiguità. Per questo motivo {@code repartoNum} è sempre richiesto
 * esplicitamente, anche se in alcuni casi sarebbe implicito guardando l'oggetto
 * {@link Stanza} stesso (che non conosce il proprio reparto).
 * <p>
 * Una {@link Stanza} restituita da questo DAO viene popolata anche con i propri letti,
 * recuperati internamente tramite {@link LettoDAO}.
 */
public interface StanzaDAO {

    /**
     * Restituisce tutte le stanze di un reparto, ciascuna con il proprio elenco di letti
     * già caricato.
     *
     * @param repartoNum il numero del reparto di cui recuperare le stanze
     * @return la lista delle stanze del reparto; lista vuota se il reparto non ha stanze
     *         o non esiste
     */
    List<Stanza> findByReparto(int repartoNum);

    /**
     * Crea una nuova stanza all'interno di un reparto.
     *
     * @param stanza     la stanza da salvare (il suo numero)
     * @param repartoNum il numero del reparto a cui la stanza appartiene
     */
    void save(Stanza stanza, int repartoNum);

    /**
     * Elimina una stanza.
     * <p>
     * A livello di schema, l'eliminazione di una stanza comporta la cancellazione a cascata
     * dei letti a essa associati.
     *
     * @param numero     il numero della stanza da eliminare
     * @param repartoNum il numero del reparto a cui la stanza appartiene
     */
    void delete(int numero, int repartoNum);
}