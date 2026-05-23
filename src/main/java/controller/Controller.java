package controller;

import gui.InterfacciaAmministratore;
import gui.InterfacciaMedico;
import gui.Login;
import model.Amministratore;
import model.Medico;
import model.Utente;
import javax.swing.*;
import java.awt.*;

public class Controller {
	private Login view;
	private JFrame mainFrame;

	private Utente autentica(String u, String p) {
		Medico m = new Medico("medico", "pass", "Francesco", "Giordano");
		Amministratore a = new Amministratore("admin", "pass");
		if (m.login(u, p)) return m;
		if (a.login(u, p)) return a;
		return null;
	}

	public Controller(Login view, JFrame mainFrame) {
		this.view = view;
		this.mainFrame = mainFrame;
		this.mainFrame.getRootPane().setDefaultButton(this.view.getLoginButton());

		this.view.getLoginButton().addActionListener(e -> {
			String u = view.getUsername();
			String p = view.getPassword();
			Utente utente = autentica(u, p);

			if (utente != null) {
				if (utente instanceof Medico) {
					apriMedico((Medico) utente);
				} else if (utente instanceof Amministratore) {
					apriAmministratore((Amministratore) utente);
				}
			} else {
				JOptionPane.showMessageDialog(mainFrame, "Credenziali errate.", "Errore Login", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private void apriMedico(Medico m) {
		InterfacciaMedico gui = new InterfacciaMedico();
		new MedicoController(gui, m);
		cambiaSchermata(gui.getPanelMedico(), "Dashboard Medico");
	}

	private void apriAmministratore(Amministratore a) {
		InterfacciaAmministratore gui = new InterfacciaAmministratore();
		new AdminController(gui, a, mainFrame);
		cambiaSchermata(gui.getPanelAmministratore(), "Dashboard Amministratore");
	}
	private void cambiaSchermata(JPanel nuovoPanel, String titolo) {
		mainFrame.setTitle(titolo);
		mainFrame.setContentPane(nuovoPanel);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.revalidate();
		mainFrame.repaint();
	}
}