package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Amministratore extends Utente {
    public Amministratore(String login, String password) {
        super(login, password);
    }

    public Ricovero registraPaziente(Paziente p, Reparto r, Letto l) {
        if (r.disponibilitaLetti() > 0 && l != null && l.isLibero()) {

            Ricovero nuovoRicovero = new Ricovero("Ricovero-" + p.getCf(), LocalDateTime.now(), p);
            l.setStatoAttuale(true);

            System.out.println("Paziente " +p.getNome()+ " " + p.getCognome() + " registrato.");
            System.out.println("Ricovero creato con codice: " + nuovoRicovero.getCodice());
            return nuovoRicovero;
        }
        System.out.println("Errore: Impossibile registrare il paziente (Reparto pieno o Letto non valido).");
        return null;
    }

    public void gestioneAnagraficaPaziente(Paziente p, String nuovoNome, String nuovoCognome, String nuovoRecapito) {
        System.out.println("Modifica anagrafica del paziente CF:" + p.getCf());
        p.setNome(nuovoNome);
        p.setCognome(nuovoCognome);
        p.setRecapito(nuovoRecapito);
    }

    public void dimissionePaziente(Ricovero ric, Letto l) {
        if (ric != null && l != null) {
            ric.setDimissioneEffettiva(LocalDateTime.now());
            l.setStatoAttuale(false);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = ric.getDimissioneEffettiva().format(formatter);
            System.out.println("Ricovero " + ric.getCodice() + " concluso il: " +dataFormattata);
            System.out.println("Letto " + l.getCodiceInventario() + " liberato.");
        } else {
            System.out.println("Errore: Dati mancanti per la dimissione.");
        }
    }
}