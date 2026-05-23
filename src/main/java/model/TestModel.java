package model;

import controller.Controller;
import gui.Login;
import javax.swing.*;

public class TestModel {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame mainFrame = new JFrame("Login");
			mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			mainFrame.setResizable(false);

			Login loginView = new Login();
			mainFrame.setContentPane(loginView.getPanelLogin());
			mainFrame.pack();
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setVisible(true);

			new Controller(loginView, mainFrame);
		});
	}
}