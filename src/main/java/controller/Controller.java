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

	private Utente autentica(String u, String p) {
		Medico m = new Medico("medico", "pass", "Francesco", "Giordano");
		Amministratore a = new Amministratore("admin", "pass");
		if (m.login(u, p)) return m;
		if (a.login(u, p)) return a;
		return null;
	}

	public Controller(Login view) {
		this.view = view;

		this.view.getLoginButton().addActionListener(e -> {
			String u = view.getUsername();
			String p = view.getPassword();

			Utente utente = autentica(u, p);

			if (utente != null) {

				SwingUtilities.getWindowAncestor(view.getPanelLogin()).dispose();

				if (utente instanceof Medico) {
					apriMedico((Medico) utente);
				} else if (utente instanceof Amministratore) {
					apriAmministratore((Amministratore) utente);
				}
			} else {
				JOptionPane.showMessageDialog(view.getPanelLogin(), "Credenziali errate.");
			}
		});
	}

	private void apriMedico(Medico m) {
		JFrame frame = new JFrame("Dashboard Medico");
		InterfacciaMedico gui = new InterfacciaMedico();
		new MedicoController(gui, m);

		frame.setContentPane(gui.getPanelMedico());
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private void apriAmministratore(Amministratore a) {
		JFrame frame = new JFrame("Dashboard Amministratore");
		InterfacciaAmministratore gui2 = new InterfacciaAmministratore();
		new AdminController(gui2, a);

		frame.setContentPane(gui2.getPanelAmministratore());
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
}