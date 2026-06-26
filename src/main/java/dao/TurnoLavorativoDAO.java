package dao;

import model.TurnoLavorativo;

import java.util.List;

/**
 * Definisce le operazioni di persistenza per i turni di lavoro del personale medico.
 * <p>
 * Un turno è sempre associato a un medico tramite il suo login: l'associazione è gestita
 * a livello di persistenza (colonna di collegamento nella tabella dei turni), non come
 * riferimento diretto all'interno della classe {@link TurnoLavorativo}, che modella solo
 * giorno della settimana e orari di inizio/fine.
 * <p>
 * Non avendo un identificativo proprio esposto dal modello, un turno specifico viene
 * individuato dalle implementazioni tramite la combinazione di tutti i suoi campi
 * (medico, giorno, inizio, fine).
 */
public interface TurnoLavorativoDAO {

    /**
     * Restituisce tutti i turni di lavoro assegnati a un medico.
     *
     * @param medicoLogin il login del medico
     * @return la lista dei turni del medico; lista vuota se non ne ha
     */
    List<TurnoLavorativo> findByMedico(String medicoLogin);

    /**
     * Assegna un nuovo turno a un medico.
     *
     * @param turno       il turno da salvare (giorno, inizio, fine)
     * @param medicoLogin il login del medico a cui assegnarlo
     */
    void save(TurnoLavorativo turno, String medicoLogin);

    /**
     * Rimuove un turno precedentemente assegnato a un medico, individuandolo dalla
     * combinazione esatta di giorno, inizio e fine.
     *
     * @param turno       il turno da rimuovere
     * @param medicoLogin il login del medico a cui il turno è assegnato
     */
    void delete(TurnoLavorativo turno, String medicoLogin);
}