package dao;

import model.Paziente;

import java.util.List;
import java.util.Optional;

/**
 * Definisce le operazioni di persistenza per i pazienti, identificati univocamente dal
 * codice fiscale ({@code cf}).
 * <p>
 * Le implementazioni normalizzano il codice fiscale in maiuscolo prima di ogni ricerca o
 * scrittura, in modo che operazioni come {@link #findByCf(String)} funzionino correttamente
 * indipendentemente da come l'utente abbia digitato il codice (maiuscolo, minuscolo o misto).
 */
public interface PazienteDAO {

    /**
     * Cerca un paziente per codice fiscale.
     *
     * @param cf il codice fiscale da cercare; il confronto non è case-sensitive
     * @return un {@link Optional} con il paziente se trovato, {@link Optional#empty()} altrimenti
     */
    Optional<Paziente> findByCf(String cf);

    /**
     * Restituisce l'intera anagrafica dei pazienti registrati.
     *
     * @return la lista di tutti i pazienti; lista vuota se non ce ne sono
     */
    List<Paziente> findAll();

    /**
     * Inserisce un nuovo paziente in anagrafica.
     *
     * @param paziente il paziente da salvare
     */
    void save(Paziente paziente);

    /**
     * Aggiorna i dati anagrafici (nome, cognome, recapito) di un paziente già esistente,
     * individuato tramite il suo codice fiscale.
     *
     * @param paziente il paziente con i dati aggiornati; il suo {@code cf} identifica la riga
     *                 da modificare e non viene mai cambiato da questa operazione
     */
    void update(Paziente paziente);

    /**
     * Elimina un paziente dall'anagrafica.
     * <p>
     * A livello di schema, l'eliminazione di un paziente con ricoveri associati comporta
     * la cancellazione a cascata dello storico clinico collegato: per questo motivo i
     * controller dell'applicazione bloccano preventivamente questa operazione se il paziente
     * ha ricoveri registrati, piuttosto che lasciare che la cancellazione avvenga in modo
     * silenzioso.
     *
     * @param cf il codice fiscale del paziente da eliminare
     */
    void delete(String cf);
}