package dao;

import model.PrestazioneMedica;
import model.Ricovero;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Definisce le operazioni di persistenza per le prestazioni mediche (visite, interventi,
 * esami) erogate da un medico a un paziente.
 * <p>
 * Una prestazione è sempre associata al medico che l'ha registrata; l'associazione al
 * paziente, tramite il suo codice fiscale, è opzionale a livello di modello (può non essere
 * specificata se il form da cui viene creata non la richiede), ma è necessaria per poter
 * determinare quale medico sia "assegnato" a un determinato ricovero (vedi
 * {@link #findMedicoAssegnato(String, Date)}).
 */
public interface PrestazioneMedicaDAO {

    /**
     * Restituisce tutte le prestazioni registrate da un medico.
     *
     * @param medicoLogin il login del medico
     * @return la lista delle prestazioni erogate dal medico; lista vuota se non ne ha registrate
     */
    List<PrestazioneMedica> findByMedico(String medicoLogin);

    /**
     * Determina il medico "assegnato" a un paziente per un determinato ricovero, individuato
     * come l'autore della prestazione più recente registrata per quel paziente, a partire
     * dalla data di inizio del ricovero in poi.
     * <p>
     * Prestazioni avvenute prima dell'inizio del ricovero specificato non vengono considerate,
     * per evitare di associare al ricovero corrente un medico relativo a un ricovero
     * precedente dello stesso paziente.
     * <p>
     * Per risolvere il medico assegnato di molti ricoveri contemporaneamente, preferire
     * {@link #findMedicoAssegnatoBatch(List)}, che evita di eseguire una query separata
     * per ciascun ricovero.
     *
     * @param ssnPaziente  il codice fiscale del paziente
     * @param dataRicovero la data di inizio del ricovero, usata come soglia: solo le
     *                     prestazioni da questa data in poi vengono considerate
     * @return un {@link Optional} con nome e cognome del medico assegnato, oppure
     *         {@link Optional#empty()} se non esiste nessuna prestazione successiva
     *         alla data di ricovero per quel paziente
     */
    Optional<String> findMedicoAssegnato(String ssnPaziente, Date dataRicovero);

    /**
     * Versione "batch" di {@link #findMedicoAssegnato(String, Date)}: risolve in una sola
     * query il medico assegnato per più ricoveri contemporaneamente, evitando di eseguire
     * una query separata per ciascuno (pattern N+1), tipicamente usato quando si deve
     * popolare una tabella con una riga per ricovero.
     *
     * @param ricoveri i ricoveri per cui si vuole conoscere il medico assegnato
     * @return una mappa codice fiscale del paziente → nome e cognome del medico assegnato.
     *         I pazienti per cui non è stata trovata nessuna prestazione successiva
     *         all'inizio del relativo ricovero semplicemente non compaiono come chiave
     *         nella mappa
     */
    Map<String, String> findMedicoAssegnatoBatch(List<Ricovero> ricoveri);

    /**
     * Registra una nuova prestazione medica.
     *
     * @param prestazione la prestazione da salvare (tipo, data/ora, descrizione, esito,
     *                    eventuale codice fiscale del paziente)
     * @param medicoLogin il login del medico che ha erogato la prestazione
     */
    void save(PrestazioneMedica prestazione, String medicoLogin);

    /**
     * Aggiorna l'esito di una prestazione già registrata, individuandola dalla combinazione
     * di medico, tipo e data/ora esatti presenti nell'oggetto passato.
     *
     * @param prestazione la prestazione con il nuovo esito da salvare
     * @param medicoLogin il login del medico che ha registrato la prestazione
     */
    void updateEsito(PrestazioneMedica prestazione, String medicoLogin);
}