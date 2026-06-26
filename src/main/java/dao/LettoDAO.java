package dao;

import model.Letto;

import java.util.List;
import java.util.Optional;

/**
 * Definisce le operazioni di persistenza per i letti, identificati univocamente dal loro
 * codice di inventario.
 * <p>
 * Lo stato di occupazione di un letto ({@code libero}/{@code occupato}) è la parte più
 * delicata gestita da questo DAO: viene aggiornato ogni volta che un ricovero viene
 * registrato o un paziente viene dimesso, tipicamente all'interno della stessa transazione
 * con cui si salva o si aggiorna il relativo {@link model.Ricovero} (vedi
 * {@link database_connection.ConnessioneDatabase#eseguiInTransazione(Runnable)}), per evitare
 * che un letto resti segnato come occupato senza un ricovero collegato, o viceversa.
 */
public interface LettoDAO {

    /**
     * Cerca un letto per codice di inventario.
     *
     * @param codiceInventario il codice del letto da cercare
     * @return un {@link Optional} con il letto se trovato, {@link Optional#empty()} altrimenti
     */
    Optional<Letto> findByCodice(String codiceInventario);

    /**
     * Restituisce tutti i letti presenti in una determinata stanza.
     *
     * @param numeroStanza il numero della stanza
     * @param repartoNum   il numero del reparto a cui la stanza appartiene
     * @return la lista dei letti della stanza; lista vuota se la stanza non ha letti
     */
    List<Letto> findByStanza(int numeroStanza, int repartoNum);

    /**
     * Crea un nuovo letto all'interno di una stanza.
     *
     * @param letto        il letto da salvare (codice di inventario e stato iniziale)
     * @param numeroStanza il numero della stanza in cui si trova il letto
     * @param repartoNum   il numero del reparto a cui la stanza appartiene
     */
    void save(Letto letto, int numeroStanza, int repartoNum);

    /**
     * Aggiorna lo stato di occupazione di un letto.
     *
     * @param codiceInventario il codice del letto da aggiornare
     * @param libero           {@code true} per segnarlo come libero, {@code false} come occupato
     */
    void updateStato(String codiceInventario, boolean libero);

    /**
     * Elimina un letto.
     *
     * @param codiceInventario il codice del letto da eliminare
     */
    void delete(String codiceInventario);
}