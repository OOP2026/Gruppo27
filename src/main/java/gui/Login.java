package gui;

import javax.swing.*;
import java.awt.event.ActionListener;
/**
 * Schermata iniziale di autenticazione dell'applicazione.
 * Raccoglie i dati inseriti dall'utente (username e password) inoltrandoli al controller di sicurezza.
 */
public class Login {
    // Lasciamo i componenti privati per incapsulamento
    private JPanel panelLogin;
    private JPasswordField passwordField1;
    private JTextField textField1;
    private JButton loginButton;
    private JPanel panel1;

    /**
     * Costruisce la form di Login. I processi di validazione sono interamente demandati al controller.
     */
    public Login() {
        //logica gestita dal controller, vuoto intenzionalmente
    }

    /**
     * Restituisce il pannello grafico principale della schermata di Login.
     *
     * @return l'istanza di JPanel associata al login
     */
    public JPanel getPanelLogin() {
        return panelLogin;
    }
    /**
     * Estrae l'identificativo utente inserito rimuovendo gli spazi bianchi in eccesso.
     *
     * @return la stringa contenente l'username inserito
     */
    public String getUsername() {
        return textField1.getText().trim();
    }
    /**
     * Estrae la sequenza di caratteri inserita all'interno del campo password.
     *
     * @return la stringa contenente la password digitata
     */
    public String getPassword() {
        return new String(passwordField1.getPassword());
    }
    /**
     * Restituisce il pulsante di Login.
     *
     * @return il componente JButton login
     */
    public JButton getLoginButton() {
        return loginButton;
    }
    /**
     * Gestisce la creazione custom del componente grafico del logo societario o istituzionale.
     */
    private void createUIComponents() {
        panel1 = new PanelImmagine("/logoFMAB.png");
    }
    /**
     * Registra un ActionListener sul pulsante di Login.
     *
     * @param listener il gestore degli eventi {@link ActionListener} fornito dal controller
     */
    public void setLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
    /**
     * Associa un listener per catturare la pressione del tasto 'Invio' direttamente all'interno del campo password.
     * <p>
     * Registra il listener sulla casella {@code passwordField1}. In questo modo, quando
     * l'utente preme il tasto 'Invio' sulla tastiera dopo aver digitato la password, Swing scatena automaticamente l'evento
     * velocizzando l'accesso senza costringere a cliccare il pulsante con il mouse.
     * </p>
     *
     * @param listener il gestore degli eventi {@link ActionListener} fornito dal controller
     */
    public void setEnterListener(ActionListener listener) {
        passwordField1.addActionListener(listener);
    }
}