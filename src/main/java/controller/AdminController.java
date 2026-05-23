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
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

//Classe temporanea per far funzionare il programma

class BackupRicoveroMemoria {
    String ssn;
    Date dataRicovero;
    Date dataDimissionePrevista;
    Letto lettoAssegnato;

    public BackupRicoveroMemoria(String ssn, Date dataRicovero, Date dataDimissionePrevista, Letto lettoAssegnato) {
        this.ssn = ssn;
        this.dataRicovero = dataRicovero;
        this.dataDimissionePrevista = dataDimissionePrevista;
        this.lettoAssegnato = lettoAssegnato;
    }
}

public class AdminController {
    private InterfacciaAmministratore view;
    private Amministratore admin;
    private List<Reparto> listaReparti;
    private List<BackupRicoveroMemoria> elencoRicoveri;
    private DefaultTableModel tableModelRicoveri;
    private DefaultTableModel tableModelDimissioni;
    private GestioneAnagraficaPazienti anagraficaView;
    private JFrame mainFrame;
    private GestioneLetti gestioneLettiView;
    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());
    private static final String TITOLO_ERRORE = "Errore";

    public AdminController(InterfacciaAmministratore view, Amministratore admin, JFrame mainFrame) {
        this.view = view;
        this.admin = admin;
        this.mainFrame = mainFrame;
        this.elencoRicoveri = new ArrayList<>();
        this.listaReparti = generaDatiOspedale();

        inizializzaAnagrafica();
        inizializzaGestioneLetti();
        inizializzaTabella();
        inizializzaAzioni();
        inizializzaFiltriDimissioni();
        }

    private void inizializzaAnagrafica() {
        this.anagraficaView = new GestioneAnagraficaPazienti();

        if (view.getTabbedPane() != null && anagraficaView.getMainPanel() != null) {
            view.getTabbedPane().setComponentAt(0, anagraficaView.getMainPanel());

            inizializzaAzioniAnagrafica();
        } else {
            System.err.println("ERRORE: I pannelli anagrafica sono NULL.");
        }
    }

    private void inizializzaGestioneLetti() {
        this.gestioneLettiView = new GestioneLetti();

        if (view.getTabbedPane() != null && gestioneLettiView.getMainPanel() != null) {
            // Inietta il pannello nell'indice 1 (che è "Disponibilità letti")
            view.getTabbedPane().setComponentAt(1, gestioneLettiView.getMainPanel());

            if (gestioneLettiView.getCmbReparti() != null && listaReparti != null) {
                for (Reparto r : listaReparti) {
                    gestioneLettiView.getCmbReparti().addItem(r);
                }
            }

            // Ascolta quando cambi reparto dalla tendina per ridisegnare
            gestioneLettiView.getCmbReparti().addActionListener(e -> aggiornaMappaCorrente());

            // Disegna subito per il primo reparto visibile
            if (!listaReparti.isEmpty()) {
                aggiornaMappaCorrente();
            }
        }
    }

    private void aggiornaMappaCorrente() {
        if (gestioneLettiView.getCmbReparti().getSelectedItem() != null) {
            disegnaMappaLetti((Reparto) gestioneLettiView.getCmbReparti().getSelectedItem());
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
                    for (Letto letto : stanza.getLetti()) {

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

                            lblLetto.addMouseListener(new java.awt.event.MouseAdapter() {
                                @Override
                                public void mouseClicked(java.awt.event.MouseEvent e) {
                                    mostraDettagliPazienteLetto(letto);
                                }
                            });
                        }
                        panelStanza.add(lblLetto);
                    }
                }
                mappa.add(panelStanza);
            }
        }
        mappa.revalidate();
        mappa.repaint();
    }

    private void mostraDettagliPazienteLetto(Letto lettoOccupato) {
            BackupRicoveroMemoria ricoveroTrovato = null;

            for (BackupRicoveroMemoria r : elencoRicoveri) {
                if (r.lettoAssegnato != null && r.lettoAssegnato.getCodiceInventario().equals(lettoOccupato.getCodiceInventario())) {
                    ricoveroTrovato = r;
                    break;
                }
            }

            if (ricoveroTrovato != null) {
                String nome = "Non registrato in anagrafica";
                String cognome = "";

                for (int i = 0; i < anagraficaView.getTableModel().getRowCount(); i++) {
                    String ssnTabella = (String) anagraficaView.getTableModel().getValueAt(i, 0);
                    if (ssnTabella.equalsIgnoreCase(ricoveroTrovato.ssn)) {
                        nome = (String) anagraficaView.getTableModel().getValueAt(i, 1);
                        cognome = (String) anagraficaView.getTableModel().getValueAt(i, 2);
                        break;
                    }
                }

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                String dataInizio = ricoveroTrovato.dataRicovero != null ? sdf.format(ricoveroTrovato.dataRicovero) : "-";
                String dataFine = ricoveroTrovato.dataDimissionePrevista != null ? sdf.format(ricoveroTrovato.dataDimissionePrevista) : "-";

                String messaggio = "Dettagli Ricovero:\n\n" +
                        "Letto: " + lettoOccupato.getCodiceInventario() + "\n" +
                        "Paziente: " + nome + " " + cognome + "\n" +
                        "SSN: " + ricoveroTrovato.ssn + "\n" +
                        "Data Ricovero: " + dataInizio + "\n" +
                        "Dimissione Prevista: " + dataFine;

                JOptionPane.showMessageDialog(
                        mainFrame,
                        messaggio,
                        "Info Paziente - Letto " + lettoOccupato.getCodiceInventario(),
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Nessun ricovero associato a questo letto nel sistema.",
                        "Errore Dati",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }

    private void inizializzaTabella() {
        String[] colonne = {"SSN Paziente", "Data Ricovero", "Dimissione Prevista", "Letto Assegnato"};
        tableModelRicoveri = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;} // Rende le celle non modificabili al doppio click
        };
        if (view.getRicoveriTable() != null) {
            view.getRicoveriTable().setModel(tableModelRicoveri);
        }

        String[] colonne1 = {"SSN Paziente", "Data Dimissione Prevista"};
        tableModelDimissioni = new DefaultTableModel(colonne1, 0){
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        if(view.getDimissioniTable() != null){
            view.getDimissioniTable().setModel(tableModelDimissioni);
        }
    }

    private void aggiornaTabelle() {
        tableModelRicoveri.setRowCount(0); // Svuota la vista grafica
        tableModelDimissioni.setRowCount(0);

        java.text.SimpleDateFormat formatoData = new java.text.SimpleDateFormat("dd/MM/yyyy");

        for (BackupRicoveroMemoria r : elencoRicoveri) {
            String dataRicoveroStr = r.dataRicovero != null ? formatoData.format(r.dataRicovero) : "-";
            String dataDimissioneStr = r.dataDimissionePrevista != null ? formatoData.format(r.dataDimissionePrevista) : "-";
            String infoLetto = r.lettoAssegnato != null ? r.lettoAssegnato.toString() : "Nessuno";

            Object[] rigaRicovero = { r.ssn, dataRicoveroStr, dataDimissioneStr, infoLetto };
            tableModelRicoveri.addRow(rigaRicovero);

            Object[] rigaDimissione = { r.ssn, dataDimissioneStr };
            tableModelDimissioni.addRow(rigaDimissione);
        }
    }

    private void inizializzaAzioniAnagrafica() {
        if (anagraficaView.getTablePazienti() != null) {
            anagraficaView.getTablePazienti().getSelectionModel().addListSelectionListener(this::caricaDatiPazienteSelezionato);
        }

        anagraficaView.getBtnAggiungi().addActionListener(e -> aggiungiPaziente());
        anagraficaView.getBtnModifica().addActionListener(e -> modificaPaziente());
        anagraficaView.getBtnElimina().addActionListener(e -> eliminaPaziente());
        anagraficaView.getBtnPulisci().addActionListener(e -> pulisciCampiAnagrafica());

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(anagraficaView.getTableModel());
            anagraficaView.getTablePazienti().setRowSorter(sorter);


            anagraficaView.getBtnCerca().addActionListener(e -> {
                String testoRicerca = anagraficaView.getTxtRicerca().getText().trim();
                if (testoRicerca.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + testoRicerca));
                }
            });
            anagraficaView.getTxtRicerca().addActionListener(e -> anagraficaView.getBtnCerca().doClick());
        }

    private void caricaDatiPazienteSelezionato(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int rigaSelezionata = anagraficaView.getTablePazienti().getSelectedRow();
            if (rigaSelezionata != -1) {
                String cf = (String) anagraficaView.getTableModel().getValueAt(rigaSelezionata, 0);
                String nome = (String) anagraficaView.getTableModel().getValueAt(rigaSelezionata, 1);
                String cognome = (String) anagraficaView.getTableModel().getValueAt(rigaSelezionata, 2);
                String recapito = (String) anagraficaView.getTableModel().getValueAt(rigaSelezionata, 3);

                anagraficaView.getTxtCf().setText(cf);
                anagraficaView.getTxtCf().setEditable(false);
                anagraficaView.getTxtNome().setText(nome);
                anagraficaView.getTxtCognome().setText(cognome);
                anagraficaView.getTxtRecapito().setText(recapito);
            }
        }
    }

    private void aggiungiPaziente() {
        String cf = anagraficaView.getTxtCf().getText().trim();
        String nome = anagraficaView.getTxtNome().getText().trim();
        String cognome = anagraficaView.getTxtCognome().getText().trim();
        String recapito = anagraficaView.getTxtRecapito().getText().trim();


        if (cf.isEmpty() || nome.isEmpty() || cognome.isEmpty()) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Attenzione: SSN, Nome e Cognome sono obbligatori!",
                    "Campi Mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < anagraficaView.getTableModel().getRowCount(); i++) {
            if (anagraficaView.getTableModel().getValueAt(i, 0).toString().equalsIgnoreCase(cf)) {
                JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Paziente con questo SSN già registrato!",
                        TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        anagraficaView.getTableModel().addRow(new Object[]{cf, nome, cognome, recapito});
        anagraficaView.getBtnPulisci().doClick();
        JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Paziente inserito con successo!");
    }

    private void modificaPaziente() {
        int rigaSelezionata = anagraficaView.getTablePazienti().getSelectedRow();
        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Seleziona prima un paziente dalla tabella!",
                    "Nessuna Selezione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = anagraficaView.getTxtNome().getText().trim();
        String cognome = anagraficaView.getTxtCognome().getText().trim();
        String recapito = anagraficaView.getTxtRecapito().getText().trim();

        if (nome.isEmpty() || cognome.isEmpty() ) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "I campi Nome, Cognome e Reparto non possono essere vuoti!",
                    TITOLO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        anagraficaView.getTableModel().setValueAt(nome, rigaSelezionata, 1);
        anagraficaView.getTableModel().setValueAt(cognome, rigaSelezionata, 2);
        anagraficaView.getTableModel().setValueAt(recapito, rigaSelezionata, 3);

        anagraficaView.getBtnPulisci().doClick();
        JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Dati del paziente aggiornati correttamente!");
    }

    private void eliminaPaziente() {
        int rigaSelezionata = anagraficaView.getTablePazienti().getSelectedRow();
        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(anagraficaView.getMainPanel(), "Seleziona un paziente da eliminare.",
                    TITOLO_ERRORE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        anagraficaView.getTableModel().removeRow(rigaSelezionata);
        anagraficaView.getBtnPulisci().doClick();
    }

    private void inizializzaFiltriDimissioni() {
        if (view.getDimissioniTable() == null || view.getDateChooserFiltro() == null) return;

        TableRowSorter<DefaultTableModel> sorterDimissioni = new TableRowSorter<>(tableModelDimissioni);
        view.getDimissioniTable().setRowSorter(sorterDimissioni);

        java.text.SimpleDateFormat formatoData = new java.text.SimpleDateFormat("dd/MM/yyyy");

        view.getBtnFiltraData().addActionListener(e -> {
            Date dataScelta = view.getDateChooserFiltro().getDate();

            if (dataScelta == null) {
                sorterDimissioni.setRowFilter(null);
            } else {

                String dataCercata = formatoData.format(dataScelta);
                sorterDimissioni.setRowFilter(RowFilter.regexFilter("^" + dataCercata + "$", 1));
            }
        });

        view.getBtnFiltraOggi().addActionListener(e -> {
            Date oggi = new Date();
            view.getDateChooserFiltro().setDate(oggi);
            // Applica il filtro
            String dataOggiStr = formatoData.format(oggi);
            sorterDimissioni.setRowFilter(RowFilter.regexFilter("^" + dataOggiStr + "$", 1));
        });

        view.getBtnResetFiltro().addActionListener(e -> {
            view.getDateChooserFiltro().setDate(null);
            sorterDimissioni.setRowFilter(null);
        });
    }

    private void pulisciCampiAnagrafica() {
        anagraficaView.getTxtCf().setText("");
        anagraficaView.getTxtCf().setEditable(true);
        anagraficaView.getTxtNome().setText("");
        anagraficaView.getTxtCognome().setText("");
        anagraficaView.getTxtRecapito().setText("");
        anagraficaView.getTablePazienti().clearSelection();
    }

    private void inizializzaAzioni() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view.getPanelAmministratore());
        //inizializzazione pulsante di logout
        if (view.getLogoutButton() != null) {
            view.getLogoutButton().addActionListener(e -> {
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
            });
        }
        //Gestione pulsante RegistraRicovero
        if(view.getRegistraRicoveriButton() == null || view.getRegistraDimissioneButton() == null) {
            System.err.println("ERRORE GUI: I bottoni sono Null. Esegui 'Build -> Rebuild Project' in IntelliJ.");
            return;
        }
        view.getRegistraRicoveriButton().addActionListener(e -> {
            RegistraRicovero dialogRicovero = new RegistraRicovero(mainFrame, listaReparti);
            dialogRicovero.getAnnullaButton().addActionListener(a -> dialogRicovero.dispose());

            dialogRicovero.getSalvaButton().addActionListener(s -> {
                String ssnInserito = dialogRicovero.getSSN().trim();
                if (ssnInserito.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogRicovero, "Attenzione: l'SSN del paziente è obbligatorio!", "Errore", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean esisteInAnagrafica = false;
                for (int i = 0; i < anagraficaView.getTableModel().getRowCount(); i++) {
                    String ssnAnagrafica = (String) anagraficaView.getTableModel().getValueAt(i, 0);
                    if (ssnAnagrafica.equalsIgnoreCase(ssnInserito)) {
                        esisteInAnagrafica = true;
                        break;
                    }
                }
            if (!esisteInAnagrafica) {
                JOptionPane.showMessageDialog(dialogRicovero,
                        "Impossibile avviare il ricovero: l'SSN inserito non è registrato nel sistema.\nRegistra prima il paziente nella scheda 'Anagrafica Pazienti'.",
                        "Paziente Sconosciuto", JOptionPane.ERROR_MESSAGE);
                return;
            }
                for (BackupRicoveroMemoria r : elencoRicoveri) {
                    if (r.ssn.equalsIgnoreCase(ssnInserito)) {
                        JOptionPane.showMessageDialog(dialogRicovero,
                                "Impossibile procedere: Il paziente con SSN " + ssnInserito + " risulta già ricoverato in struttura.",
                                "Errore Ricovero Duplicato", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                dialogRicovero.setConfermato(true);
                dialogRicovero.dispose();
            });
            // Mostriamo la finestra di registrazione (si blocca qui finché non viene chiusa)
            dialogRicovero.setVisible(true);
            // Se l'utente ha premuto "Salva" e l'SSN è valido
            if (dialogRicovero.isConfermato()) {
                Letto lettoScelto = dialogRicovero.getLettoSelezionato();
                if (lettoScelto != null) {
                    lettoScelto.setStatoAttuale(false);
                }
                // Creiamo l'oggetto temporaneo usando i getter della classe RegistraRicovero
                BackupRicoveroMemoria nuovoRicovero = new BackupRicoveroMemoria(
                        dialogRicovero.getSSN().trim(),
                        dialogRicovero.getDateChooserRicovero().getDate(),
                        dialogRicovero.getDateChooserDimissione().getDate(),
                        lettoScelto
                );
                // Salviamo il record in memoria
                elencoRicoveri.add(nuovoRicovero);
                // Aggiorniamo la grafica di entrambe le tabelle
                aggiornaTabelle();
                aggiornaMappaCorrente();
                JOptionPane.showMessageDialog(view.getPanelAmministratore(), "Ricovero registrato e inserito nei pannelli!");
            }
        });
        //Gestione Pulsante RegistraDimissione
        view.getRegistraDimissioneButton().addActionListener(e -> {
            // Interroghiamo direttamente la tabella situata nel pannello Dimissione pazienti
            JTable tabellaDimissioni = view.getDimissioniTable();
            // Capiamo quale riga ha selezionato l'amministratore con il mouse
            int rigaSelezionata = tabellaDimissioni.getSelectedRow();
            // Se non ha selezionato nessuno, blocchiamo l'operazione con un avviso
            if (rigaSelezionata == -1) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Seleziona un paziente dalla tabella nella scheda 'Dimissione pazienti' per procedere!",
                        "Nessuna selezione", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String ssnSelezionato = (String) tabellaDimissioni.getValueAt(rigaSelezionata, 0);
            // Cerchiamo l'oggetto corrispondente nella nostra lista in memoria
            BackupRicoveroMemoria ricoveroTrovato = null;
            for (BackupRicoveroMemoria r : elencoRicoveri) {
                if (r.ssn.equalsIgnoreCase(ssnSelezionato)) {
                    ricoveroTrovato = r;
                    break;
                }
            }
            if (ricoveroTrovato == null) return;
            // Apriamo il JDialog di dimissione passando la data prevista memorizzata nel ricovero
            RegistraDimissione dialogDimissione = new RegistraDimissione(parentFrame, ricoveroTrovato.dataDimissionePrevista,ssnSelezionato);
            // Azioni del JDialog di dimissione
            dialogDimissione.getAnnullaButton().addActionListener(a -> dialogDimissione.dispose());
            dialogDimissione.getSalvaButton().addActionListener(s -> {
                dialogDimissione.setConfermato(true);
                dialogDimissione.dispose();
            });

            dialogDimissione.setVisible(true);
            // Se la dimissione viene salvata con successo
            if (dialogDimissione.isConfermato()) {
                // Liberiamo il letto ripristinando il suo stato a true (disponibile)
                if (ricoveroTrovato.lettoAssegnato != null) {
                    ricoveroTrovato.lettoAssegnato.setStatoAttuale(true);
                }
                // Eliminiamo il ricovero dalla memoria
                elencoRicoveri.remove(ricoveroTrovato);
                // Rinfreschiamo i dati grafici: il paziente sparirà istantaneamente da tutte le tabelle
                aggiornaTabelle();
                aggiornaMappaCorrente();
                JOptionPane.showMessageDialog(view.getPanelAmministratore(),
                        "Paziente SSN: " + ssnSelezionato + " dimesso correttamente. Il letto associato è tornato disponibile.");
            }
        });
    }

//Serve per far funzionare il programma senza Database implementato
    private List<Reparto> generaDatiOspedale() {
        List<Reparto> reparti = new ArrayList<>();

        Reparto cardiologia = new Reparto(10, "Cardiologia");
        Stanza c1 = new Stanza(101);
        c1.aggiungiLetto(new Letto("L1", true)); // Assumo costruttore: Letto(codice, isLibero)
        c1.aggiungiLetto(new Letto("L2", true));
        c1.aggiungiLetto(new Letto("L22", true));
        cardiologia.aggiungiStanza(c1);

        Stanza c2 = new Stanza(111);
        c2.aggiungiLetto(new Letto("L1", true)); // Assumo costruttore: Letto(codice, isLibero)
        c2.aggiungiLetto(new Letto("L2", true));
        c2.aggiungiLetto(new Letto("L22", true));
        cardiologia.aggiungiStanza(c2);

        Stanza c3 = new Stanza(1011);
        c3.aggiungiLetto(new Letto("L1", true)); // Assumo costruttore: Letto(codice, isLibero)
        c3.aggiungiLetto(new Letto("L2", true));
        c3.aggiungiLetto(new Letto("L22", true));
        cardiologia.aggiungiStanza(c3);
        reparti.add(cardiologia);

        Reparto chirurgia = new Reparto(20, "Chirurgia");
        Stanza ch1 = new Stanza(201);
        ch1.aggiungiLetto(new Letto("L3", true));
        ch1.aggiungiLetto(new Letto("L4", true));
        chirurgia.aggiungiStanza(ch1);
        reparti.add(chirurgia);

        Reparto pediatria = new Reparto(30, "Pediatria");
        Stanza p1 = new Stanza(301);
        p1.aggiungiLetto(new Letto("L5", true));
        p1.aggiungiLetto(new Letto("L6", true));
        pediatria.aggiungiStanza(p1);
        reparti.add(pediatria);

        Reparto ortopedia = new Reparto(40, "Ortopedia");
        Stanza o1 = new Stanza(401);
        o1.aggiungiLetto(new Letto("L7", true));
        o1.aggiungiLetto(new Letto("L8", true));
        ortopedia.aggiungiStanza(o1);
        reparti.add(ortopedia);

        Reparto neurologia = new Reparto(50, "Neurologia");
        Stanza n1 = new Stanza(501);
        n1.aggiungiLetto(new Letto("L9", true));
        n1.aggiungiLetto(new Letto("L10", true));
        neurologia.aggiungiStanza(n1);
        reparti.add(neurologia);

        Reparto oncologia = new Reparto(60, "Oncologia");
        Stanza on1 = new Stanza(601);
        on1.aggiungiLetto(new Letto("L12", true)); // Salto L11 perché lo hai già usato in Cardiologia
        on1.aggiungiLetto(new Letto("L13", true));
        oncologia.aggiungiStanza(on1);
        reparti.add(oncologia);

        Reparto ginecologia = new Reparto(70, "Ginecologia");
        Stanza g1 = new Stanza(701);
        g1.aggiungiLetto(new Letto("L14", true));
        g1.aggiungiLetto(new Letto("L15", true));
        ginecologia.aggiungiStanza(g1);
        reparti.add(ginecologia);

        Reparto terapiaIntensiva = new Reparto(80, "Terapia Intensiva");
        Stanza t1 = new Stanza(801);
        t1.aggiungiLetto(new Letto("L16", true));
        t1.aggiungiLetto(new Letto("L17", true));
        terapiaIntensiva.aggiungiStanza(t1);
        reparti.add(terapiaIntensiva);

        Reparto psichiatria = new Reparto(90, "Psichiatria");
        Stanza ps1 = new Stanza(901);
        ps1.aggiungiLetto(new Letto("L18", true));
        ps1.aggiungiLetto(new Letto("L19", true));
        psichiatria.aggiungiStanza(ps1);
        reparti.add(psichiatria);

        return reparti;
    }
}