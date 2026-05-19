package model;

import controller.Controller;
import gui.Login;
import javax.swing.*;

public class TestModel {

	public static void main(String[] args) {
		Utente u = new Utente("topolino", "minni");

		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			JFrame frame = new JFrame("Login System - FMAB");

			Login loginForm = new Login();

			new Controller(loginForm, u);

			frame.setContentPane(loginForm.getPanel1());

			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			frame.pack();
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}