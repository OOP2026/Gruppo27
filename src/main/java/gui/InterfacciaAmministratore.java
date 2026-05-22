package gui;

import javax.swing.*;

public class InterfacciaAmministratore {
    private JPanel panelAmministratore;
    private JTabbedPane tabbedPane1;
    private JScrollBar scrollBar1;
    private JScrollBar scrollBar2;
    private JScrollBar scrollBar3;
    private JScrollBar scrollBar4;
    private JButton button1;
    private JButton registraDimissioneButton;
    private JButton registraRicoveriButton;

    public InterfacciaAmministratore() {
        //usato dal controller
    }

    public JPanel getPanelAmministratore() {
        return panelAmministratore;
    }
    public JButton getRegistraRicoveriButton() { return registraRicoveriButton;}
    public JButton getRegistraDimissioneButton() { return registraDimissioneButton;}
}
//ToDo Schermata disponibità letti
//ToDo Schermata Anagrafica Pazienti
//ToDo Schermata elenco pazienti in scadenza di dimissione con filtro data odierna o data specifica
