package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class RegistraPrestazione extends JDialog {
    private JTextArea descrizionePrestazione;
    private JButton salvaButton;
    private JButton annullaButton;
    private JPanel dataOraPanel;
    private JComboBox<String> tipoPrestazioneBox;
    private JTextArea EsitoPrestazione;
    private JPanel mainPrestazione;
    private JTextField ssnField;
    private JDateChooser dateChooserPrestazione;
    private JComboBox<String> oraBox;

    public RegistraPrestazione(JFrame parent) {
        super(parent,"Registra Prestazione",true);
        setContentPane(mainPrestazione);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        inizializzaDataOra();

        setSize(550, 480);
        setLocationRelativeTo(parent);
    }

    private void inizializzaDataOra() {
        //Inizializziamo il JDateChooser
        dateChooserPrestazione = new JDateChooser();
        dateChooserPrestazione.setDateFormatString("dd/MM/yyyy");
        dateChooserPrestazione.setDate(new Date()); // Imposta la data di oggi di default
        dataOraPanel.setLayout(new BorderLayout(5, 0));
        dataOraPanel.add(dateChooserPrestazione, BorderLayout.CENTER);
        //JComboBox per l'ora e i minuti intermedi
        oraBox = new JComboBox<>();
        java.util.List<String> orari = new java.util.ArrayList<>();
        for (int ora = 8; ora < 20; ora++) {
            orari.add(String.format("%02d:00", ora));
            orari.add(String.format("%02d:05", ora));
            orari.add(String.format("%02d:10", ora));
            orari.add(String.format("%02d:15", ora));
            orari.add(String.format("%02d:20", ora));
            orari.add(String.format("%02d:25", ora));
            orari.add(String.format("%02d:30", ora));
            orari.add(String.format("%02d:35", ora));
            orari.add(String.format("%02d:40", ora));
            orari.add(String.format("%02d:45", ora));
            orari.add(String.format("%02d:50", ora));
            orari.add(String.format("%02d:55", ora));
        }
        oraBox.setModel(new DefaultComboBoxModel<>(orari.toArray(new String[0])));
        // Aggiungiamo la combo-box dell'ora a destra del calendario
        dataOraPanel.add(oraBox, BorderLayout.EAST);
        //Popoliamo la combo box del tipo prestazione
        if (tipoPrestazioneBox != null) {
            tipoPrestazioneBox.setModel(new DefaultComboBoxModel<>(new String[]{"VISITA", "INTERVENTO_CHIRURGICO"}));
        }
    }

    // Prende la Date del JDateChooser e le stringhe della JComboBox e sputa fuori un LocalDateTime
    public LocalDateTime getLocalDateTimeInput() {
        Date dateSelezionata = dateChooserPrestazione.getDate();
        if (dateSelezionata == null) return null;
        // Convertiamo la vecchia java.util.Date nel moderno LocalDate
        LocalDate dataLocal = dateSelezionata.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // Estraiamo ora e minuti selezionati nella tendina
        String[] oraMinuti = ((String) oraBox.getSelectedItem()).split(":");
        int ora = Integer.parseInt(oraMinuti[0]);
        int minuti = Integer.parseInt(oraMinuti[1]);
        // Uniamo tutto nel LocalDateTime richiesto dal tuo controller
        return LocalDateTime.of(dataLocal, LocalTime.of(ora, minuti));
    }
    public JButton getSalvaButton() {return  salvaButton;}
    public JButton getAnnullaButton() {return annullaButton;}
    public String getTipoSelezionato() { return (String) tipoPrestazioneBox.getSelectedItem(); }
    public String getEsitoInput() { return EsitoPrestazione.getText().trim(); }
    public String getDescrizioneInput() { return descrizionePrestazione.getText().trim(); }
}
