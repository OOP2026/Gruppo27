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
    private JLabel JLBenvenuto;
    private JPanel ContenutoGiornaliera;
    private JPanel ContenutoSettimanale;
    private JTable AgendaGiornaliera;
    private JTable AgendaSettimanale;

    public InterfacciaMedico() {
        //usato dal controller
    }

    public void setBenvenuto(String cognome){
        if (JLBenvenuto != null) {
            JLBenvenuto.setText(cognome); //Il cognome appare dinamicamente nella GUI
        }
    }

    public JPanel getPanelMedico() {return panelMedico;}
    public JTable getAgendaSettimanale(){ return AgendaSettimanale;}
    public JTable getAgendaGiornaliera(){ return AgendaGiornaliera;}
}
//ToDo Aggiungere funzione aggiungi e rimuovi prestazione
//ToDo Rendere le agende a comparsa procedurale