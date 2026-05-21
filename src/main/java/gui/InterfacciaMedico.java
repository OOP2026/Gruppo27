package gui;

import javax.swing.*;

public class InterfacciaMedico {
    private JPanel panelMedico;
    private JScrollPane Giornaliera;
    private JScrollPane Settimanale;
    private JButton WeekButton;
    private JButton DayButton;
    private JScrollBar scrollBar1;
    private JScrollBar scrollBar2;

    public InterfacciaMedico() {
        //usato dal controller
    }

    public JPanel getPanelMedico() {return panelMedico;}

}
//ToDo Aggiungere funzione aggiungi e rimuovi prestazione
//ToDo Rendere le agende a comparsa procedurale