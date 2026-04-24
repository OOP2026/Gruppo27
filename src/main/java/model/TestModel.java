package model;

public class TestModel {

	public static void main(String[] args) {
		Utente u = new Utente("topolino","minni");
		System.out.println(u.login("pippo","pluto"));
		System.out.println(u.login("topolino","minni"));
		Letto n1 = new	Letto("FG2E1020", false);
		Letto n2 = new Letto("DF22992", false);
		Reparto A = new Reparto("Oculistica");
	}

}
