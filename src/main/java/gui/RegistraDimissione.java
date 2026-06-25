package gui;
import com.toedter.calendar.JDateChooser;
import controller.AdminController;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.util.Date;

public class RegistraDimissione extends JDialog {
    private JPanel mainPanel;
    private JPanel datePanel;
    private JDateChooser dimissioneEffetivaChooser;
    private JTextField ssnField;
    private JTextArea esitoTextArea;
    private JTextArea terapiaTextArea;
    private JButton salvaButton;
    private JButton annullaButton;
    private JPanel DataPrevista;
    private boolean confermato = false;

    public RegistraDimissione(JFrame parent, Date dataRicovero, String ssnPaziente) {
        super(parent, "Registrazione Dimissione Paziente", true);
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if (ssnField != null) {
            ssnField.setText(ssnPaziente);
            ssnField.setEditable(false);
        }
        dimissioneEffetivaChooser = new JDateChooser();
        dimissioneEffetivaChooser.setDateFormatString("dd/MM/yyyy");
        dimissioneEffetivaChooser.setDate(new Date());
        dimissioneEffetivaChooser.setMaxSelectableDate(new Date());
        if (dataRicovero != null) {
            dimissioneEffetivaChooser.setMinSelectableDate(dataRicovero);
        }
        datePanel.setLayout(new BorderLayout());
        datePanel.add(dimissioneEffetivaChooser, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public void setupSSNFilter() {
        if (ssnField != null) {
            ((AbstractDocument) ssnField.getDocument()).setDocumentFilter(new AdminController.UpperCaseDocumentFilter());
        }
    }

    // 2. Metodo per ottenere il valore
    public String getSSN() {
        return ssnField != null ? ssnField.getText() : null;
    }
    public String getDiagnosiUscita() {
        return esitoTextArea.getText();
    }

    public String getTerapia() {
        return terapiaTextArea.getText();
    }

    public JDateChooser getDimissioneEffettiva() {
        return dimissioneEffetivaChooser;
    }

    public JButton getSalvaButton() {
        return salvaButton;
    }

    public JButton getAnnullaButton() {
        return annullaButton;
    }

    public boolean isConfermato() {
        return confermato;
    }

    public void setConfermato(boolean confermato) {
        this.confermato = confermato;
    }

}
