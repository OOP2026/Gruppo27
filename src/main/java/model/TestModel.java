package model;

import controller.Controller;
import gui.Login;
import javax.swing.*;

public class TestModel {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Login login = new Login();
			JFrame frame = new JFrame("Login");
			frame.setContentPane(login.getPanelLogin());
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setResizable(false);
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			new Controller(login);
		});
	}
}