package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 * Pannello grafico preposto alla visualizzazione e alla gestione dell'anagrafica dei pazienti.
 * Contiene i campi per l'inserimento/modifica dei dati e la tabella riassuntiva dei record presenti.
 */
public class GestioneAnagraficaPazienti {
    private JPanel mainPanel;
    private JTextField txtCf;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtRecapito;

    private JButton btnAggiungi;
    private JButton btnModifica;
    private JButton btnElimina;
    private JButton btnPulisci;
    private JTable tablePazienti;
    private DefaultTableModel tableModel;

    private JTextField txtRicerca;
    private JButton btnCerca;

    /**
     * Costruisce la schermata di gestione dell'anagrafica attivando la formattazione della tabella.
     * <p>
     * Se la JTable definita da file di design (.form) risulta correttamente
     * istanziata, delega la configurazione strutturale e l'assegnazione dei vettori di colonna al metodo privato {@code creaTabella()}.
     * </p>
     */
    public GestioneAnagraficaPazienti() {
        if (tablePazienti != null) {
            creaTabella();
        }
    }

    /**
     * Configura le colonne e l'editabilità della tabella dei pazienti.
     * <p>
     * Istanzia un oggetto {@link DefaultTableModel} definendo l'array fisso
     * delle etichette delle colonne. Per impedire modifiche accidentali da tastiera direttamente sulle celle della griglia,
     * sovrascrive inline il metodo {@code isCellEditable(int row, int column)} forzandolo a restituire costantemente {@code false}.
     * Infine, collega il modello alla JTable mediante {@code tablePazienti.setModel(tableModel)}.
     * </p>
     */
    private void creaTabella() {
        // Aggiunta la colonna Reparto per la visualizzazione suddivisa
        String[] colonne = {"SSN", "Nome", "Cognome", "Recapito"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePazienti.setModel(tableModel);
    }

    /**
     * Restituisce il pannello principale della schermata anagrafica.
     *
     * @return il componente JPanel principale
     */
    public JPanel getMainPanel() { return mainPanel; }
    /**
     * Restituisce il campo di testo per l'inserimento del Codice Fiscale (SSN).
     *
     * @return il componente JTextField del codice fiscale
     */
    public JTextField getTxtCf() { return txtCf; }
    /**
     * Restituisce il campo di testo per l'inserimento del nome del paziente.
     *
     * @return il componente JTextField del nome
     */
    public JTextField getTxtNome() { return txtNome; }
    /**
     * Restituisce il campo di testo per l'inserimento del cognome del paziente.
     *
     * @return il componente JTextField del cognome
     */
    public JTextField getTxtCognome() { return txtCognome; }
    /**
     * Restituisce il campo di testo per l'inserimento del recapito telefonico.
     *
     * @return il componente JTextField del recapito
     */
    public JTextField getTxtRecapito() { return txtRecapito; }
    /**
     * Restituisce il pulsante per l'aggiunta di un nuovo paziente.
     *
     * @return il componente JButton aggiungi
     */
    public JButton getBtnAggiungi() { return btnAggiungi; }
    /**
     * Restituisce il pulsante per la modifica dei dati del paziente selezionato.
     *
     * @return il componente JButton modifica
     */
    public JButton getBtnModifica() { return btnModifica; }
    /**
     * Restituisce il pulsante per la rimozione del paziente selezionato.
     *
     * @return il componente JButton elimina
     */
    public JButton getBtnElimina() { return btnElimina; }
    /**
     * Restituisce il pulsante per ripulire i campi di testo della form.
     *
     * @return il componente JButton pulisci
     */
    public JButton getBtnPulisci() { return btnPulisci; }
    /**
     * Restituisce la tabella grafica in cui vengono visualizzati i pazienti.
     *
     * @return il componente JTable dei pazienti
     */
    public JTable getTablePazienti() { return tablePazienti; }
    /**
     * Restituisce il modello dei dati della tabella dei pazienti.
     *
     * @return l'oggetto DefaultTableModel associato alla tabella
     */
    public DefaultTableModel getTableModel() { return tableModel; }
    /**
     * Restituisce il campo di testo utilizzato per digitare la stringa di ricerca.
     *
     * @return il componente JTextField di ricerca
     */
    public JTextField getTxtRicerca() { return txtRicerca; }
    /**
     * Restituisce il pulsante per attivare il filtro di ricerca sui pazienti.
     *
     * @return il componente JButton cerca
     */
    public JButton getBtnCerca() { return btnCerca; }
}