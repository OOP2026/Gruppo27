package controller;

import gui.InterfacciaAmministratore;
import gui.InterfacciaMedico;
import gui.Login;
import model.Amministratore;
import model.Medico;
import model.Utente;

import javax.swing.*;
import java.awt.Frame; // Aggiunto import per risolvere l'avviso di MAXIMIZED_BOTH
import java.util.ArrayList;
import java.util.List;

public class Controller {
	private Login view;
	private JFrame mainFrame;

	private final List<Utente> databaseUtentiMock;

	public Controller(Login view, JFrame mainFrame) {
		this.view = view;
		this.mainFrame = mainFrame;

		this.databaseUtentiMock = new ArrayList<>();
		this.databaseUtentiMock.add(new Medico("medico", "pass", "Francesco", "Giordano"));
		this.databaseUtentiMock.add(new Amministratore("admin", "pass"));

		this.view.setLoginListener(e -> gestisciLogin());
		this.view.setEnterListener(e -> gestisciLogin());

		this.mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		this.mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				int scelta = JOptionPane.showConfirmDialog(
						mainFrame,
						"Sei sicuro di voler uscire?",
						"Conferma Uscita",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
				);
				if (scelta == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

	}

	private void gestisciLogin() {
		String u = view.getUsername();
		String p = view.getPassword();
		Utente utenteLoggato = autentica(u, p);

		if (utenteLoggato != null) {
			instradaUtente(utenteLoggato);
		} else {
			JOptionPane.showMessageDialog(mainFrame, "Credenziali errate.", "Errore Login", JOptionPane.ERROR_MESSAGE);
		}
	}

	private Utente autentica(String username, String password) {

		for (Utente utente : databaseUtentiMock) {
			if (utente.login(username, password)) {
				return utente;
			}
		}
		return null;
	}

	private void instradaUtente(Utente utente) {

		if (utente instanceof Medico medico) {
			InterfacciaMedico gui = new InterfacciaMedico();

			new MedicoController(gui, medico, mainFrame);

			cambiaSchermata(gui.getPanelMedico(), "Dashboard Medico - " + medico.getCognome());

		} else if (utente instanceof Amministratore amministratore) {
			InterfacciaAmministratore gui = new InterfacciaAmministratore();

			new AdminController(gui, amministratore, mainFrame);

			cambiaSchermata(gui.getPanelAmministratore(), "Dashboard Amministratore");
		}
	}

	private void cambiaSchermata(JPanel nuovoPanel, String titolo) {
		mainFrame.setTitle(titolo);
		mainFrame.setContentPane(nuovoPanel);
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.revalidate();
		mainFrame.repaint();
		mainFrame.setResizable(true);
	}
}