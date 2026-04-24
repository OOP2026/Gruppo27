package model;

public class TestModel {

	public static void main(String[] args) {
		Utente u = new Utente("topolino","minni");
		System.out.println(u.login("pippo","pluto"));
		System.out.println(u.login("topolino","minni"));
		Letto n1 = new	Letto("FG2E1020", true);
		Letto n2 = new Letto("DF22992", false);
		Reparto A = new Reparto("Pediatrico");
		Stanza ingresso= new Stanza(100);
		Stanza bambini= new Stanza(101);
		A.aggiungiStanza(ingresso);
		A.aggiungiStanza(bambini);
		A.getStanza(100);
		System.out.println(A.getQuantitaStanze());
		bambini.aggiungiLetto(n1);
		bambini.aggiungiLetto(n2);
		System.out.println(bambini.getQuantitaLettiLiberi());
		Reparto B = new Reparto("Ortopedia");
		Stanza attesa= new Stanza(100);
		B.getStanza(100);
		B.getStanza(101);
		System.out.println(B.getQuantitaStanze());
		A.disponibilitaLetti();
	}

}
