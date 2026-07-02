package controller;

import dao.LettoDAO;
import dao.PazienteDAO;
import dao.PrestazioneMedicaDAO;
import dao.RepartoDAO;
import dao.RicoveroDAO;
import database_connection.ConnessioneDatabase;
import gui.InterfacciaAmministratore;
import gui.RegistraDimissione;
import gui.RegistraRicovero;
import gui.GestioneAnagraficaPazienti;
import gui.GestioneLetti;
import implementazioneDao.LettoPostgresDao;
import implementazioneDao.PazientePostgresDao;
import implementazioneDao.PrestazioneMedicaPostgresDao;
import implementazioneDao.RepartoPostgresDao;
import implementazioneDao.RicoveroPostgresDao;

import model.*;
import model.Ricovero;
import model.Paziente;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller principale per l'interfaccia dell'amministratore.
 * <p>
 * Gestisce il flusso dei dati e la logica di business relativa alla gestione dell'anagrafica pazienti,
 * alla mappatura in tempo reale dei letti nei reparti, alla registrazione dei ricoveri e
 * alle procedure di dimissione.
 * </p>
 */
public class AdminController {

    // ────────────────────────────────────────────────────────────
    private static final Logger LOGGER           = Logger.getLogger(AdminController.class.getName());
    private static final String         TITOLO_ERRORE    = "Errore";
    private static final String         DATE_FORMAT      = "dd/MM";
    private SimpleDateFormat SDF            = new SimpleDateFormat(DATE_FORMAT);

    // Indici table anagrafica
    private static final int COL_SSN      = 0;
    private static final int COL_NOME     = 1;
    private static final int COL_COGNOME  = 2;
    private static final int COL_RECAPITO = 3;

    // ────────────────────────────────────────────────────────────────
    private final InterfacciaAmministratore view;
    private final Amministratore                  admin;
    private final JFrame                          mainFrame;
    private final List<Reparto>                   listaReparti;

    private final PazienteDAO pazienteDAO;
    private final RicoveroDAO ricoveroDAO;
    private final LettoDAO lettoDAO;
    private final PrestazioneMedicaDAO prestazioneMedicaDAO;

    private final List<Ricovero> elencoRicoveri;
    private final List<Paziente> elencoPazienti;

    private GestioneAnagraficaPazienti anagraficaView;
    private GestioneLetti              gestioneLettiView;
    private DefaultTableModel          tableModelRicoveri;
    private DefaultTableModel          tableModelDimissioni;

    /**
     * Costruisce un'istanza di AdminController inizializzando i componenti DAO necessari,
     * scaricando i dati dal database e configurando le componenti grafiche associate.
     * <p>
     * Alloca le istanze delle implementazioni PostgreSQL per i DAO. Avvia una routine di sbarramento scaricando
     * l'elenco totale dei reparti e dei pazienti. Popola la cache locale {@code elencoRicoveri} eseguendo un ciclo
     * {@code for-each} sui pazienti che interroga iterativamente il database tramite {@code findByPaziente(cf)}.
     * Successivamente ordina l'esecuzione sequenziale dei metodi di inizializzazione della UI, il popolamento delle
     * tabelle e il refresh della mappa dei letti.
     * </p>
     *
     * @param view      l'interfaccia grafica dell'amministratore
     * @param admin     l'oggetto modello che rappresenta l'amministratore loggato
     * @param mainFrame il frame principale dell'applicazione Swing
     */
    public AdminController(InterfacciaAmministratore view, Amministratore admin, JFrame mainFrame) {
        this.view        = view;
        this.admin       = admin;
        this.mainFrame   = mainFrame;

        RepartoDAO repartoDAO = new RepartoPostgresDao();
        this.pazienteDAO = new PazientePostgresDao();
        this.ricoveroDAO = new RicoveroPostgresDao();
        this.lettoDAO = new LettoPostgresDao();
        this.prestazioneMedicaDAO = new PrestazioneMedicaPostgresDao();

        this.listaReparti = repartoDAO.findAll();
        this.elencoPazienti = new ArrayList<>(pazienteDAO.findAll());
        this.elencoRicoveri = new ArrayList<>();
        for (Paziente paziente : elencoPazienti) {
            elencoRicoveri.addAll(ricoveroDAO.findByPaziente(paziente.getCf()));
        }

        inizializzaAnagrafica();
        inizializzaGestioneLetti();
        inizializzaTabella();
        inizializzaAzioni();
        inizializzaFiltriDimissioni();
        popolaTabellaPazienti();
        aggiornaTabelle();
    }

    // =========================================================================
    // METODI INIZIALIZZA
    // =========================================================================
    /**
     * Inizializza la scheda di gestione dell'anagrafica dei pazienti inserendola nel pannello a schede.
     * <p>
     * Istanzia l'oggetto della sotto-vista {@link GestioneAnagraficaPazienti},verifica l'integrità del contenitore a
     * schede e vi inserisce il pannello radice dell'anagrafica all'indice 0 tramite il metodo {@code setComponentAt()}.
     * Infine, delega la configurazione dei listener richiamando {@code inizializzaAzioniAnagrafica()}.
     * </p>
     */
    private void inizializzaAnagrafica() {
        anagraficaView = new GestioneAnagraficaPazienti();

        JTabbedPane tabbedPane = view.getTabbedPane();
        if (tabbedPane == null || anagraficaView.getMainPanel() == null) {
            LOGGER.severe("ERRORE: I pannelli anagrafica sono NULL.");
            return;
        }
        tabbedPane.setComponentAt(0, anagraficaView.getMainPanel());
        inizializzaAzioniAnagrafica();
    }
    /**
     * Inizializza la scheda di gestione della mappa dei letti, configurando la casella a discesa dei reparti.
     * <p>
     * Istanzia la sotto-vista {@link GestioneLetti} e la alloca all'indice 1
     * del pannello a schede della dashboard. Estrae il componente JComboBox dei reparti e vi inserisce massivamente
     * gli elementi della lista locale tramite un riferimento a metodo {@code listaReparti.forEach(cmbReparti::addItem)}.
     * Associa un listener per ridisegnare la mappa dei posti letto ad ogni cambio di reparto e invoca
     * {@code aggiornaMappaCorrente()} per inizializzare il rendering visivo del primo reparto.
     * </p>
     */
    private void inizializzaGestioneLetti() {
        gestioneLettiView = new GestioneLetti();

        JTabbedPane tabbedPane = view.getTabbedPane();
        if (tabbedPane == null || gestioneLettiView.getMainPanel() == null) {
            return;
        }
        tabbedPane.setComponentAt(1, gestioneLettiView.getMainPanel());

        JComboBox<Reparto> cmbReparti = gestioneLettiView.getCmbReparti();
        if (cmbReparti != null && listaReparti != null) {
            listaReparti.forEach(cmbReparti::addItem);
            cmbReparti.addActionListener(e -> aggiornaMappaCorrente());
            JTextField txtRicerca = gestioneLettiView.getTxtRicercaLetto();
            if (txtRicerca != null) {
                txtRicerca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtraLetti(); }
                    @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtraLetti(); }
                    @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtraLetti(); }
                });
            }
        }

        if (!listaReparti.isEmpty()) {
            aggiornaMappaCorrente();
        }
    }
    /**
     * Configura le tabelle Swing non modificabili per lo storico dei ricoveri e per le dimissioni in corso.
     * Richiama la funzione di supporto {@code buildNonEditableModel} per istanziare
     * i modelli tabellari impostando le rispettive intestazioni di colonna. Per la tabella dei ricoveri storici, installa
     * un {@link TableRowSorter} configurando un criterio di ordinamento decrescente automatico basato sulla colonna 0 (Anno)
     * mediante la classe {@link RowSorter.SortKey}.
     * </p>
     */
    private void inizializzaTabella() {
        // --- Storico ricoveri ---
        String[] colonneRicoveri = {"Anno", "Stato", "SSN Paziente", "Data Entrata",
                "Data Uscita", "Diagnosi Entrata", "Diagnosi Uscita"};
        tableModelRicoveri = buildNonEditableModel(colonneRicoveri);

        if (view.getRicoveriTable() != null) {
            view.getRicoveriTable().setModel(tableModelRicoveri);

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModelRicoveri);
            sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
            sorter.sort();
            view.getRicoveriTable().setRowSorter(sorter);
        }

        // --- Dimissioni in corso ---
        String[] colonneDimissioni = {"SSN Paziente", "Nome", "Cognome", "Reparto",
                "Letto", "Medico", "Data Dimissione"};
        tableModelDimissioni = buildNonEditableModel(colonneDimissioni);

        if (view.getDimissioniTable() != null) {
            view.getDimissioniTable().setModel(tableModelDimissioni);
        }
    }
    /**
     * Costruisce un modello di tabella predefinito forzando le celle a non essere modificabili dall'utente.
     *
     * @param columns array di stringhe contenente i nomi delle colonne
     * @return un'istanza configurata di DefaultTableModel non editabile
     */
    private DefaultTableModel buildNonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    /**
     * Configura i listener per la selezione della tabella pazienti, i filtri di ricerca e i pulsanti CRUD.
     * <p>
     * Associa un {@link javax.swing.event.ListSelectionListener} al modello di
     * selezione della JTable per intercettare i click sulle righe dell'anagrafica. Estrae il documento di testo del campo CF
     * ed esegue un cast ad {@link AbstractDocument} per installarvi un {@link UpperCaseDocumentFilter}, forzando la conversione
     * in tempo reale dei caratteri immessi in maiuscolo. Registra infine i listener d'azione per i pulsanti Aggiungi, Modifica,
     * Elimina, Pulisci e installa un {@link TableRowSorter} legato al campo di ricerca testuale mediante l'espressione
     * regolare {@code RowFilter.regexFilter("(?i)" + testo)}.
     * </p>
     */
    private void inizializzaAzioniAnagrafica() {
        if (anagraficaView.getTablePazienti() != null) {
            anagraficaView.getTablePazienti()
                    .getSelectionModel()
                    .addListSelectionListener(this::caricaDatiPazienteSelezionato);
        }

        // forza il sistema a scrivere SSN in MAIUSC
        if (anagraficaView.getTxtCf() != null) {
            ((AbstractDocument) anagraficaView.getTxtCf().getDocument())
                    .setDocumentFilter(new UpperCaseDocumentFilter());
        }

        anagraficaView.getBtnAggiungi().addActionListener(e -> aggiungiPaziente());
        anagraficaView.getBtnModifica().addActionListener(e -> modificaPaziente());
        anagraficaView.getBtnElimina().addActionListener(e -> eliminaPaziente());
        anagraficaView.getBtnPulisci().addActionListener(e -> pulisciCampiAnagrafica());

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(anagraficaView.getTableModel());
        anagraficaView.getTablePazienti().setRowSorter(sorter);

        // Search: button click and Enter key both trigger the filter
        anagraficaView.getBtnCerca().addActionListener(e -> {
            String testo = anagraficaView.getTxtRicerca().getText().trim();
            sorter.setRowFilter(testo.isEmpty() ? null : RowFilter.regexFilter("(?i)" + testo));
        });
        anagraficaView.getTxtRicerca().addActionListener(e -> anagraficaView.getBtnCerca().doClick());
    }
    /**
     * Collega le azioni principali per i pulsanti di Logout, Registrazione Ricovero e Dimissione.
     * Risolve il riferimento al frame principale salendo lungo l'albero gerarchico
     * dei componenti Swing tramite {@link SwingUtilities#getWindowAncestor(Component)}. Registra i listener d'azione
     * delegando le operazioni di business rispettivamente ai metodi {@code gestisciLogout()}, {@code gestisciRegistrazioneRicovero()}
     * e {@code gestisciDimissione(parentFrame)}.
     * </p>
     */
    private void inizializzaAzioni() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view.getPanelAmministratore());

        if (view.getLogoutButton() != null) {
            view.getLogoutButton().addActionListener(e -> gestisciLogout());
        }

        if (view.getRegistraRicoveriButton() == null || view.getRegistraDimissioneButton() == null) {
            LOGGER.severe("ERRORE GUI: I bottoni Ricovero/Dimissione sono NULL.");
            return;
        }

        view.getRegistraRicoveriButton().addActionListener(e -> gestisciRegistrazioneRicovero());
        view.getRegistraDimissioneButton().addActionListener(e -> gestisciDimissione(parentFrame));
    }
    /**
     * Inizializza i filtri di ricerca temporali per la griglia delle dimissioni in corso.
     * <p>
     * Instanzia e collega un {@link TableRowSorter} alla tabella delle dimissioni.
     * Associa un listener al pulsante "Filtra Data" che estrae l'oggetto Date dal calendario, lo formatta in stringa
     * mediante il SimpleDateFormat locale e lo immette come filtro di eguaglianza testuale stringente sulla colonna 6
     * (indice della data di dimissione) applicando {@code RowFilter.regexFilter}. Configura analogamente il pulsante "Oggi"
     * (forzando il JDateChooser al timestamp odierno) ed il pulsante "Reset" (ripristinando il filtro del sorter a {@code null}).
     * </p>
     */
    private void inizializzaFiltriDimissioni() {
        if (view.getDimissioniTable() == null || view.getDateChooserFiltro() == null) return;

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModelDimissioni);
        view.getDimissioniTable().setRowSorter(sorter);

        view.getBtnFiltraData().addActionListener(e -> {
            Date dataScelta = view.getDateChooserFiltro().getDate();
            if (dataScelta == null) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("^" + SDF.format(dataScelta) + "$", 6));
            }
        });

        view.getBtnFiltraOggi().addActionListener(e -> {
            Date oggi = new Date();
            view.getDateChooserFiltro().setDate(oggi);
            sorter.setRowFilter(RowFilter.regexFilter("^" + SDF.format(oggi) + "$", 6));
        });

        view.getBtnResetFiltro().addActionListener(e -> {
            view.getDateChooserFiltro().setDate(null);
            sorter.setRowFilter(null);
        });
    }
    /**
     * Riversa i record dei pazienti caricati in memoria locale all'interno della JTable di anagrafica.
     */
    private void popolaTabellaPazienti() {
        DefaultTableModel model = anagraficaView.getTableModel();
        model.setRowCount(0); // Azzera per sicurezza

        // Travasa i dati scaricati dal DB nella JTable
        for (Paziente p : elencoPazienti) {
            model.addRow(new Object[]{
                    p.getCf(),
                    p.getNome(),
                    p.getCognome(),
                    p.getRecapito()
            });
        }
    }
    // =========================================================================
    // METODI GESTIONE AZIONI
    // =========================================================================
    /**
     * Gestisce la procedura di logout visualizzando una finestra di dialogo di conferma
     * e reindirizzando l'utente alla schermata di Login.
     */
    private void gestisciLogout() {
        int conferma = JOptionPane.showConfirmDialog(
                mainFrame,
                "Sei sicuro di voler effettuare il logout?",
                "Conferma Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (conferma == JOptionPane.YES_OPTION) {
            gui.Login loginView = new gui.Login();
            new Controller(loginView, mainFrame);
            mainFrame.setExtendedState(JFrame.NORMAL);
            mainFrame.setSize(750, 650);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setTitle("Login");
            mainFrame.setContentPane(loginView.getPanelLogin());
            mainFrame.revalidate();
            mainFrame.repaint();
            mainFrame.setResizable(false);

        }
    }
    /**
     * Gestisce la procedura guidata per la registrazione di un nuovo ricovero o di un day hospital.
     * <p>
     * Il metodo esegue in sequenza le seguenti operazioni di business e validazione:
     * <ul>
     * <li>Inizializza e mostra la finestra di dialogo modale {@link RegistraRicovero} passando i reparti disponibili.</li>
     * <li>Intercetta l'azione di salvataggio ed esegue i controlli di sbarramento sull'SSN del paziente (presenza obbligatoria, esistenza in anagrafica e assenza di ricoveri attivi concomitanti).</li>
     * <li>Se l'input è valido, istanzia un nuovo oggetto {@link Ricovero} configurandolo con i dati prelevati dalla form (date, letto, diagnosi e note).</li>
     * <li>Avvia una transazione SQL atomica per salvare il ricovero e aggiornare lo stato del letto a "occupato" sul database, garantendo che entrambe le operazioni abbiano successo o nessuna.</li>
     * <li>In caso di fallimento della persistenza, intercetta l'eccezione, mostra un messaggio d'errore e interrompe il flusso senza alterare la memoria locale.</li>
     * <li>A transazione conclusa, aggiorna lo stato del letto in memoria, inserisce il ricovero nella cache locale e ordina il refresh delle tabelle e della mappa grafica dei letti.</li>
     * </ul>
     * </p>
     */
    private void gestisciRegistrazioneRicovero() {
        RegistraRicovero dialogRicovero = new RegistraRicovero(mainFrame, listaReparti);
        dialogRicovero.getAnnullaButton().addActionListener(a -> dialogRicovero.dispose());

        dialogRicovero.getSalvaButton().addActionListener(s -> {
            String ssnInserito = normalizeSsn(dialogRicovero.getSSN());

            if (ssnInserito.isEmpty()) {
                JOptionPane.showMessageDialog(dialogRicovero,
                        "Attenzione: l'SSN del paziente è obbligatorio!",
                        TITOLO_ERRORE, JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!ssnEsisteInAnagrafica(ssnInserito)) {
                JOptionPane.showMessageDialog(dialogRicovero,
                        "Impossibile avviare il ricovero: l'SSN inserito non è registrato nel sistema.",
                        "Paziente Sconosciuto", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (pazienteGiaRicoverato(ssnInserito)) {
                JOptionPane.showMessageDialog(dialogRicovero,
                        "Impossibile procedere: Il paziente risulta già ricoverato in struttura.",
                        "Errore Ricovero Duplicato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dialogRicovero.setConfermato(true);
            dialogRicovero.dispose();
        });

        dialogRicovero.setVisible(true);

        if (!dialogRicovero.isConfermato()) return;

        String diagnosiEntrata = normalizeDiagnosi(dialogRicovero.getDiagnosiEntrata());

        Letto lettoScelto = dialogRicovero.getLettoSelezionato();

        Ricovero nuovoRicovero = new Ricovero(
                normalizeSsn(dialogRicovero.getSSN()),
                dialogRicovero.getDateChooserRicovero().getDate(),
                dialogRicovero.getDateChooserDimissione().getDate(),
                lettoScelto,
                diagnosiEntrata
        );
        nuovoRicovero.setDayHospital(dialogRicovero.isDayHospital());
        nuovoRicovero.setDescrizione(dialogRicovero.getDescrizione());

        try {
            // Le due operazioni devono avvenire insieme: se il salvataggio del ricovero
            // fallisse dopo aver già segnato il letto come occupato, ci ritroveremmo con
            // un letto bloccato senza nessun ricovero collegato. La transazione garantisce
            // che vengano confermate entrambe o nessuna delle due.
            ConnessioneDatabase.eseguiInTransazione(() -> {
                if (lettoScelto != null) {
                    lettoDAO.updateStato(lettoScelto.getCodiceInventario(), false);
                }
                ricoveroDAO.save(nuovoRicovero);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view.getPanelAmministratore(),
                    "Errore durante la registrazione del ricovero, nessuna modifica è stata salvata: "
                            + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()),
                    TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* Solo se la transazione è andata a buon fine aggiorniamo lo stato in memoria,
        // così la UI resta coerente con quanto effettivamente salvato sul database.
        if (lettoScelto != null) {
            lettoScelto.setStatoAttuale(false);
        }*/
        elencoRicoveri.add(nuovoRicovero);
        aggiornaTabelle();
        aggiornaMappaCorrente();
        JOptionPane.showMessageDialog(view.getPanelAmministratore(),
                "Ricovero registrato e inserito nei pannelli!");
    }
    /**
     * Gestisce e formalizza la procedura di dimissione di un paziente selezionato.
     * <p>
     * Il metodo esegue le seguenti operazioni:
     * <ul>
     * <li>Verifica che ci sia una riga selezionata nella tabella delle dimissioni.</li>
     * <li>Converte l'indice della riga della vista nel corrispondente indice del modello per preservare la coerenza in caso di filtri o ordinamenti attivi.</li>
     * <li>Recupera il ricovero attivo associato all'SSN selezionato.</li>
     * <li>Visualizza la finestra di dialogo modale {@link RegistraDimissione} per l'inserimento dei dati di uscita (data effettiva, diagnosi e terapia).</li>
     * <li>Valida che la data di dimissione inserita non sia antecedente a quella di inizio ricovero.</li>
     * <li>Esegue una transazione SQL per aggiornare lo stato del ricovero nel database e liberare il letto associato.</li>
     * <li>In caso di errore nel database, esegue il rollback dello stato dell'oggetto in memoria locale.</li>
     * <li>Aggiorna le componenti grafiche (tabelle e mappa dei letti) per riflettere le modifiche.</li>
     * </ul>
     * </p>
     *
     * @param parentFrame il frame grafico sovraordinato utilizzato per l'ancoraggio e la corretta visualizzazione della finestra di dialogo modale
     */
    private void gestisciDimissione(JFrame parentFrame) {
        JTable tabellaDimissioni = view.getDimissioniTable();
        int rigaSelezionataView = tabellaDimissioni.getSelectedRow();

        if (rigaSelezionataView == -1) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Seleziona un paziente dalla tabella nella scheda 'Dimissione pazienti' per procedere!",
                    "Nessuna selezione", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // IMPORTANTE: la tabella ha un RowSorter (vedi inizializzaFiltriDimissioni), quindi l'indice
        // di riga selezionato si riferisce alla vista ordinata/filtrata, non al model sottostante.
        // Va convertito prima di leggere i valori, altrimenti con un ordinamento o filtro attivo
        // si rischia di leggere i dati di un paziente diverso da quello effettivamente cliccato.
        int rigaSelezionata = tabellaDimissioni.convertRowIndexToModel(rigaSelezionataView);

        String ssnSelezionato = normalizeSsn((String) tabellaDimissioni.getModel().getValueAt(rigaSelezionata, 0));
        Ricovero ricovero = trovaRicoveroAttivo(ssnSelezionato);
        if (ricovero == null) return;

        RegistraDimissione dialogDimissione =
                new RegistraDimissione(parentFrame, ricovero.getDataRicovero(), ricovero.getDataDimissionePrevista(), ssnSelezionato);
        dialogDimissione.getAnnullaButton().addActionListener(a -> dialogDimissione.dispose());
        dialogDimissione.getSalvaButton().addActionListener(s -> {
            Date dataScelta = dialogDimissione.getDimissioneEffettiva().getDate();
            if (dataScelta == null) {
                JOptionPane.showMessageDialog(dialogDimissione,
                        "Seleziona una data di dimissione valida!",
                        TITOLO_ERRORE, JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Il controllo logico di sbarramento: non prima dell'inizio del ricovero
            if (dataScelta.before(ricovero.getDataRicovero())) {
                JOptionPane.showMessageDialog(dialogDimissione,
                        "Errore: La data di dimissione non può essere antecedente alla data del ricovero (" + formatData(ricovero.getDataRicovero()) + ").",
                        "Vincolo Data Violato", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dialogDimissione.setConfermato(true);
            dialogDimissione.dispose();
        });

        dialogDimissione.setVisible(true);

        if (!dialogDimissione.isConfermato()) return;

        String diagnosiUscita = normalizeDiagnosi(dialogDimissione.getDiagnosiUscita());
        String terapia = dialogDimissione.getTerapia();
        Date dataDimissioneScelta = dialogDimissione.getDimissioneEffettiva().getDate();
        Date dataDimissioneEffettiva = dataDimissioneScelta != null ? dataDimissioneScelta : new Date();
        Letto lettoDaLiberare = ricovero.getLettoAssegnato();

        // Prepariamo i nuovi valori senza ancora mutare l'oggetto Ricovero reale: se la
        // transazione dovesse fallire, vogliamo poter lasciare l'oggetto esattamente come
        // era prima, così la UI non mostra un paziente come dimesso se il DB non ha salvato nulla.
        boolean inCorsoOriginale = ricovero.isInCorso();
        String diagnosiUscitaOriginale = ricovero.getDiagnosiUscita();
        String terapiaOriginale = ricovero.getTerapia();
        Date dataDimissioneEffettivaOriginale = ricovero.getDataDimissioneEffettiva();

        ricovero.setInCorso(false);
        ricovero.setDiagnosiUscita(diagnosiUscita);
        ricovero.setTerapia(terapia);
        ricovero.setDataDimissioneEffettiva(dataDimissioneEffettiva);

        try {
            // Stesso ragionamento della registrazione: chiudere il ricovero e liberare il
            // letto devono avvenire insieme, altrimenti un fallimento a metà strada lascerebbe
            // un ricovero chiuso con il letto ancora segnato come occupato (o viceversa).
            ConnessioneDatabase.eseguiInTransazione(() -> {
                ricoveroDAO.update(ricovero);
                if (lettoDaLiberare != null) {
                    lettoDAO.updateStato(lettoDaLiberare.getCodiceInventario(), true);
                }
            });
        } catch (Exception e) {
            // Rollback dello stato in memoria: la transazione non è andata a buon fine.
            ricovero.setInCorso(inCorsoOriginale);
            ricovero.setDiagnosiUscita(diagnosiUscitaOriginale);
            ricovero.setTerapia(terapiaOriginale);
            ricovero.setDataDimissioneEffettiva(dataDimissioneEffettivaOriginale);

            JOptionPane.showMessageDialog(view.getPanelAmministratore(),
                    "Errore durante la dimissione del paziente, nessuna modifica è stata salvata: "
                            + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()),
                    TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lettoDaLiberare != null) {
            lettoDaLiberare.setStatoAttuale(true);
            ricovero.setLettoAssegnato(null);
        }

        aggiornaTabelle();
        aggiornaMappaCorrente();
        JOptionPane.showMessageDialog(view.getPanelAmministratore(),
                "Paziente SSN: " + ssnSelezionato + " dimesso correttamente. Salvato nello storico.");
    }

    // =========================================================================
    // GESTIONE ANAGRAFICA
    // =========================================================================
    /**
     * Intercetta la riga selezionata nella tabella anagrafica, converte l'indice del filtro
     * e compila i rispettivi campi di testo della form di inserimento/modifica.
     * <p>
     * Verifica che la selezione sia conclusa mediante {@code !e.getValueIsAdjusting()}.
     * Converte l'indice di riga visualizzato in indice del modello dati, estrae i valori stringa dalle celle e li assegna
     * ai rispettivi JTextField mediante {@code setText()}, forzando temporaneamente la chiave primaria CF a non essere editabile.
     * </p>
     *
     * @param e l'evento di selezione della lista
     */
    private void caricaDatiPazienteSelezionato(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        JTable tablePazienti = anagraficaView.getTablePazienti();
        int rigaSelezionataView = tablePazienti.getSelectedRow();
        if (rigaSelezionataView == -1) return;

        // Stessa cautela del RowSorter: l'indice va convertito da vista a model.
        int rigaSelezionata = tablePazienti.convertRowIndexToModel(rigaSelezionataView);

        DefaultTableModel model = anagraficaView.getTableModel();
        anagraficaView.getTxtCf()     .setText((String) model.getValueAt(rigaSelezionata, COL_SSN));
        anagraficaView.getTxtCf()     .setEditable(false);
        anagraficaView.getTxtNome()   .setText((String) model.getValueAt(rigaSelezionata, COL_NOME));
        anagraficaView.getTxtCognome().setText((String) model.getValueAt(rigaSelezionata, COL_COGNOME));
        anagraficaView.getTxtRecapito().setText((String) model.getValueAt(rigaSelezionata, COL_RECAPITO));
    }
        /**
         * Valida i campi compilati ed inserisce un nuovo paziente nell'anagrafica del DB e della vista.
         * <p>
         * Verifica che la selezione sia conclusa mediante {@code !e.getValueIsAdjusting()}.
         * Converte l'indice di riga visualizzato in indice del modello dati, estrae i valori stringa dalle celle e li assegna
         * ai rispettivi JTextField mediante {@code setText()}, forzando temporaneamente la chiave primaria CF a non essere editabile.
         * </p>
         */
    private void aggiungiPaziente() {
        String cf      = normalizeSsn(anagraficaView.getTxtCf().getText());
        String nome    = anagraficaView.getTxtNome()    .getText().trim();
        String cognome = anagraficaView.getTxtCognome() .getText().trim();
        String recapito = anagraficaView.getTxtRecapito().getText().trim();

        if (cf.isEmpty() || nome.isEmpty() || cognome.isEmpty()) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "Attenzione: SSN, Nome e Cognome sono obbligatori!",
                    "Campi Mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ssnEsisteInAnagrafica(cf)) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "Paziente con questo SSN già registrato!", TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        Paziente nuovoPaziente = new Paziente(cf, nome, cognome, recapito);
        pazienteDAO.save(nuovoPaziente);
        elencoPazienti.add(nuovoPaziente);

        anagraficaView.getTableModel().addRow(new Object[]{cf, nome, cognome, recapito});
        anagraficaView.getBtnPulisci().doClick();
        JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Paziente inserito con successo!");
    }
    /**
     * Applica le modifiche apportate ai dati anagrafici del paziente selezionato aggiornando il database.
     * <p>
     *  Valida la corretta selezione di una riga convertendone l'indice. Campiona le stringhe
     * modificabili dai campi ed esegue un ciclo di ricerca sulla cache {@code elencoPazienti} per localizzare l'oggetto corrispondente
     * tramite il codice fiscale. Aggiorna le proprietà dell'oggetto modello, invoca l'operazione sul database {@code pazienteDAO.update()}
     * e aggiorna manualmente le celle del modello tabellare Swing tramite i comandi {@code setValueAt()}, invocando infine il reset della form.
     * </p>
     */
    private void modificaPaziente() {
        JTable tablePazienti = anagraficaView.getTablePazienti();
        int rigaSelezionataView = tablePazienti.getSelectedRow();
        if (rigaSelezionataView == -1) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "Seleziona prima un paziente dalla tabella!", "Nessuna Selezione", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int rigaSelezionata = tablePazienti.convertRowIndexToModel(rigaSelezionataView);

        String nome    = anagraficaView.getTxtNome()    .getText().trim();
        String cognome = anagraficaView.getTxtCognome() .getText().trim();
        String recapito = anagraficaView.getTxtRecapito().getText().trim();

        if (nome.isEmpty() || cognome.isEmpty()) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "I campi Nome e Cognome non possono essere vuoti!", TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = anagraficaView.getTableModel();
        String cfPaziente = (String) model.getValueAt(rigaSelezionata, COL_SSN);
        for (Paziente p : elencoPazienti) {
            if (p.getCf().equalsIgnoreCase(cfPaziente)) {
                p.setNome(nome);
                p.setCognome(cognome);
                p.setRecapito(recapito);
                pazienteDAO.update(p);
                break;
            }
        }
        model.setValueAt(nome,    rigaSelezionata, COL_NOME);
        model.setValueAt(cognome, rigaSelezionata, COL_COGNOME);
        model.setValueAt(recapito, rigaSelezionata, COL_RECAPITO);

        anagraficaView.getBtnPulisci().doClick();
        JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Dati del paziente aggiornati correttamente!");
    }
    /**
     * Rimuove il paziente selezionato se e solo se non possiede alcuno storico clinico/ricovero pregresso.
     * <p>
     * Determina il codice fiscale della riga selezionata. Esegue un controllo restrittivo
     * di sbarramento analizzando tramite stream {@code elencoRicoveri.stream().anyMatch} se l'SSN compare in un qualsiasi record
     * di degenza (passato o presente). Se viene trovata corrispondenza, blocca l'eliminazione per preservare l'integrità referenziale.
     * In caso contrario, esegue l'operazione distruttiva sul DB {@code pazienteDAO.delete()}, purga la lista locale e rimuove
     * la riga dal modello dati della tabella tramite {@code removeRow()}.
     * </p>
     */
    private void eliminaPaziente() {
        JTable tablePazienti = anagraficaView.getTablePazienti();
        int rigaSelezionataView = tablePazienti.getSelectedRow();
        if (rigaSelezionataView == -1) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "Seleziona un paziente da eliminare.", TITOLO_ERRORE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        int rigaSelezionata = tablePazienti.convertRowIndexToModel(rigaSelezionataView);

        DefaultTableModel model = anagraficaView.getTableModel();
        String cfDaEliminare = (String) model.getValueAt(rigaSelezionata, COL_SSN);

        boolean haStoricoClinico = elencoRicoveri.stream()
                .anyMatch(r -> r.getSsn().equalsIgnoreCase(cfDaEliminare));

        if (haStoricoClinico) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "Impossibile eliminare: il paziente ha uno storico di ricoveri registrato nel sistema.",
                    TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        pazienteDAO.delete(cfDaEliminare);
        elencoPazienti.removeIf(p -> p.getCf().equalsIgnoreCase(cfDaEliminare));
        anagraficaView.getTableModel().removeRow(rigaSelezionata);
        anagraficaView.getBtnPulisci().doClick();
    }
    /**
     * Resetta e svuota tutti i campi di input dell'interfaccia anagrafica riattivando l'editabilità della chiave.
     */
    private void pulisciCampiAnagrafica() {
        anagraficaView.getTxtCf()     .setText("");
        anagraficaView.getTxtCf()     .setEditable(true);
        anagraficaView.getTxtNome()   .setText("");
        anagraficaView.getTxtCognome().setText("");
        anagraficaView.getTxtRecapito().setText("");
        anagraficaView.getTablePazienti().clearSelection();
    }

    // =========================================================================
    // MAPPATURA LETTI
    // =========================================================================
    /**
     * Determina quale reparto è correntemente selezionato nella GUI e ne ordina il ridisegno visivo.
     * <p>
     * Interroga l'elemento selezionato della ComboBox dei reparti:
     * tramite un blocco condizionale {@code instanceof}, convalida l'oggetto e lo inoltra alla funzione di rendering {@code disegnaMappaLetti()}.
     * </p>
     */
    private void aggiornaMappaCorrente() {
        Object selected = gestioneLettiView.getCmbReparti().getSelectedItem();
        if (selected instanceof Reparto) {
            disegnaMappaLetti((Reparto) selected);
        }

    }
    private void filtraLetti() {
        Object selected = gestioneLettiView.getCmbReparti().getSelectedItem();
        if (!(selected instanceof Reparto)) return;

        String filtro = gestioneLettiView.getTxtRicercaLetto().getText().trim().toLowerCase();
        Reparto reparto = (Reparto) selected;

        JPanel mappa = gestioneLettiView.getPanelMappaLetti();
        mappa.removeAll();

        if (reparto.getStanze() != null) {
            for (Stanza stanza : reparto.getStanze()) {
                List<Letto> letti = stanza.getLetti();
                if (letti == null) continue;

                List<Letto> lettiFiltrati = filtro.isEmpty()
                        ? letti
                        : letti.stream()
                        .filter(l -> l.getCodiceInventario().toLowerCase().contains(filtro))
                        .collect(java.util.stream.Collectors.toList());

                if (lettiFiltrati.isEmpty()) continue;

                JPanel panelStanza = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
                panelStanza.setBorder(BorderFactory.createTitledBorder("Stanza " + stanza.getNumero()));
                panelStanza.setAlignmentX(Component.LEFT_ALIGNMENT);
                lettiFiltrati.forEach(letto -> panelStanza.add(creaLabelLetto(letto)));
                mappa.add(panelStanza);
            }
        }

        mappa.revalidate();
        mappa.repaint();
    }
    /**
     * Rigenera dinamicamente i componenti grafici rappresentanti le stanze e i rispettivi letti del reparto.
     * <p>
     * <b>Meccanismo di funzionamento:</b> Estrae il pannello contenitore della mappa e lo svuota totalmente tramite {@code removeAll()}.
     * Esegue un ciclo {@code for} sulle stanze del reparto: instanzia per ciascuna un sotto-pannello JPanel configurato con layout
     * {@link FlowLayout} allineato a sinistra e vi applica un bordo titolato con il numero di stanza. Un ciclo interno scorre i letti della
     * stanza ed aggiunge al pannello della stanza i componenti JLabel generati dalla funzione fabbrica {@code creaLabelLetto(letto)}.
     * Al termine dei cicli inserisce i blocchi nel contenitore radice ed invoca {@code revalidate()} e {@code repaint()}.
     * </p>
     *
     * @param reparto l'oggetto reparto da mappare a schermo
     */
    private void disegnaMappaLetti(Reparto reparto) {
        JPanel mappa = gestioneLettiView.getPanelMappaLetti();
        mappa.removeAll();

        if (reparto != null && reparto.getStanze() != null) {
            for (Stanza stanza : reparto.getStanze()) {
                JPanel panelStanza = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
                panelStanza.setBorder(BorderFactory.createTitledBorder("Stanza " + stanza.getNumero()));
                panelStanza.setAlignmentX(Component.LEFT_ALIGNMENT);

                if (stanza.getLetti() != null) {
                    stanza.getLetti().forEach(letto -> panelStanza.add(creaLabelLetto(letto)));
                }
                mappa.add(panelStanza);
            }
        }

        mappa.revalidate();
        mappa.repaint();
    }
    /**
     * Istanzia un componente JLabel configurato ad-hoc per riflettere cromaticamente lo stato del letto (libero/occupato).
     * </p>
     * Istanzia un oggetto JLabel impostando opacità e dimensioni fisse. Campiona la data attuale
     * di sistema ed esegue un controllo predittivo tramite stream sulla lista dei ricoveri complessivi: un letto viene considerato occupato
     * *solo* se vi è un ricovero in corso legato al codice del letto *E* la data odierna non precede la data d'inizio del ricovero stesso.
     * Se il test è negativo (il letto è libero o bloccato per ricoveri futuri), colora lo sfondo di verde chiaro. Se il test è positivo,
     * colora lo sfondo di rosso, modifica il cursore a forma di mano e vi aggancia un {@link MouseAdapter} per catturare i click ed
     * esporre i dettagli clinici.
     * </p>
     *
     * @param letto il modello del letto da configurare visivamente
     * @return un'etichetta Swing configurata
     */
    private JLabel creaLabelLetto(Letto letto) {
        JLabel lblLetto = new JLabel(letto.getCodiceInventario(), SwingConstants.CENTER);
        lblLetto.setOpaque(true);
        lblLetto.setPreferredSize(new Dimension(80, 50));
        lblLetto.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        Date oggi = new Date();
        boolean occupatoOggi = elencoRicoveri.stream()
                .anyMatch(r -> r.getLettoAssegnato() != null
                        && r.getLettoAssegnato().getCodiceInventario().equals(letto.getCodiceInventario())
                        && r.isInCorso()
                        && !oggi.before(r.getDataRicovero()));
        if (!occupatoOggi) {
            lblLetto.setBackground(new Color(144, 238, 144));
            lblLetto.setForeground(Color.BLACK);
            lblLetto.setToolTipText("Letto disponibile");
        } else {
            lblLetto.setBackground(new Color(255, 99, 71));
            lblLetto.setForeground(Color.WHITE);
            lblLetto.setToolTipText("Letto occupato (Clicca per dettagli)");
            lblLetto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblLetto.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mostraDettagliPazienteLetto(letto);
                }
            });
        }
        return lblLetto;
    }
    /**
     * Ricerca e mostra in una finestra informativa i dettagli anagrafici e clinici del paziente che occupa il letto selezionato.
     * <p>
     * Filtra i ricoveri locali intercettando quello che possiede il letto occupato in oggetto.
     * Recupera nome e cognome tramite il sotto-metodo di cache, formatta i limiti temporali e compone una stringa testuale
     * riassuntiva multiriga, proiettandola a schermo tramite una finestra di messaggio informativa {@link JOptionPane#showMessageDialog}.
     * </p>
     *
     * @param lettoOccupato l'oggetto letto su cui è stato effettuato il clic
     */
    private void mostraDettagliPazienteLetto(Letto lettoOccupato) {
        Ricovero ricovero = elencoRicoveri.stream()
                .filter(r -> r.getLettoAssegnato() != null &&
                        r.getLettoAssegnato().getCodiceInventario()
                                .equals(lettoOccupato.getCodiceInventario()))
                .findFirst()
                .orElse(null);

        if (ricovero == null) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Nessun ricovero associato a questo letto nel sistema.",
                    "Errore Dati", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomeCognome = cercaNomeCognomeBySsn(ricovero.getSsn());
        String dataInizio    = formatData(ricovero.getDataRicovero());
        String dataFine      = formatData(ricovero.getDataDimissionePrevista());
        String tipoRicovero  = ricovero.isDayHospital() ? "Day Hospital" : "Ricovero ordinario";
        String descrizione   = (ricovero.getDescrizione() == null || ricovero.getDescrizione().isBlank())
                ? "-" : ricovero.getDescrizione();

        String messaggio = "Dettagli Ricovero:\n\n"
                + "Letto: "               + lettoOccupato.getCodiceInventario() + "\n"
                + "Paziente: "            + nomeCognome[0] + " " + nomeCognome[1] + "\n"
                + "SSN: "                 + ricovero.getSsn() + "\n"
                + "Tipo: "                + tipoRicovero + "\n"
                + "Data Ricovero: "       + dataInizio + "\n"
                + "Dimissione Prevista: " + dataFine + "\n"
                + "Diagnosi: "            + ricovero.getDiagnosiEntrata() + "\n"
                + "Note: "                + descrizione;

        JOptionPane.showMessageDialog(mainFrame, messaggio,
                "Info Paziente - Letto " + lettoOccupato.getCodiceInventario(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Svuota e ripopola integralmente sia la tabella riassuntiva dei ricoveri storici sia quella delle dimissioni attive.
     * <p>
     * Invece di eseguire interrogazioni SQL ripetitive nel ciclo loop, invoca preliminarmente
     * un'unica interrogazione collettiva batch chiamata {@code prestazioneMedicaDAO.findMedicoAssegnatoBatch(elencoRicoveri)},
     * memorizzando le associazioni in una mappa temporanea. Azzera i modelli tabellari delle griglie visive Swing.
     * Avvia un ciclo su {@code elencoRicoveri} ricavando l'anno d'ammissione tramite un oggetto {@link Calendar}.
     * Per i ricoveri attivi, interroga la mappa locale tramite il comando {@code mediciAssegnati.getOrDefault()} riducendo
     * drasticamente il numero complessivo di accessi al database e inserisce le righe risultanti nei modelli dati delle tabelle.
     * </p>
     */
    private void aggiornaTabelle() {
        tableModelRicoveri  .setRowCount(0);
        tableModelDimissioni.setRowCount(0);

        Calendar cal = Calendar.getInstance();

        // Risolviamo il medico assegnato per TUTTI i ricoveri con un'unica query batch,
        // invece di interrogare il database una volta per ogni ricovero dentro il loop.
        Map<String, String> mediciAssegnati = prestazioneMedicaDAO.findMedicoAssegnatoBatch(elencoRicoveri);

        for (Ricovero ricovero : elencoRicoveri) {
            String[] nomeCognome = cercaNomeCognomeBySsn(ricovero.getSsn());

            String annoStr    = "-";
            if (ricovero.getDataRicovero() != null) {
                cal.setTime(ricovero.getDataRicovero());
                annoStr = String.valueOf(cal.get(Calendar.YEAR));
            }

            String stato      = ricovero.isInCorso() ? "In Corso" : "Dimesso";
            String dataEntrata = formatData(ricovero.getDataRicovero());
            String dataUscita;
            if (ricovero.isInCorso()) {
                dataUscita = "(Prevista: " + formatData(ricovero.getDataDimissionePrevista()) + ")";
            } else {
                dataUscita = formatData(ricovero.getDataDimissioneEffettiva());
            }

            tableModelRicoveri.addRow(new Object[]{
                    annoStr, stato, ricovero.getSsn(), dataEntrata,
                    dataUscita, ricovero.getDiagnosiEntrata(), ricovero.getDiagnosiUscita()
            });

            if (ricovero.isInCorso()) {
                String infoLetto    = ricovero.getLettoAssegnato() != null
                        ? ricovero.getLettoAssegnato().getCodiceInventario() : "-";
                String nomeReparto  = trovaNomeReparto(infoLetto);
                String medicoAssegnato = mediciAssegnati.getOrDefault(normalizeSsn(ricovero.getSsn()), "Da assegnare");

                tableModelDimissioni.addRow(new Object[]{
                        ricovero.getSsn(), nomeCognome[0], nomeCognome[1],
                        nomeReparto, infoLetto, medicoAssegnato, dataUscita
                });
            }
        }
    }

    // =========================================================================
    // METODI UTILI
    // =========================================================================
    /**
     * Formatta un oggetto Date nel formato stringa definito locale.
     *
     * @param data l'oggetto Date da formattare
     * @return stringa formattata o "-" se la data è nulla
     */
    private String formatData(Date data) {
        return data != null ? SDF.format(data) : "-";
    }
    /**
     * Cerca in cache locale il nome e cognome associati a un codice fiscale.
     *
     * @param ssn il codice fiscale da ricercare
     * @return un array di due stringhe [Nome, Cognome]
     */
    private String[] cercaNomeCognomeBySsn(String ssn) {
        for (Paziente p : elencoPazienti) {
            if (p.getCf().equalsIgnoreCase(ssn)) {
                return new String[]{p.getNome(), p.getCognome()};
            }
        }
        return new String[]{"Non registrato in anagrafica", ""};
    }
    /**
     * Verifica la presenza di un codice fiscale nell'elenco dei pazienti registrati.
     *
     * @param ssn il codice fiscale da controllare
     * @return true se presente, false altrimenti
     */
    private boolean ssnEsisteInAnagrafica(String ssn) {
        return elencoPazienti.stream().anyMatch(p -> p.getCf().equalsIgnoreCase(ssn));
    }
    /**
     * Verifica se un determinato paziente ha un ricovero attualmente attivo.
     *
     * @param ssn il codice fiscale del paziente
     * @return true se il paziente risulta già ricoverato, false altrimenti
     */
    private boolean pazienteGiaRicoverato(String ssn) {
        return elencoRicoveri.stream()
                .anyMatch(r -> r.isInCorso() && r.getSsn().equalsIgnoreCase(ssn));
    }
    /**
     * Recupera l'oggetto Ricovero attivo associato all'SSN del paziente.
     *
     * @param ssn il codice fiscale del paziente
     * @return l'istanza di ricovero in corso, null altrimenti
     */
    private Ricovero trovaRicoveroAttivo(String ssn) {
        return elencoRicoveri.stream()
                .filter(r -> r.isInCorso() && r.getSsn() .equalsIgnoreCase(ssn))
                .findFirst()
                .orElse(null);
    }
    /**
     * Individua il nome del reparto di appartenenza di un determinato letto partendo dal codice inventario.
     * <p>
     * Esegue una scansione ad albero strutturata su tre livelli nidificati:
     * analizza i reparti della lista, le stanze di ciascun reparto ed i singoli letti di ogni stanza. Se rileva
     * l'uguaglianza del codice d'inventario arresta i cicli e restituisce immediatamente la stringa del nome del reparto.
     * </p>
     *
     * @param codiceInventario il codice identificativo del letto
     * @return il nome del reparto o "Non assegnato"
     */
    private String trovaNomeReparto(String codiceInventario) {
        if ("-".equals(codiceInventario) || listaReparti == null) return "Non assegnato";
        for (Reparto reparto : listaReparti) {
            for (Stanza stanza : reparto.getStanze()) {
                for (Letto letto : stanza.getLetti()) {
                    if (letto.getCodiceInventario().equals(codiceInventario)) {
                        return reparto.getNome();
                    }
                }
            }
        }
        return "Non assegnato";
    }
    /**
     * Normalizza la stringa SSN rimuovendo spazi e forzando il maiuscolo.
     *
     * @param ssn la stringa grezza dell'SSN
     * @return stringa normalizzata, o vuota se l'input è nullo
     */
    private String normalizeSsn(String ssn) {
        return ssn == null ? "" : ssn.trim().toUpperCase();
    }
    /**
     * Normalizza il testo della diagnosi applicando un valore di default se vuoto.
     *
     * @param diagnosi la stringa di diagnosi inserita
     * @return stringa normalizzata o "Non specificata"
     */
    private String normalizeDiagnosi(String diagnosi) {
        return (diagnosi == null || diagnosi.trim().isEmpty()) ? "Non specificata" : diagnosi.trim();
    }

    //Classe per forzare SSN MAIUSC
    /**
     * Filtro per documenti testuali Swing delegato alla conversione forzata di caratteri minuscoli in maiuscoli.
     */
    public static final class UpperCaseDocumentFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            if (text != null) {
                super.insertString(fb, offset, text.toUpperCase(), attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                throws BadLocationException {
            if (text != null) {
                super.replace(fb, offset, length, text.toUpperCase(), attr);
            }
        }
    }
}