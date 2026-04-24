package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TestModel {

	public static void main(String[] args) {
		Utente u = new Utente("topolino","minni");
		System.out.println(u.login("pippo","pluto"));
		System.out.println(u.login("topolino","minni"));

		Letto n1 = new	Letto("FG2E1020", false);
		Letto n2 = new Letto("DF22992", false);

		Reparto pediatrico = new Reparto("Pediatrico");
		Stanza ingresso= new Stanza(100);
		Stanza bambini= new Stanza(101);
		pediatrico.aggiungiStanza(ingresso);
		pediatrico.aggiungiStanza(bambini);
		pediatrico.getStanza(100);
		System.out.println(pediatrico.getQuantitaStanze());
		bambini.aggiungiLetto(n1);
		bambini.aggiungiLetto(n2);
		System.out.println(bambini.getQuantitaLettiLiberi());
		Reparto B = new Reparto("Ortopedia");
		Stanza attesa= new Stanza(100);
		B.getStanza(100);
		B.getStanza(101);
		System.out.println(B.getQuantitaStanze());

		System.out.println("Gestione Ospedale");

		Amministratore admin= new Amministratore("admin", "1234");

		Medico dottore = new Medico("giord", "4321","Francesco","Giordano");

		Paziente paziente = new Paziente("CODICEFISCALE", "Michele","Gada","recapito");

		Ricovero ric = admin.registraPaziente(paziente, pediatrico, n1);

		if(ric!= null){

			System.out.println("Stato letto: "+(n1.isLibero() ? "Libero" : "Occupato"));

			dottore.aggiungiTurnO(LocalDateTime.now().plusDays(1)); // aggiunge turno domani
			dottore.visualizzaTurni();
			dottore.registraPrestazione(paziente, "Visita di controllo");

			admin.gestioneAnagraficaPaziente(paziente,"San","Pellegrino","nuovoRecapito");
			System.out.println("Il paziente ora e "+ paziente.getNome()+" "+ paziente.getCognome()+" "+paziente.getRecapito());

			System.out.println("Dimissione");
			admin.dimissionePaziente(ric, n1);

			System.out.println("Stato letto dopo dimissione: "+(n1.isLibero() ? "Libero" : "Occupato"));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			System.out.println("Data fine ricovero "+ric.getDimissioneEffettiva().format(formatter));
		}
		pediatrico.disponibilitaLetti();
	}

}
