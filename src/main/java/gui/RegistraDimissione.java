package gui;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
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
    private boolean confermato;

    public RegistraDimissione(JFrame parent, Date dataPrevistaRicovero) {
        super(parent, "Registrazione Dimissione Paziente", true);
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        dimissioneEffetivaChooser = new JDateChooser();
        dimissioneEffetivaChooser.setDateFormatString("dd/MM/yyyy");
        dimissioneEffetivaChooser.setDate(new Date());
        datePanel.setLayout(new BorderLayout());
        datePanel.add(dimissioneEffetivaChooser, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public String getSsn() { return ssnField.getText(); }
    public String getEsito() { return esitoTextArea.getText(); }
    public String getTerapia() { return terapiaTextArea.getText(); }
    public JDateChooser getDimissioneEffettiva() { return dimissioneEffetivaChooser; }
    public JButton getSalvaButton() { return salvaButton; }
    public JButton getAnnullaButton() { return annullaButton; }
    public boolean isConfermato(){return confermato;}
    public void setConfermato(boolean confermato){this.confermato=confermato;}

    //prova dimissione poi si cancella
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            Date ipoteticaDataPrevista = new Date();
            RegistraDimissione dialog = new RegistraDimissione(frame, ipoteticaDataPrevista);

            dialog.getAnnullaButton().addActionListener(e -> dialog.dispose());
            dialog.getSalvaButton().addActionListener(e -> {
                dialog.setConfermato(true);
                dialog.dispose();
            });

            dialog.setVisible(true);

            if (dialog.isConfermato()) {
                System.out.println("Dimissione Registrata!");
                System.out.println("SSN: " + dialog.getSsn());
                System.out.println("Data Effettiva: " + dialog.getDimissioneEffettiva().getDate());
            }
            System.exit(0);
        });
    }
}
