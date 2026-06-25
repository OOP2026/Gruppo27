package controller;

import dao.PrestazioneMedicaDAO;
import dao.TurnoLavorativoDAO;
import gui.InterfacciaMedico;
import gui.RegistraPrestazione;
import implementazioneDao.PrestazioneMedicaPostgresDao;
import implementazioneDao.TurnoLavorativoPostgresDao;
import model.Medico;
import model.TurnoLavorativo;
import model.PrestazioneMedica;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class MedicoController {
    private InterfacciaMedico view;
    private Medico model;
    private final JFrame mainFrame;
    private LocalDate datagiornaliera;

    private final TurnoLavorativoDAO turnoLavorativoDAO;
    private final PrestazioneMedicaDAO prestazioneMedicaDAO;

    private DefaultTableModel modelGiornaliero;
    private DefaultTableModel modelSettimanale;
    private List<PrestazioneMedica> prestazioniRegistrate;

    private final DateTimeFormatter oraFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dataOraFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MedicoController(InterfacciaMedico view, Medico model, JFrame mainFrame) {
        this.view = view;
        this.model = model;
        this.mainFrame = mainFrame;
        this.datagiornaliera = LocalDate.now();

        this.turnoLavorativoDAO = new TurnoLavorativoPostgresDao();
        this.prestazioneMedicaDAO = new PrestazioneMedicaPostgresDao();

        this.view.setBenvenuto("Benvenuto Dott. " + model.getCognome());

        caricaTurniDelMedico();
        this.prestazioniRegistrate = new ArrayList<>(prestazioneMedicaDAO.findByMedico(model.getLogin()));

        inizializzaTabelle();
        caricaAgendaGiornaliera();
        caricaAgendaSettimanale();
        inizializzaAzioni();
    }

    private void inizializzaTabelle() {
        //Configurazione Tabella Giornaliera
        String[] colonneGiornaliera = {"Orario", "Prestazione"};
        modelGiornaliero = new DefaultTableModel(colonneGiornaliera, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        view.getAgendaGiornaliera().setModel(modelGiornaliero);
        view.getAgendaGiornaliera().setRowHeight(35);

        //Configurazione Tabella Settimanale
        String[] colonneSettimanale = {"Orario", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        modelSettimanale = new DefaultTableModel(colonneSettimanale, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        view.getAgendaSettimanale().setModel(modelSettimanale);
        view.getAgendaSettimanale().setRowHeight(35);

        DefaultTableCellRenderer agendeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cella = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // La prima colonna (Orari) mantiene lo sfondo standard del sistema
                if (column == 0) {
                    cella.setBackground(table.getTableHeader().getBackground());
                    cella.setForeground(Color.BLACK);
                    return cella;
                }
                // Calcoliamo il LocalDateTime associato a questa specifica cella (riga/colonna)
                int ora = 8 + row;
                LocalDateTime slotOrario;

                if (table == view.getAgendaGiornaliera()) {
                    // Tabella Giornaliera
                    slotOrario = LocalDateTime.of(datagiornaliera, LocalTime.of(ora, 0));
                } else {
                    // Tabella Settimanale
                    LocalDate lunedi = datagiornaliera.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDate giornoCorrente = lunedi.plusDays((long)column - 1);
                    slotOrario = LocalDateTime.of(giornoCorrente, LocalTime.of(ora, 0));
                }
                //Se il medico è in turno, illumina la cella di VERDE
                if (model.isPrestazioneValid(slotOrario)) {
                    cella.setBackground(new Color(212, 239, 223)); // Verde chiaro
                    cella.setForeground(new Color(27, 94, 32));   // Testo verde scuro
                } else {
                    // FONDAMENTALE: Se non è in turno, resetta lo sfondo a Bianco
                    cella.setBackground(Color.WHITE);
                    cella.setForeground(Color.BLACK);
                }
                // Gestione del colore di selezione standard se il medico clicca sulla cella
                if (isSelected) {
                    cella.setBackground(table.getSelectionBackground());
                    cella.setForeground(table.getSelectionForeground());
                }

                return cella;
            }
        };
        // Applichiamo il renderer a tutte le colonne di entrambe le tabelle
        for (int i = 0; i < view.getAgendaGiornaliera().getColumnCount(); i++) {
            view.getAgendaGiornaliera().getColumnModel().getColumn(i).setCellRenderer(agendeRenderer);
        }
        for (int i = 0; i < view.getAgendaSettimanale().getColumnCount(); i++) {
            view.getAgendaSettimanale().getColumnModel().getColumn(i).setCellRenderer(agendeRenderer);
        }
    }

    private void caricaAgendaGiornaliera() {
        // 1. Aggiorna la data in alto (es: "Agenda del giorno: Lunedì 25 Maggio 2026")
        if (view.getLabelGiornaliera() != null) {
            DateTimeFormatter formatoDataTitolo = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", java.util.Locale.ITALIAN);
            String dataFormattata = datagiornaliera.format(formatoDataTitolo);
            // Mette la prima lettera in maiuscolo (es: lunedì -> Lunedì)
            String titolo = dataFormattata.substring(0, 1).toUpperCase() + dataFormattata.substring(1);
            ((JLabel) view.getLabelGiornaliera()).setText("Agenda del giorno: " + titolo);
        }

        // 2. Svuota e ricarica i dati della tabella
        modelGiornaliero.setRowCount(0);
        for (int ora = 8; ora <= 20; ora++) {
            String orarioStr = String.format("%02d:00", ora);
            String contenutoCella = "";

            for (PrestazioneMedica p : prestazioniRegistrate) {
                if (p.getDataOra().toLocalDate().equals(datagiornaliera) && p.getDataOra().getHour() == ora) {
                    if (!contenutoCella.isEmpty()) contenutoCella += " + ";
                    contenutoCella += p.getTipo().toString();
                }
            }
            modelGiornaliero.addRow(new Object[]{orarioStr, contenutoCella});
        }
    }

    private void caricaAgendaSettimanale() {
        LocalDate lunedi = datagiornaliera.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domenica = lunedi.plusDays(6);
        //Aggiorna l'intervallo di date in alto
        if (view.getLabelSettimanale() != null) {
            DateTimeFormatter formatoSettimana = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            view.getLabelSettimanale().setText("Settimana dal " + lunedi.format(formatoSettimana) + " al " + domenica.format(formatoSettimana));
        }
        //Svuota e ricarica i dati della tabella settimanale
        modelSettimanale.setRowCount(0);
        for (int ora = 8; ora <= 20; ora++) {
            Object[] riga = new Object[8];
            riga[0] = String.format("%02d:00", ora);

            for (int i = 0; i < 7; i++) {
                LocalDate giornoCorrente = lunedi.plusDays(i);
                String contenutoCella = "";

                for (PrestazioneMedica p : prestazioniRegistrate) {
                    if (p.getDataOra().toLocalDate().equals(giornoCorrente) && p.getDataOra().getHour() == ora) {
                        if (!contenutoCella.isEmpty()) contenutoCella += " + ";
                        contenutoCella += p.getTipo().toString();
                    }
                }
                riga[i + 1] = contenutoCella;
            }
            modelSettimanale.addRow(riga);
        }
    }

    private void inizializzaAzioni() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view.getPanelMedico());

        //GESTIONE DEL BOTTONE LOGOUT
        if (view.getLogoutButton() != null) {
            view.getLogoutButton().addActionListener(e -> gestisciLogout());
        }

        //DEFINIZIONE DELL'AZIONE PER AGGIUNGERE LE PRESTAZIONI CON LA NUOVA INTERFACCIA DIALOG
        java.awt.event.ActionListener aggiungerePrestazioneAzione = e -> {
            // Creiamo un'istanza della tua nuova finestra di dialogo passandogli il frame principale
            gui.RegistraPrestazione dialog = new gui.RegistraPrestazione(parentFrame);

            // Azione per il tasto ANNULLA della JDialog
            dialog.getAnnullaButton().addActionListener(evt -> dialog.dispose());

            // Azione per il tasto SALVA della JDialog
            dialog.getSalvaButton().addActionListener(evt -> {
                try {
                    // Recuperiamo il LocalDateTime combinato dal JDateChooser + JComboBox dell'ora
                    LocalDateTime orarioScelto = dialog.getLocalDateTimeInput();

                    if (orarioScelto == null) {
                        JOptionPane.showMessageDialog(dialog, "Seleziona una data valida!", "Errore Data", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    // CONTROLLO STRUTTURATO: Il medico è in turno in quell'orario intermediario?
                    if (!model.isPrestazioneValid(orarioScelto)) {
                        JOptionPane.showMessageDialog(dialog,
                                "Impossibile registrare la prestazione!\nIn questa data/ora il medico non è in turno lavorativo.",
                                "Pianificazione Rifiutata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Recuperiamo il tipo (VISITA o INTERVENTO_CHIRURGICO)
                    String tipoScelto = dialog.getTipoSelezionato();
                    // Recuperiamo l'esito inserito nella JTextArea
                    String esito = dialog.getEsitoInput();
                    if (esito.isEmpty()) {
                        esito = "Ancora non erogato";
                    }
                    // Mappiamo la stringa nell'Enum corretto
                    PrestazioneMedica.Prestazione tipoEnum = tipoScelto.equals("VISITA") ?
                            PrestazioneMedica.Prestazione.VISITA : PrestazioneMedica.Prestazione.INTERVENTO_CHIRURGICO;
                    // Creazione dell'oggetto e inserimento nel Model, nel DB e nella lista locale
                    PrestazioneMedica nuovaPrestazione = new PrestazioneMedica(tipoEnum, orarioScelto, esito);
                    nuovaPrestazione.setDescrizione(dialog.getDescrizioneInput());

                    String ssnPaziente = dialog.getSsnPaziente();
                    if (ssnPaziente != null && !ssnPaziente.isBlank()) {
                        nuovaPrestazione.setSsnPaziente(ssnPaziente.toUpperCase());
                    }

                    model.registraPrestazione(nuovaPrestazione);
                    prestazioneMedicaDAO.save(nuovaPrestazione, model.getLogin());
                    prestazioniRegistrate.add(nuovaPrestazione);
                    // Ricarichiamo ed aggiorniamo al volo le tabelle della schermata principale
                    caricaAgendaGiornaliera();
                    caricaAgendaSettimanale();
                    // Chiudiamo il dialog e confermiamo il successo
                    dialog.dispose();
                    JOptionPane.showMessageDialog(parentFrame, "Prestazione pianificata correttamente alle " + orarioScelto.format(oraFormatter));
                } catch (Exception ex) {
                    String dettaglio = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(dialog, "Errore durante il salvataggio dei dati: " + dettaglio, "Errore", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            // Mostriamo la finestra (essendo modale bloccherà il frame principale finché non viene chiusa o salvata)
            dialog.setVisible(true);
        };
        // COLLEGAMENTO DEI PULSANTI DI INSERIMENTO
        if (view.getDayButton() != null) view.getDayButton().addActionListener(aggiungerePrestazioneAzione);
        if (view.getWeekButton() != null) view.getWeekButton().addActionListener(aggiungerePrestazioneAzione);

        // COLLEGAMENTO DEI PULSANTI DI NAVIGAZIONE AVANTI/INDIETRO
        if (view.getIndietroButton() != null) {
            view.getIndietroButton().addActionListener(evt -> {
                this.datagiornaliera = this.datagiornaliera.minusWeeks(1);
                caricaAgendaGiornaliera();
                caricaAgendaSettimanale();
            });
        }

        if (view.getAvantiButton() != null) {
            view.getAvantiButton().addActionListener(evt -> {
                this.datagiornaliera = this.datagiornaliera.plusWeeks(1);
                caricaAgendaGiornaliera();
                caricaAgendaSettimanale();
            });
        }
        // CLICK SULLA TABELLA GIORNALIERA
        view.getAgendaGiornaliera().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int riga = view.getAgendaGiornaliera().getSelectedRow();
                if (riga == -1) return;

                int oraSlot = 8 + riga;

                List<PrestazioneMedica> filtrate = new ArrayList<>();
                for (PrestazioneMedica p : prestazioniRegistrate) {
                    if (p.getDataOra().toLocalDate().equals(datagiornaliera) && p.getDataOra().getHour() == oraSlot) {
                        filtrate.add(p);
                    }
                }
                gestisciSelezioneEMostraDettagli(parentFrame, filtrate);
            }
        });
        // CLICK SULLA TABELLA SETTIMANALE
        view.getAgendaSettimanale().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int riga = view.getAgendaSettimanale().getSelectedRow();
                int colonna = view.getAgendaSettimanale().getSelectedColumn();
                if (riga == -1 || colonna == 0) return;

                LocalDate lunedi = datagiornaliera.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate giornoSelezionato = lunedi.plusDays((long)colonna - 1);
                int oraSlot = 8 + riga;

                List<PrestazioneMedica> filtrate = new ArrayList<>();
                for (PrestazioneMedica p : prestazioniRegistrate) {
                    if (p.getDataOra().toLocalDate().equals(giornoSelezionato) && p.getDataOra().getHour() == oraSlot) {
                        filtrate.add(p);
                    }
                }
                gestisciSelezioneEMostraDettagli(parentFrame, filtrate);
            }
        });
    }

    private void gestisciSelezioneEMostraDettagli(JFrame parentFrame, List<PrestazioneMedica> filtrate) {
        if (filtrate.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Nessuna prestazione registrata in questo slot orario.", "Slot Vuoto", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Se ce n'è solo una, la mostriamo direttamente
        if (filtrate.size() == 1) {
            mostraSchedaDettagli(parentFrame, filtrate.get(0));
            return;
        }
        // Se ce ne sono più di una (es. 10:15 e 10:45), mostriamo un menù a tendina per scegliere
        String[] opzioniIntermedie = new String[filtrate.size()];
        for (int i = 0; i < filtrate.size(); i++) {
            opzioniIntermedie[i] = "[" + filtrate.get(i).getDataOra().format(oraFormatter) + "] " + filtrate.get(i).getTipo();
        }

        String scelta = (String) JOptionPane.showInputDialog(parentFrame,
                "Sono presenti più prestazioni in questo slot.\nSeleziona quella da analizzare:",
                "Prestazioni dello Slot",
                JOptionPane.PLAIN_MESSAGE, null, opzioniIntermedie, opzioniIntermedie[0]);

        if (scelta != null) {
            for (int i = 0; i < opzioniIntermedie.length; i++) {
                if (opzioniIntermedie[i].equals(scelta)) {
                    mostraSchedaDettagli(parentFrame, filtrate.get(i));
                    break;
                }
            }
        }
    }

    private void mostraSchedaDettagli(JFrame frame, PrestazioneMedica p) {
        String ssnInfo = (p.getSsnPaziente() == null || p.getSsnPaziente().isBlank())
                ? "Non specificato" : p.getSsnPaziente();
        String descrizioneInfo = (p.getDescrizione() == null || p.getDescrizione().isBlank())
                ? "-" : p.getDescrizione();

        String scheda = "📋 --- SCHEDA PRESTAZIONE MEDICA ---\n\n" +
                "SSN Paziente: " + ssnInfo + "\n" +
                "Tipologia Atto: " + p.getTipo() + "\n" +
                "Data e Ora Esecuzione: " + p.getDataOra().format(dataOraFormatter) + "\n" +
                "Descrizione: " + descrizioneInfo + "\n" +
                "Esito Diagnostico / Note:\n" + p.getEsito();
        String[] opzioni = {"Chiudi", "Modifica Esito"};
        //Mostriamo il pannello interattivo
        int scelta = JOptionPane.showOptionDialog(
                frame,
                scheda,
                "Dettagli Clinici Atto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opzioni,
                opzioni[0]
        );
        //Se il medico clicca su "Modifica Esito"
        if (scelta == 1) {
            String nuovoEsito = JOptionPane.showInputDialog(
                    frame,
                    "Inserisci il nuovo esito diagnostico / referto:",
                    "Modifica Esito",
                    JOptionPane.QUESTION_MESSAGE
            );
            // Se l'utente non ha premuto annulla e non ha lasciato il campo vuoto
            if (nuovoEsito != null && !nuovoEsito.trim().isEmpty()) {
                // Aggiorna l'esito direttamente sull'oggetto PrestazioneMedica e nel DB
                p.setEsito(nuovoEsito.trim());
                prestazioneMedicaDAO.updateEsito(p, model.getLogin());
                // Ricarica i dati grafici per mostrare eventuali variazioni istantanee nelle tabelle
                caricaAgendaGiornaliera();
                caricaAgendaSettimanale();

                JOptionPane.showMessageDialog(frame, "Esito aggiornato con successo!", "Operazione Completata", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void caricaTurniDelMedico() {
        for (TurnoLavorativo turno : turnoLavorativoDAO.findByMedico(model.getLogin())) {
            model.aggiungiTurno(turno);
        }
    }
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
}