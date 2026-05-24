package controller;

import gui.InterfacciaAmministratore;
import gui.RegistraDimissione;
import gui.RegistraRicovero;
import gui.GestioneAnagraficaPazienti;
import gui.GestioneLetti;

import model.Amministratore;
import model.Reparto;
import model.Letto;
import model.Stanza;

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
import java.util.logging.Logger;

class BackupRicoveroMemoria {

    private static final String DEFAULT_DIAGNOSI = "-";

    String ssn;
    Date dataRicovero;
    Date dataDimissionePrevista;
    Date dataDimissioneEffettiva;
    Letto lettoAssegnato;
    String diagnosiEntrata;
    String diagnosiUscita;
    boolean inCorso;

    BackupRicoveroMemoria(String ssn, Date dataRicovero, Date dataDimissionePrevista,
                          Letto lettoAssegnato, String diagnosiEntrata) {
        this.ssn                   = ssn;
        this.dataRicovero          = dataRicovero;
        this.dataDimissionePrevista = dataDimissionePrevista;
        this.lettoAssegnato        = lettoAssegnato;
        this.diagnosiEntrata       = diagnosiEntrata;
        this.diagnosiUscita        = DEFAULT_DIAGNOSI;
        this.inCorso               = true;
    }
}

// ---------------------------------------------------------------------------
// AdminController
// ---------------------------------------------------------------------------
public class AdminController {

    // ────────────────────────────────────────────────────────────
    private static final Logger LOGGER           = Logger.getLogger(AdminController.class.getName());
    private static final String         TITOLO_ERRORE    = "Errore";
    private static final String         DATE_FORMAT      = "dd/MM";
    private static final SimpleDateFormat SDF            = new SimpleDateFormat(DATE_FORMAT);

    // Indici table anagrafica
    private static final int COL_SSN      = 0;
    private static final int COL_NOME     = 1;
    private static final int COL_COGNOME  = 2;
    private static final int COL_RECAPITO = 3;

    // ────────────────────────────────────────────────────────────────
    private final InterfacciaAmministratore view;
    private final Amministratore            admin;
    private final JFrame                    mainFrame;
    private final List<Reparto>             listaReparti;
    private final List<BackupRicoveroMemoria> elencoRicoveri = new ArrayList<>();

    private GestioneAnagraficaPazienti anagraficaView;
    private GestioneLetti              gestioneLettiView;
    private DefaultTableModel          tableModelRicoveri;
    private DefaultTableModel          tableModelDimissioni;

    // ──────────────────────────────────────────────────────────
    public AdminController(InterfacciaAmministratore view, Amministratore admin, JFrame mainFrame) {
        this.view        = view;
        this.admin       = admin;
        this.mainFrame   = mainFrame;
        this.listaReparti = generaDatiOspedale();

        inizializzaAnagrafica();
        inizializzaGestioneLetti();
        inizializzaTabella();
        inizializzaAzioni();
        inizializzaFiltriDimissioni();
    }

    // =========================================================================
    // METODI INIZIALIZZA
    // =========================================================================

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
        }

        if (!listaReparti.isEmpty()) {
            aggiornaMappaCorrente();
        }
    }

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

    private DefaultTableModel buildNonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

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

    // =========================================================================
    // METODI GESTIONE AZIONI

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
        }
    }

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
        if (lettoScelto != null) {
            lettoScelto.setStatoAttuale(false);
        }

        BackupRicoveroMemoria nuovoRicovero = new BackupRicoveroMemoria(
                normalizeSsn(dialogRicovero.getSSN()),
                dialogRicovero.getDateChooserRicovero().getDate(),
                dialogRicovero.getDateChooserDimissione().getDate(),
                lettoScelto,
                diagnosiEntrata
        );

        elencoRicoveri.add(nuovoRicovero);
        aggiornaTabelle();
        aggiornaMappaCorrente();
        JOptionPane.showMessageDialog(view.getPanelAmministratore(),
                "Ricovero registrato e inserito nei pannelli!");
    }

    private void gestisciDimissione(JFrame parentFrame) {
        JTable tabellaDimissioni = view.getDimissioniTable();
        int rigaSelezionata = tabellaDimissioni.getSelectedRow();

        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Seleziona un paziente dalla tabella nella scheda 'Dimissione pazienti' per procedere!",
                    "Nessuna selezione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ssnSelezionato = normalizeSsn((String) tabellaDimissioni.getValueAt(rigaSelezionata, 0));
        BackupRicoveroMemoria ricovero = trovaRicoveroAttivo(ssnSelezionato);
        if (ricovero == null) return;

        RegistraDimissione dialogDimissione =
                new RegistraDimissione(parentFrame, ricovero.dataDimissionePrevista, ssnSelezionato);
        dialogDimissione.getAnnullaButton().addActionListener(a -> dialogDimissione.dispose());
        dialogDimissione.getSalvaButton().addActionListener(s -> {
            dialogDimissione.setConfermato(true);
            dialogDimissione.dispose();
        });

        dialogDimissione.setVisible(true);

        if (!dialogDimissione.isConfermato()) return;

        // Read diagnosis silently from the form — no popup
        String diagnosiUscita = normalizeDiagnosi(dialogDimissione.getDiagnosiUscita());

        ricovero.inCorso               = false;
        ricovero.diagnosiUscita        = diagnosiUscita;
        ricovero.dataDimissioneEffettiva = new Date();

        if (ricovero.lettoAssegnato != null) {
            ricovero.lettoAssegnato.setStatoAttuale(true);
            ricovero.lettoAssegnato = null;
        }

        aggiornaTabelle();
        aggiornaMappaCorrente();
        JOptionPane.showMessageDialog(view.getPanelAmministratore(),
                "Paziente SSN: " + ssnSelezionato + " dimesso correttamente. Salvato nello storico.");
    }

    // =========================================================================
    // GESTIONE ANAGRAFICA
    // =========================================================================

    private void caricaDatiPazienteSelezionato(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        int rigaSelezionata = anagraficaView.getTablePazienti().getSelectedRow();
        if (rigaSelezionata == -1) return;

        DefaultTableModel model = anagraficaView.getTableModel();
        anagraficaView.getTxtCf()     .setText((String) model.getValueAt(rigaSelezionata, COL_SSN));
        anagraficaView.getTxtCf()     .setEditable(false);
        anagraficaView.getTxtNome()   .setText((String) model.getValueAt(rigaSelezionata, COL_NOME));
        anagraficaView.getTxtCognome().setText((String) model.getValueAt(rigaSelezionata, COL_COGNOME));
        anagraficaView.getTxtRecapito().setText((String) model.getValueAt(rigaSelezionata, COL_RECAPITO));
    }

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

        anagraficaView.getTableModel().addRow(new Object[]{cf, nome, cognome, recapito});
        anagraficaView.getBtnPulisci().doClick();
        JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Paziente inserito con successo!");
    }

    private void modificaPaziente() {
        int rigaSelezionata = anagraficaView.getTablePazienti().getSelectedRow();
        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "Seleziona prima un paziente dalla tabella!", "Nessuna Selezione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome    = anagraficaView.getTxtNome()    .getText().trim();
        String cognome = anagraficaView.getTxtCognome() .getText().trim();
        String recapito = anagraficaView.getTxtRecapito().getText().trim();

        if (nome.isEmpty() || cognome.isEmpty()) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "I campi Nome e Cognome non possono essere vuoti!", TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = anagraficaView.getTableModel();
        model.setValueAt(nome,    rigaSelezionata, COL_NOME);
        model.setValueAt(cognome, rigaSelezionata, COL_COGNOME);
        model.setValueAt(recapito, rigaSelezionata, COL_RECAPITO);

        anagraficaView.getBtnPulisci().doClick();
        JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Dati del paziente aggiornati correttamente!");
    }

    private void eliminaPaziente() {
        int rigaSelezionata = anagraficaView.getTablePazienti().getSelectedRow();
        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(),
                    "Seleziona un paziente da eliminare.", TITOLO_ERRORE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        anagraficaView.getTableModel().removeRow(rigaSelezionata);
        anagraficaView.getBtnPulisci().doClick();
    }

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

    private void aggiornaMappaCorrente() {
        Object selected = gestioneLettiView.getCmbReparti().getSelectedItem();
        if (selected instanceof Reparto) {
            disegnaMappaLetti((Reparto) selected);
        }
    }

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

    private JLabel creaLabelLetto(Letto letto) {
        JLabel lblLetto = new JLabel(letto.getCodiceInventario(), SwingConstants.CENTER);
        lblLetto.setOpaque(true);
        lblLetto.setPreferredSize(new Dimension(80, 50));
        lblLetto.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        if (letto.isLibero()) {
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

    private void mostraDettagliPazienteLetto(Letto lettoOccupato) {
        BackupRicoveroMemoria ricovero = elencoRicoveri.stream()
                .filter(r -> r.lettoAssegnato != null &&
                        r.lettoAssegnato.getCodiceInventario()
                                .equals(lettoOccupato.getCodiceInventario()))
                .findFirst()
                .orElse(null);

        if (ricovero == null) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Nessun ricovero associato a questo letto nel sistema.",
                    "Errore Dati", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomeCognome = cercaNomeCognomeBySsn(ricovero.ssn);
        String dataInizio    = formatData(ricovero.dataRicovero);
        String dataFine      = formatData(ricovero.dataDimissionePrevista);

        String messaggio = "Dettagli Ricovero:\n\n"
                + "Letto: "               + lettoOccupato.getCodiceInventario() + "\n"
                + "Paziente: "            + nomeCognome[0] + " " + nomeCognome[1] + "\n"
                + "SSN: "                 + ricovero.ssn + "\n"
                + "Data Ricovero: "       + dataInizio + "\n"
                + "Dimissione Prevista: " + dataFine;

        JOptionPane.showMessageDialog(mainFrame, messaggio,
                "Info Paziente - Letto " + lettoOccupato.getCodiceInventario(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // AGGIORNAMENTO TABELLE
    // =========================================================================

    private void aggiornaTabelle() {
        tableModelRicoveri  .setRowCount(0);
        tableModelDimissioni.setRowCount(0);

        Calendar cal = Calendar.getInstance();

        for (BackupRicoveroMemoria ricovero : elencoRicoveri) {
            String[] nomeCognome = cercaNomeCognomeBySsn(ricovero.ssn);

            String annoStr    = "-";
            if (ricovero.dataRicovero != null) {
                cal.setTime(ricovero.dataRicovero);
                annoStr = String.valueOf(cal.get(Calendar.YEAR));
            }

            String stato      = ricovero.inCorso ? "In Corso" : "Dimesso";
            String dataEntrata = formatData(ricovero.dataRicovero);
            String dataUscita;
            if (ricovero.inCorso) {
                dataUscita = "(Prevista: " + formatData(ricovero.dataDimissionePrevista) + ")";
            } else {
                dataUscita = formatData(ricovero.dataDimissioneEffettiva);
            }

            tableModelRicoveri.addRow(new Object[]{
                    annoStr, stato, ricovero.ssn, dataEntrata,
                    dataUscita, ricovero.diagnosiEntrata, ricovero.diagnosiUscita
            });

            if (ricovero.inCorso) {
                String infoLetto    = ricovero.lettoAssegnato != null
                        ? ricovero.lettoAssegnato.getCodiceInventario() : "-";
                String nomeReparto  = trovaNomeReparto(infoLetto);

                tableModelDimissioni.addRow(new Object[]{
                        ricovero.ssn, nomeCognome[0], nomeCognome[1],
                        nomeReparto, infoLetto, "Da assegnare", dataUscita
                });
            }
        }
    }

    // =========================================================================
    // METODI UTILI
    // =========================================================================

    private String formatData(Date data) {
        return data != null ? SDF.format(data) : "-";
    }

    private String[] cercaNomeCognomeBySsn(String ssn) {
        if (anagraficaView == null || anagraficaView.getTableModel() == null) {
            return new String[]{"N/D", "N/D"};
        }
        DefaultTableModel model = anagraficaView.getTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, COL_SSN);
            if (val != null && val.toString().equalsIgnoreCase(ssn)) {
                return new String[]{
                        (String) model.getValueAt(i, COL_NOME),
                        (String) model.getValueAt(i, COL_COGNOME)
                };
            }
        }
        return new String[]{"Non registrato in anagrafica", ""};
    }

    private boolean ssnEsisteInAnagrafica(String ssn) {
        if (anagraficaView == null || anagraficaView.getTableModel() == null) return false;
        DefaultTableModel model = anagraficaView.getTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, COL_SSN);
            if (val != null && val.toString().equalsIgnoreCase(ssn)) return true;
        }
        return false;
    }

    private boolean pazienteGiaRicoverato(String ssn) {
        return elencoRicoveri.stream()
                .anyMatch(r -> r.inCorso && r.ssn.equalsIgnoreCase(ssn));
    }

    private BackupRicoveroMemoria trovaRicoveroAttivo(String ssn) {
        return elencoRicoveri.stream()
                .filter(r -> r.inCorso && r.ssn.equalsIgnoreCase(ssn))
                .findFirst()
                .orElse(null);
    }

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

    private String normalizeSsn(String ssn) {
        return ssn == null ? "" : ssn.trim().toUpperCase();
    }

    private String normalizeDiagnosi(String diagnosi) {
        return (diagnosi == null || diagnosi.trim().isEmpty()) ? "Non specificata" : diagnosi.trim();
    }

    // =========================================================================
    // Dati ospedale fittizio (no db)
    // =========================================================================

    private List<Reparto> generaDatiOspedale() {
        List<Reparto> reparti = new ArrayList<>();

        reparti.add(creaReparto(10, "Cardiologia",
                creaStanza(101,  "L1", "L2", "L22"),
                creaStanza(111,  "L1", "L2", "L22"),
                creaStanza(1011, "L1", "L2", "L22")));

        reparti.add(creaReparto(20, "Chirurgia",
                creaStanza(201, "L3", "L4")));

        reparti.add(creaReparto(30, "Pediatria",
                creaStanza(301, "L5", "L6")));

        reparti.add(creaReparto(40, "Ortopedia",
                creaStanza(401, "L7", "L8")));

        reparti.add(creaReparto(50, "Neurologia",
                creaStanza(501, "L9", "L10")));

        reparti.add(creaReparto(60, "Oncologia",
                creaStanza(601, "L12", "L13")));

        reparti.add(creaReparto(70, "Ginecologia",
                creaStanza(701, "L14", "L15")));

        reparti.add(creaReparto(80, "Terapia Intensiva",
                creaStanza(801, "L16", "L17")));

        reparti.add(creaReparto(90, "Psichiatria",
                creaStanza(901, "L18", "L19")));

        return reparti;
    }

    private Reparto creaReparto(int id, String nome, Stanza... stanze) {
        Reparto reparto = new Reparto(id, nome);
        for (Stanza s : stanze) {
            reparto.aggiungiStanza(s);
        }
        return reparto;
    }

    private Stanza creaStanza(int numero, String... codiciBed) {
        Stanza stanza = new Stanza(numero);
        for (String codice : codiciBed) {
            stanza.aggiungiLetto(new Letto(codice, true));
        }
        return stanza;
    }

    //Classe per forzare SSN MAIUSC
    
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