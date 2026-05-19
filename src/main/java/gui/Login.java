package gui;

import javax.swing.*;

public class Login {
    // Lasciamo i componenti privati per incapsulamento
    private JPanel panel1;
    private JPasswordField passwordField1;
    private JTextField textField1;
    private JButton loginButton;
    private JPanel panel2;

    public Login() {
        //logica gestita dal controller, vuoto intenzionalmente
        //commento per togliere errore sonarqube
    }


    public JPanel getPanel1() {
        return panel1;
    }

    public String getUsername() {
        return textField1.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField1.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }


    private void createUIComponents() {
        panel2 = new PanelImmagine("/logoFMAB.png");
    }
}