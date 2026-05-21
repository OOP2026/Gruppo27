package controller;

import gui.InterfacciaMedico;
import gui.Login;
import model.Medico;
import model.Utente;
import javax.swing.*;

public class Controller {
	private Login view;
	private Utente model;
	private InterfacciaMedico menu;
	private Medico m;

	public Controller(Login view, Utente model) {
		this.view = view;
		this.model = model;

		// trasformato in lamba, dava errore
		this.view.getLoginButton().addActionListener(e -> {
			String username = view.getUsername();
			String password = view.getPassword();


			if (model.login(username, password)) {
				JOptionPane.showMessageDialog(view.getPanelLogin(), "Login effettuato!");
			} else {
				JOptionPane.showMessageDialog(view.getPanelLogin(), "Credenziali errate.", "Errore", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public Controller(InterfacciaMedico menu, Medico m) {
		this.menu = menu;
		this.m = m;


	}
}