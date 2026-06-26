package gui;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
/**
 * Finestra principale della dashboard riservata all'Amministratore del sistema.
 * Coordina i pannelli a schede relativi allo storico clinico generale, alle dimissioni
 * in corso e include i filtri temporali di ricerca.
 */
public class InterfacciaAmministratore {
    private JPanel panelAmministratore;
    private JTabbedPane tabbedPane1;
    private JButton logoutButton;
    private JButton registraDimissioneButton;
    private JButton registraRicoveriButton;
    private JTable ricoveroTable;
    private JTable dimissioniTable;
    private JTable anagraficaTable;
    private JTable lettiTable;

    private JDateChooser dateChooserFiltro;
    private JPanel datePanelFiltro;
    private JButton btnFiltraData;
    private JButton btnFiltraOggi;
    private JButton btnResetFiltro;
    private JPanel panel1;

    /**
     * Costruisce l'interfaccia dell'amministratore configurando il selettore di date per i filtri.
     */
    public InterfacciaAmministratore() {
        dateChooserFiltro = new JDateChooser();
        dateChooserFiltro.setDateFormatString("dd/MM/yyyy");
        dateChooserFiltro.setMinSelectableDate(new java.util.Date());

        if (datePanelFiltro != null) {
            datePanelFiltro.setLayout(new BorderLayout());
            datePanelFiltro.add(dateChooserFiltro, BorderLayout.CENTER);
        }
    }
    /**
     * Metodo richiamato automaticamente per l'inizializzazione dei componenti grafici personalizzati (Custom Create).
     */
    private void createUIComponents() {panel1 = new PanelImmagine("/logoFMAB.png");}
    /**
     * Restituisce il pannello principale dell'interfaccia amministratore.
     *
     * @return il componente JPanel contenitore
     */
    public JPanel getPanelAmministratore() {
        return panelAmministratore;
    }
    /**
     * Restituisce il pulsante per registrare i ricoveri.
     *
     * @return il componente JButton corrispondente
     */
    public JButton getRegistraRicoveriButton() {
        return registraRicoveriButton;
    }
    /**
     * Restituisce il pulsante per la registrazione della dimissione.
     *
     * @return il componente JButton corrispondente
     */
    public JButton getRegistraDimissioneButton() {
        return registraDimissioneButton;
    }
    /**
     * Restituisce la tabella contenente lo storico complessivo dei ricoveri.
     *
     * @return la JTable dei ricoveri storici
     */
    public JTable getRicoveriTable() {
        return ricoveroTable;
    }
    /**
     * Restituisce la tabella contenente l'elenco dei pazienti pronti per la dimissione.
     *
     * @return la JTable delle dimissioni attive
     */
    public JTable getDimissioniTable() {
        return dimissioniTable;
    }
    /**
     * Restituisce la tabella dell'anagrafica.
     *
     * @return la JTable anagrafica
     */
    public JTable getAnagraficaTable() {
        return anagraficaTable;
    }
    /**
     * Restituisce la tabella dei letti.
     *
     * @return la JTable letti
     */
    public JTable getLettiTable() {
        return lettiTable;
    }
    /**
     * Restituisce il componente a schede (Tab) della dashboard.
     *
     * @return l'oggetto JTabbedPane principale
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane1;
    }
    /**
     * Restituisce il pulsante di logout.
     *
     * @return il componente JButton logout
     */
    public JButton getLogoutButton() {
        return logoutButton;
    }
    /**
     * Restituisce l'oggetto per la selezione della data nei filtri di dimissione.
     *
     * @return il componente JDateChooser del filtro data
     */
    public JDateChooser getDateChooserFiltro() { return dateChooserFiltro; }
    /**
     * Restituisce il pulsante per applicare il filtro sulla data selezionata.
     *
     * @return il componente JButton filtra data
     */
    public JButton getBtnFiltraData() { return btnFiltraData; }
    /**
     * Restituisce il pulsante per impostare istantaneamente il filtro sulla data odierna.
     *
     * @return il componente JButton filtra oggi
     */
    public JButton getBtnFiltraOggi() { return btnFiltraOggi; }
    /**
     * Restituisce il pulsante per azzerare e rimuovere i filtri temporali attivi.
     *
     * @return il componente JButton reset filtro
     */
    public JButton getBtnResetFiltro() { return btnResetFiltro; }
}
