package gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import model.Letto;
import model.Reparto;
import model.Stanza;
/**
 * Finestra di dialogo modale per la registrazione di un nuovo ricovero o day hospital.
 * Gestisce l'inserimento dell'SSN, della diagnosi e la selezione dinamica del letto
 * filtrata in base al reparto ospedaliero scelto.
 */
public class RegistraRicovero extends JDialog{
    private JPanel mainPanel;
    private JTextField SSNTextField;
    private JTextField DiagnosiTextField;
    private JCheckBox dayHospitalCheckBox;
    private JTextArea descrizioneTextArea;
    private JButton annullaButton;
    private JPanel datePanel;
    private JButton salvaButton;
    private JPanel datePanel2;
    private JComboBox<Reparto> RepartoComboBox;
    private JComboBox<Letto> LettoComboBox;
    private JDateChooser dateChooserRicovero;
    private JDateChooser dateChooserDimissione;
    private boolean confermato = false;

    /**
     * Costruisce la finestra di dialogo inizializzando i selettori grafici di data e popolando il menu a tendina dei reparti.
     * <p>
     * Configura la finestra come modale invocando il costruttore della superclasse
     * {@code super(parent, ..., true)}, bloccando l'interazione con il frame principale. Instanzia due componenti
     * esterni {@link JDateChooser} impostando la formattazione e vincolando la data minima selezionabile alla giornata odierna
     * tramite {@code setMinSelectableDate(new Date())}, prevenendo inserimenti retroattivi. Tramite un ciclo {@code for-each},
     * popola la casella combinata {@code RepartoComboBox} e aggancia un {@link java.awt.event.ActionListener} che intercetta
     * il cambio di selezione per invocare la routine privata {@code aggiornaLettiDisponibili}, garantendo l'aggiornamento dinamico.
     * </p>
     *
     * @param parent        il frame sovraordinato per l'ancoraggio e il posizionamento centrale della finestra
     * @param selectReparti la lista dei reparti da cui prelevare i dati per popolare la casella di selezione iniziale
     */
    public RegistraRicovero(JFrame parent, List<Reparto> selectReparti) {
        super(parent,"Registrazione Nuovo Ricovero",true);
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    //Data ricovero
        dateChooserRicovero = new JDateChooser();
        dateChooserRicovero.setDateFormatString("dd/MM/yyyy");
        dateChooserRicovero.setDate(new Date());
        dateChooserRicovero.setMinSelectableDate(new Date());
        datePanel.setLayout(new BorderLayout());
        datePanel.add(dateChooserRicovero, BorderLayout.CENTER);
    //Data dimissione prevista
        dateChooserDimissione = new JDateChooser();
        dateChooserDimissione.setDateFormatString("dd/MM/yyyy");
        dateChooserDimissione.setDate(new Date());
        dateChooserDimissione.setMinSelectableDate(new Date());
        datePanel2.setLayout(new BorderLayout());
        datePanel2.add(dateChooserDimissione, BorderLayout.CENTER);

        if(selectReparti != null){
            for(Reparto r: selectReparti){
                RepartoComboBox.addItem(r);
            }
        }

        aggiornaLettiDisponibili((Reparto) RepartoComboBox.getSelectedItem());
        //Quando cambia il reparto nel box, vengono aggiornati i letti disponibili
        RepartoComboBox.addActionListener(e -> {
            Object item = RepartoComboBox.getSelectedItem();
            if (item instanceof Reparto) {
                Reparto repartoSelezionato = (Reparto) item;
                aggiornaLettiDisponibili(repartoSelezionato);
            }
        });

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    /**
     * Aggiorna la casella combinata dei letti mostrando esclusivamente quelli liberi
     * appartenenti alle stanze del reparto selezionato.
     *
     * @param reparto il reparto di cui scansionare le stanze e i letti
     */
    private void aggiornaLettiDisponibili(Reparto reparto){
        LettoComboBox.removeAllItems();
        if(reparto == null || reparto.getStanze() == null) return;
        for(Stanza stanza : reparto.getStanze()){
            if(stanza.getLetti() != null){
                for (Letto letto : stanza.getLetti()){
                    if (letto.isLibero()){
                        LettoComboBox.addItem(letto);
                    }
                }
            }
        }
        LettoComboBox.revalidate();
        LettoComboBox.repaint();
    }
    /**
     * Restituisce l'SSN del paziente inserito nel campo di testo.
     *
     * @return il codice fiscale/SSN inserito
     */
    public String getSSN() { return SSNTextField.getText(); }
    /**
     * Restituisce il testo della diagnosi d'entrata specificato.
     *
     * @return la diagnosi iniziale
     */
    public String getDiagnosiEntrata(){ return DiagnosiTextField.getText();}
    /**
     * Specifica se il ricovero da registrare è in regime di Day Hospital.
     *
     * @return true se la casella Day Hospital è selezionata, false altrimenti
     */
    public boolean isDayHospital() { return dayHospitalCheckBox.isSelected(); }
    /**
     * Restituisce le note descrittive aggiuntive sul ricovero.
     *
     * @return il testo descrittivo delle note
     */
    public String getDescrizione() { return descrizioneTextArea.getText(); }
    /**
     * Restituisce il componente di selezione per la data del ricovero.
     *
     * @return l'istanza di JDateChooser della data d'ingresso
     */
    public JDateChooser getDateChooserRicovero() { return dateChooserRicovero; }
    /**
     * Restituisce il componente di selezione per la data di dimissione prevista.
     *
     * @return l'istanza di JDateChooser della data d'uscita stimata
     */
    public JDateChooser getDateChooserDimissione() {return dateChooserDimissione;}
    /**
     * Restituisce il pulsante per annullare l'operazione.
     *
     * @return il bottone annulla
     */
    public JButton getAnnullaButton() { return annullaButton; }
    /**
     * Restituisce il pulsante per confermare e salvare il ricovero.
     *
     * @return il bottone salva
     */
    public JButton getSalvaButton() { return salvaButton; }
    /**
     * Verifica se l'operazione di inserimento è stata confermata dall'utente.
     *
     * @return true se il ricovero è stato approvato, false altrimenti
     */
    public boolean isConfermato() { return confermato; }
    /**
     * Configura lo stato di conferma della procedura.
     *
     * @param confermato impostare a true se l'azione è andata a buon fine
     */
    public void setConfermato(boolean confermato) { this.confermato = confermato; }
    /**
     * Restituisce il reparto attualmente selezionato nella casella a discesa.
     *
     * @return l'oggetto Reparto selezionato
     */
    public Reparto getRepartoSelezionato() { return (Reparto) RepartoComboBox.getSelectedItem(); }
    /**
     * Restituisce il letto attualmente selezionato nella casella a discesa.
     *
     * @return l'oggetto Letto selezionato
     */
    public Letto getLettoSelezionato() { return (Letto) LettoComboBox.getSelectedItem(); }
}