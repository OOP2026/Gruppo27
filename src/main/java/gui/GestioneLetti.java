package gui;

import javax.swing.*;
import java.awt.*;
import model.Reparto;
/**
 * Pannello grafico delegato alla visualizzazione della disposizione spaziale dei letti e delle stanze.
 * Permette di selezionare un reparto e monitorare graficamente i posti liberi o occupati.
 */
public class GestioneLetti {
    private JPanel mainPanel;
    private JComboBox<Reparto> cmbReparti;
    private JPanel panelMappaLetti;
    private JScrollPane scrollMappa;
    private JTextField txtRicercaLetto;
    /**
     * Costruisce il pannello predisponendo il layout verticale per l'allineamento delle stanze.
     * <p>
     * <b>Meccanismo di funzionamento:</b> Configura il gestore di layout del pannello interno {@code panelMappaLetti}
     * assegnandogli un {@link BoxLayout} orientato lungo l'asse verticale (Y_AXIS). Questa impostazione forza i pannelli delle stanze,
     * generati successivamente dal controller in tempo reale, ad allinearsi verticalmente uno sotto l'altro in modo ordinato.
     * </p>
     */
    public GestioneLetti() {
        if (panelMappaLetti != null) {
            panelMappaLetti.setLayout(new BoxLayout(panelMappaLetti, BoxLayout.Y_AXIS));
        }
    }
    /**
     * Restituisce il pannello principale della schermata di gestione dei letti.
     *
     * @return il componente JPanel principale
     */
    public JPanel getMainPanel() { return mainPanel; }
    /**
     * Restituisce la casella combinata per la selezione del reparto ospedaliero.
     *
     * @return il componente JComboBox contenente i reparti
     */
    public JComboBox<Reparto> getCmbReparti() { return cmbReparti; }
    /**
     * Restituisce il pannello interno in cui viene disegnata la mappa visiva dei letti.
     *
     * @return il componente JPanel della mappa
     */
    public JPanel getPanelMappaLetti() { return panelMappaLetti; }
    /**
     * Restituisce il campo di testo per la ricerca o il filtraggio di uno specifico letto.
     *
     * @return il componente JTextField per la ricerca del letto
     */
    public JTextField getTxtRicercaLetto() { return txtRicercaLetto; }
}