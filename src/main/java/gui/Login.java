package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JPanel panel1;
    private JPasswordField passwordField1;
    private JTextField textField1;
    private JButton loginButton;
    private JPanel panel2; //  pannello che userà l'immagine come sfondo

    public Login() {
        // gestione del click sul pulsante di Login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText().trim();
                String password = new String(passwordField1.getPassword());

                if (username.equals("admin") && password.equals("1234")) {
                    JOptionPane.showMessageDialog(panel1, "Login effettuato!");
                } else {
                    JOptionPane.showMessageDialog(panel1, "Credenziali errate.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    private void createUIComponents() {
        panel2 = new PanelImmagine("/logoFMAB.png");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Look and Feel di sistema moderno
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JFrame frame = new JFrame("Login System - FMAB");
                Login loginForm = new Login();

                frame.setContentPane(loginForm.panel1);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setResizable(false);
                frame.setLocationRelativeTo(null); // centra la finestra
                frame.setVisible(true);            // mostra la schermata
            }
        });
    }
}