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
    private JButton logout;
    private JLabel LabelGiornaliera;
    private JLabel LabelSettimanale;
    private JButton AvantiButton;
    private JButton IndietroButton;

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
    public JScrollPane getGiornaliera() {return Giornaliera;}
    public JScrollPane getSettimanale() {return Settimanale;}
    public JButton getWeekButton() {return WeekButton;}
    public JButton getDayButton() {return DayButton;}
    public JPanel getContenutoGiornaliero() {return ContenutoGiornaliera;}
    public JPanel getContenutoSettimanale(){ return ContenutoSettimanale;}
    public JLabel getLabelGiornaliera(){return LabelGiornaliera;}
    public JLabel getLabelSettimanale(){return LabelSettimanale;}
    public JButton getAvantiButton(){return AvantiButton;}
    public JButton getIndietroButton(){return IndietroButton;}
    public JButton getLogoutButton(){return logout;}
}