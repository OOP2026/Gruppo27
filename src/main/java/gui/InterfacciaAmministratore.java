package gui;

import javax.swing.*;

public class InterfacciaAmministratore {
    private JPanel panelAmministratore;
    private JTabbedPane tabbedPane1;
    private JScrollBar scrollBar1;
    private JScrollBar scrollBar2;
    private JScrollBar scrollBar3;
    private JScrollBar scrollBar4;

    public InterfacciaAmministratore() {
        //usato dal controller
    }

    public JPanel getPanelAmministratore() {
        return panelAmministratore;
    }
}

//ToDo Schermata disponibità letti
//ToDo Schermata Anagrafica Pazienti
//ToDo Schermata assegnazione paziente a letto
//ToDo Schermata elenco pazienti in scadenza di dimissione con filtro data odierna o data specifica
