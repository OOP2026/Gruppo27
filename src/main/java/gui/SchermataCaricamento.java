package gui;

import javax.swing.*;
import java.awt.*;

public class SchermataCaricamento extends JDialog {

    private JLabel lblBenvenuto;
    private JLabel lblMessaggio;
    private JProgressBar progressBar;

    public SchermataCaricamento(JFrame parent) {
        super(parent, "Caricamento", true);
        setUndecorated(true);
        setSize(350, 130);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setBackground(Color.WHITE);
        JPanel panelTesto = new JPanel(new GridLayout(2, 1, 0, 5));
        panelTesto.setBackground(Color.WHITE);
        lblBenvenuto = new JLabel("", SwingConstants.CENTER);
        lblBenvenuto.setFont(new Font("Arial", Font.BOLD, 16));
        lblBenvenuto.setForeground(new Color(25, 26, 28));
        lblMessaggio = new JLabel("Verifica credenziali...", SwingConstants.CENTER);
        lblMessaggio.setFont(new Font("Arial", Font.PLAIN, 12));
        panelTesto.add(lblBenvenuto);
        panelTesto.add(lblMessaggio);
        panel.add(panelTesto, BorderLayout.NORTH);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.CENTER);

        add(panel);
    }
    public void mostraBenvenuto(String identificativo) {
        String id = (identificativo != null) ? identificativo : "";
        lblBenvenuto.setText("Benvenuto " + id);
        lblBenvenuto.paintImmediately(lblBenvenuto.getVisibleRect());
    }
    public void setMessaggio(String nuovoMessaggio) {
        lblMessaggio.setText(nuovoMessaggio);
        lblMessaggio.paintImmediately(lblMessaggio.getVisibleRect());
    }
    public void nascondiBarra() {
        progressBar.setVisible(false);
        this.getRootPane().paintImmediately(0, 0, getWidth(), getHeight());
    }
}