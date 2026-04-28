package model;

import java.util.Date;

public class TestModel {

	public static void main(String[] args) {
		Utente u = new Utente("topolino","minni");
		System.out.println(u.login("pippo","pluto"));
		System.out.println(u.login("topolino","minni"));
	}
}
