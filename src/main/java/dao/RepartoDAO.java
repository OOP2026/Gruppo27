package dao;

import model.Reparto;

import java.util.List;
import java.util.Optional;

/**
 * Definisce le operazioni di persistenza per i reparti ospedalieri.
 * <p>
 * Un {@link Reparto} restituito da questo DAO è popolato con l'intera gerarchia strutturale
 * a esso associata (le sue {@link model.Stanza}, ciascuna con i propri {@link model.Letto}):
 * le implementazioni si occupano di ricostruire questa struttura annidata leggendo le
 * relative tabelle collegate, così il chiamante non deve interrogare DAO separati per
 * ottenere un quadro completo di un reparto.
 */
public interface RepartoDAO {

    /**
     * Cerca un reparto per numero identificativo, con stanze e letti già caricati.
     *
     * @param num il numero del reparto
     * @return un {@link Optional} con il reparto se trovato, {@link Optional#empty()} altrimenti
     */
    Optional<Reparto> findByNum(int num);

    /**
     * Restituisce tutti i reparti dell'ospedale, ciascuno con la propria struttura di
     * stanze e letti già popolata.
     *
     * @return la lista completa dei reparti; lista vuota se non ce ne sono
     */
    List<Reparto> findAll();

    /**
     * Crea un nuovo reparto.
     *
     * @param reparto il reparto da salvare (numero e nome)
     */
    void save(Reparto reparto);

    /**
     * Elimina un reparto.
     * <p>
     * A livello di schema, l'eliminazione di un reparto comporta la cancellazione a cascata
     * delle stanze e dei letti a esso associati.
     *
     * @param num il numero del reparto da eliminare
     */
    void delete(int num);
}