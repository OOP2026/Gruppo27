package model;

import controller.Controller;
import gui.InterfacciaMedico;
import gui.Login;
import javax.swing.*;

public class TestModel {

	public static void main(String[] args) {
		Utente u = new Utente("topolino", "minni");
		Medico m = new Medico("Pippo", "Boy", "Michele", "Giordano");
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			JFrame frame = new JFrame("Login");

			Login loginForm = new Login();

			new Controller(loginForm, u);

			frame.setContentPane(loginForm.getPanelLogin());

			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			frame.pack();
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			JFrame frame1 = new JFrame("Medico");

			InterfacciaMedico medicoForm = new InterfacciaMedico();
			new Controller(medicoForm, m);

			frame1.setContentPane(medicoForm.getPanelMedico());

			frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			frame1.pack();
			//frame1.setResizable(false);
			frame1.setLocationRelativeTo(null);
			frame1.setVisible(true);
		});
	}
}