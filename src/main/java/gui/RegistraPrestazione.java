package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import com.toedter.calendar.JDateChooser;
/**
 * Finestra di dialogo modale utilizzata dai medici per registrare una prestazione sanitaria effettuata.
 * Include campi per l'esito diagnostico, dettagli della prestazione e la selezione ad intervalli dell'orario.
 */
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
    /**
     * Costruisce la finestra modale, inizializza le componenti orarie ed imposta le dimensions della form.
     *
     * @param parent il frame principale di riferimento
     */
    public RegistraPrestazione(JFrame parent) {
        super(parent,"Registra Prestazione",true);
        setContentPane(mainPrestazione);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        inizializzaDataOra();

        setSize(550, 480);
        setLocationRelativeTo(parent);
    }
    /**
     * Genera la barra di selezione temporale combinando il calendario e la casella oraria a intervalli regolari.
     * <p>
     * Instanzia un {@link JDateChooser} impostando la data odierna come predefinita.
     * Successivamente, genera programmaticamente una lista di stringhe tramite un ciclo {@code for} che scansiona la fascia
     * diurna dalle 08:00 alle 20:00, inserendo intervalli regolari di 5 minuti (es. "08:00", "08:05", "08:10"). Mappa le stringhe
     * in un {@link DefaultComboBoxModel} assegnandolo alla ComboBox {@code oraBox}. Infine, imposta i tipi di prestazione
     * ammessi ("VISITA", "INTERVENTO_CHIRURGICO") all'interno di {@code tipoPrestazioneBox}.
     * </p>
     */
    private void inizializzaDataOra() {
        dateChooserPrestazione = new JDateChooser();
        dateChooserPrestazione.setDateFormatString("dd/MM/yyyy");
        dateChooserPrestazione.setDate(new Date()); // Imposta la data di oggi di default
        dataOraPanel.setLayout(new BorderLayout(5, 0));
        dataOraPanel.add(dateChooserPrestazione, BorderLayout.CENTER);
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
        dataOraPanel.add(oraBox, BorderLayout.EAST);
        if (tipoPrestazioneBox != null) {
            tipoPrestazioneBox.setModel(new DefaultComboBoxModel<>(new String[]{"VISITA", "INTERVENTO_CHIRURGICO"}));
        }
    }

    /**
     * Combina la selezione del calendario JDateChooser con l'orario scelto nella casella combinata
     * per strutturare e restituire un unico oggetto LocalDateTime unificato.
     *
     * @return il {@link LocalDateTime} risultante dagli input della form, o null se la data è assente
     */
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

    /**
     * Restituisce il pulsante salva.
     *
     * @return il componente JButton salva
     */
    public JButton getSalvaButton() {return  salvaButton;}
    /**
     * Restituisce il pulsante annulla.
     *
     * @return il componente JButton annulla
     */
    public JButton getAnnullaButton() {return annullaButton;}
    /**
     * Restituisce il tipo di atto medico selezionato nella casella a discesa (es. VISITA).
     *
     * @return la stringa della prestazione selezionata
     */
    public String getTipoSelezionato() { return (String) tipoPrestazioneBox.getSelectedItem(); }
    /**
     * Estrae il referto o l'esito inserito dal medico nell'area di testo.
     *
     * @return la stringa dell'esito diagnostico ripulita da spazi bianchi
     */
    public String getEsitoInput() { return EsitoPrestazione.getText().trim(); }
    /**
     * Estrae la descrizione generale dell'intervento o della visita medica.
     *
     * @return la stringa descrittiva della prestazione ripulita da spazi bianchi
     */
    public String getDescrizioneInput() { return descrizionePrestazione.getText().trim(); }
    /**
     * Recupera il codice fiscale (SSN) del paziente su cui viene erogata la prestazione.
     *
     * @return la stringa dell'SSN del paziente o null se il campo è assente
     */
    public String getSsnPaziente() { return ssnField != null ? ssnField.getText().trim() : null; }
}