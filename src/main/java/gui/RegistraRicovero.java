package gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class RegistraRicovero {
    private JPanel mainPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JCheckBox dayHospitalCheckBox;
    private JTextArea descrizioneTextArea;
    private JButton annullaButton;
    private JPanel datePanel;
    private JButton salvaButton;
    private JDateChooser dateChooser;

    public RegistraRicovero() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new Date());
        dateChooser.setMinSelectableDate(new Date());
        datePanel.setLayout(new BorderLayout());
        datePanel.add(dateChooser, BorderLayout.CENTER);
    }
    public String getDatiPaziente() { return textField1.getText(); }
    public boolean isDayHospital() { return dayHospitalCheckBox.isSelected(); }
    public String getDescrizione() { return descrizioneTextArea.getText(); }
    public JDateChooser getDateChooser() { return dateChooser; }
    public JButton getAnnullaButton() { return annullaButton; }
    public JButton getSalvaButton() { return salvaButton; }

// prova regisreazione poi si cancella
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TEST: Registra Ricovero");
            RegistraRicovero testGui = new RegistraRicovero();
            frame.setContentPane(testGui.mainPanel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);
        });
    }
}