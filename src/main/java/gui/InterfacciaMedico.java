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

    public InterfacciaMedico() {
        //usato dal controller, ma aggiungo forzatamente un BoxLayout
        /*if (ContenutoGiornaliera != null) {
            ContenutoGiornaliera.setLayout(new BoxLayout(ContenutoGiornaliera, BoxLayout.Y_AXIS));
        }
        if (ContenutoSettimanale != null) {
            ContenutoSettimanale.setLayout(new BoxLayout(ContenutoSettimanale, BoxLayout.Y_AXIS));
        }*/
    }

    public JPanel getPanelMedico() {return panelMedico;}

    public void setBenvenuto(String cognome){
        if (JLBenvenuto != null) {
            JLBenvenuto.setText(cognome); //Il cognome appare dinamicamente nella GUI
        }
    }
}
//ToDo Aggiungere funzione aggiungi e rimuovi prestazione
//ToDo Rendere le agende a comparsa procedurale