package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

    public GestioneAnagraficaPazienti() {
        if (tablePazienti != null) {
            creaTabella();
        }
    }

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

    // Getter completi per il controller
    public JPanel getMainPanel() { return mainPanel; }
    public JTextField getTxtCf() { return txtCf; }
    public JTextField getTxtNome() { return txtNome; }
    public JTextField getTxtCognome() { return txtCognome; }
    public JTextField getTxtRecapito() { return txtRecapito; }
    public JButton getBtnAggiungi() { return btnAggiungi; }
    public JButton getBtnModifica() { return btnModifica; }
    public JButton getBtnElimina() { return btnElimina; }
    public JButton getBtnPulisci() { return btnPulisci; }
    public JTable getTablePazienti() { return tablePazienti; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtRicerca() { return txtRicerca; }
    public JButton getBtnCerca() { return btnCerca; }
}