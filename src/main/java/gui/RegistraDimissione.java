package gui;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Finestra di dialogo modale adibita alla registrazione dell'atto di dimissione di un paziente ospedalizzato.
 * Raccoglie la diagnosi d'uscita, le indicazioni terapeutiche e la data effettiva di rilascio.
 */
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

    /**
     * Costruisce il modulo grafico di dimissione vincolando i range del calendario in base alla data di ricovero.
     * <p>
     * Inizializza la finestra impostando il blocco modale sul parent. Compila il campo
     * dell'SSN disattivandone l'editabilità diretta da tastiera per preservare l'integrità referenziale. Instanzia il
     * {@link JDateChooser} impostando la data corrente e, come misura fondamentale di controllo logico, applica il vincolo
     * {@code setMinSelectableDate(dataRicovero)}: questo impedisce graficamente all'amministratore di selezionare una data
     * di dimissione antecedente al giorno in cui il paziente è effettivamente entrato in ospedale.
     * </p>
     *
     * @param parent        il frame grafico principale di riferimento
     * @param dataInizioRicovero  la data d'ingresso iniziale del paziente (usata come limite minimo selezionabile)
     * @param dataDimissionePrevista  la data di uscita del paziente stimata da mostrare a schermo
     * @param ssnPaziente   il codice fiscale/SSN del paziente da dimettere
     */
    public RegistraDimissione(JFrame parent, Date dataInizioRicovero, Date dataDimissionePrevista, String ssnPaziente) {
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

        if (dataInizioRicovero != null) {
            dimissioneEffetivaChooser.setMinSelectableDate(dataInizioRicovero);
        }

        if(DataPrevista != null && dataDimissionePrevista != null) {
            //Per far apparire la data di dimissione prevista affianco al JLabel corrispondente
            SimpleDateFormat sdfVisualizza = new SimpleDateFormat("dd/MM/yyyy");
            JLabel lblDataPrevista = new JLabel(sdfVisualizza.format(dataDimissionePrevista));
            DataPrevista.setLayout(new BorderLayout());
            DataPrevista.add(lblDataPrevista, BorderLayout.CENTER);
        }
        datePanel.setLayout(new BorderLayout());
        datePanel.add(dimissioneEffetivaChooser, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

/**
 * Restituisce la casella di testo contenente il codice fiscale (SSN) del paziente.
 *
 * @return la stringa dell'SSN, o null se il campo non è inizializzato
 */
    public String getSSN() {
        return ssnField != null ? ssnField.getText() : null;
    }
    /**
     * Raccoglie la diagnosi finale di uscita inserita nell'apposita area di testo.
     *
     * @return il testo descrittivo della diagnosi di uscita
     */
    public String getDiagnosiUscita() {
        return esitoTextArea.getText();
    }
    /**
     * Raccoglie la terapia medica prescritta post-dimissione scritta nell'area di testo.
     *
     * @return il testo descrittivo della terapia assegnata
     */
    public String getTerapia() {
        return terapiaTextArea.getText();
    }
    /**
     * Restituisce il selettore della data effettiva in cui avviene la dimissione del paziente.
     *
     * @return il componente JDateChooser della data di rilascio
     */
    public JDateChooser getDimissioneEffettiva() {
        return dimissioneEffetivaChooser;
    }
    /**
     * Restituisce il pulsante salva.
     *
     * @return il componente JButton salva
     */
    public JButton getSalvaButton() {
        return salvaButton;
    }
    /**
     * Restituisce il pulsante annulla.
     *
     * @return il componente JButton annulla
     */
    public JButton getAnnullaButton() {
        return annullaButton;
    }
    /**
     * Restituisce lo stato di conferma.
     *
     * @return true se confermato
     */
    public boolean isConfermato() {
        return confermato;
    }
    /**
     * Imposta lo stato di conferma.
     *
     * @param confermato valore booleano di conferma
     */
    public void setConfermato(boolean confermato) {
        this.confermato = confermato;
    }
}
