package controller;

import gui.InterfacciaAmministratore;
import gui.RegistraDimissione;
import gui.RegistraRicovero;

import model.Amministratore;
import model.Reparto;
import model.Letto;
import model.Stanza;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public AdminController(InterfacciaAmministratore view, Amministratore admin) {
        this.view = view;
        this.admin = admin;
        this.elencoRicoveri = new ArrayList<>();
        this.listaReparti = generaDatiOspedale();

        inizializzaTabella();
        inizializzaAzioni();
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

    private void inizializzaAzioni() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view.getPanelAmministratore());
        //Gestione pulsante RegistraRicovero
        view.getRegistraRicoveriButton().addActionListener(e -> {
            RegistraRicovero dialogRicovero = new RegistraRicovero(parentFrame, listaReparti);
            dialogRicovero.getAnnullaButton().addActionListener(a -> dialogRicovero.dispose());
            dialogRicovero.getSalvaButton().addActionListener(s -> {
                if (dialogRicovero.getSSN().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogRicovero, "Attenzione: l'SSN del paziente è obbligatorio!", "Errore", JOptionPane.WARNING_MESSAGE);
                    return;
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
                // Creiamo l'oggetto temporaneo usando i getter della tua classe RegistraRicovero
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
        c1.aggiungiLetto(new Letto("L11", true)); // Assumo costruttore: Letto(codice, isLibero)
        c1.aggiungiLetto(new Letto("L2", true));
        cardiologia.aggiungiStanza(c1);
        reparti.add(cardiologia);

        Reparto chirurgia = new Reparto(20, "Chirurgia");
        Stanza ch1 = new Stanza(201);
        ch1.aggiungiLetto(new Letto("L3", true));
        ch1.aggiungiLetto(new Letto("L4", true));
        chirurgia.aggiungiStanza(ch1);
        reparti.add(chirurgia);

        return reparti;
    }
}