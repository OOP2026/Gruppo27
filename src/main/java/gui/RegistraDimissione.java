package gui;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class RegistraDimissione {
    private JPanel mainPanel;
    private JPanel datePanel;
    private JDateChooser dateChooser;

    private JTextField ssnField;
    private JTextArea esitoTextArea;
    private JTextArea terapiaTextArea;
    private JButton salvaButton;
    private JButton annullaButton;

    public RegistraDimissione() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new Date());
        datePanel.setLayout(new BorderLayout());
        datePanel.add(dateChooser, BorderLayout.CENTER);

    }
    public String getSsn() { return ssnField.getText(); }
    public String getEsito() { return esitoTextArea.getText(); }
    public String getTerapia() { return terapiaTextArea.getText(); }
    public JDateChooser getDateChooser() { return dateChooser; }
    public JButton getSalvaButton() { return salvaButton; }
    public JButton getAnnullaButton() { return annullaButton; }

    //prova dimissione poi si cancella
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TEST: Registra Dimissione");
            RegistraDimissione testGui = new RegistraDimissione();
            frame.setContentPane(testGui.mainPanel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);
        });
    }
}
