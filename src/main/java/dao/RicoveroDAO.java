package dao;

import model.Ricovero;

import java.util.List;

/**
 * Definisce le operazioni di persistenza per i ricoveri ospedalieri.
 * <p>
 * Un ricovero non ha un identificativo esposto a livello applicativo (a livello di schema
 * la riga ha una chiave generata automaticamente, ma il modello {@link Ricovero} non la
 * espone): le implementazioni identificano quindi un ricovero specifico tramite la coppia
 * (codice fiscale del paziente, data di inizio ricovero), che nella pratica è sufficiente
 * a distinguere ricoveri diversi, dato che uno stesso paziente non può avere due ricoveri
 * attivi nello stesso momento.
 * <p>
 * La registrazione e la chiusura di un ricovero comportano tipicamente anche un cambiamento
 * dello stato del letto associato (vedi {@link LettoDAO#updateStato(String, boolean)}): le
 * due operazioni vengono eseguite insieme all'interno della stessa transazione dal
 * controller, per garantire che non rimangano mai in uno stato incoerente l'una rispetto
 * all'altra.
 */
public interface RicoveroDAO {

    /**
     * Restituisce tutti i ricoveri (sia in corso che conclusi) di un determinato paziente,
     * costituendo il suo storico clinico ricostruito a partire dai ricoveri registrati.
     *
     * @param ssn il codice fiscale del paziente
     * @return la lista dei ricoveri del paziente, in nessun ordine garantito; lista vuota
     *         se il paziente non ha mai avuto ricoveri
     */
    List<Ricovero> findByPaziente(String ssn);

    /**
     * Restituisce tutti i ricoveri attualmente in corso, indipendentemente dal paziente.
     *
     * @return la lista dei ricoveri con stato "in corso"; lista vuota se non ce ne sono
     */
    List<Ricovero> findInCorso();

    /**
     * Registra un nuovo ricovero.
     *
     * @param ricovero il ricovero da salvare, comprensivo di letto assegnato (se presente),
     *                  diagnosi di entrata, ed eventuali campi opzionali (day hospital,
     *                  descrizione, terapia)
     */
    void save(Ricovero ricovero);

    /**
     * Aggiorna un ricovero esistente, individuato tramite la coppia (codice fiscale del
     * paziente, data di inizio ricovero) presente nell'oggetto passato.
     * <p>
     * Usato tipicamente per registrare la dimissione di un paziente (impostando data di
     * dimissione effettiva, diagnosi di uscita, terapia e stato "non più in corso"), ma può
     * aggiornare qualsiasi campo modificabile del ricovero.
     *
     * @param ricovero il ricovero con i dati aggiornati
     */
    void update(Ricovero ricovero);
}