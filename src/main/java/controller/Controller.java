package controller;

import gui.Login;
import model.Utente;
import javax.swing.*;

public class Controller {
	private Login view;
	private Utente model;

	public Controller(Login view, Utente model) {
		this.view = view;
		this.model = model;

		// trasformato in lamba , dava errore
		this.view.getLoginButton().addActionListener(e -> {
			String username = view.getUsername();
			String password = view.getPassword();


			if (model.login(username, password)) {
				JOptionPane.showMessageDialog(view.getPanel1(), "Login effettuato!");
			} else {
				JOptionPane.showMessageDialog(view.getPanel1(), "Credenziali errate.", "Errore", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}