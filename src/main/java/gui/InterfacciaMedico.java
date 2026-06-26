package gui;

import javax.swing.*;
/**
 * Finestra principale della dashboard riservata al personale Medico.
 * Organizza e mostra l'agenda delle visite del medico suddivisa in griglia giornaliera e settimanale.
 */
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
    /**
     * Costruisce l'interfaccia grafica del medico. I comportamenti sono gestiti dal rispettivo controller.
     */
    public InterfacciaMedico() {
        //usato dal controller
    }
    /**
     * Aggiorna dinamicamente l'etichetta testuale di benvenuto mostrando il cognome del medico connesso.
     * <p>
     * Esegue una verifica preventiva sulla nullità del componente grafico {@code JLBenvenuto}
     * e ne modifica la proprietà testuale richiamando il metodo {@code setText(cognome)}, rendendo visibile l'informazione personalizzata sulla GUI.
     * </p>
     *
     * @param cognome il cognome del medico autenticato
     */
    public void setBenvenuto(String cognome){
        if (JLBenvenuto != null) {
            JLBenvenuto.setText(cognome); //Il cognome appare dinamicamente nella GUI
        }
    }

    /**
     * Restituisce il pannello principale della dashboard medica.
     *
     * @return il componente JPanel principale
     */
    public JPanel getPanelMedico() { return panelMedico; }

    /**
     * Restituisce la tabella grafica che rappresenta l'agenda settimanale del medico.
     *
     * @return la JTable della pianificazione settimanale
     */
    public JTable getAgendaSettimanale() { return AgendaSettimanale; }

    /**
     * Restituisce la tabella grafica che rappresenta l'agenda giornaliera del medico.
     *
     * @return la JTable della pianificazione del giorno corrente
     */
    public JTable getAgendaGiornaliera() { return AgendaGiornaliera; }

    /**
     * Restituisce il pannello a scorrimento della vista giornaliera.
     *
     * @return il componente JScrollPane giornaliero
     */
    public JScrollPane getGiornaliera() { return Giornaliera; }

    /**
     * Restituisce il pannello a scorrimento della vista settimanale.
     *
     * @return il componente JScrollPane settimanale
     */
    public JScrollPane getSettimanale() { return Settimanale; }

    /**
     * Restituisce il pulsante per visualizzare o pianificare la vista settimanale.
     *
     * @return il componente JButton della vista settimana
     */
    public JButton getWeekButton() { return WeekButton; }

    /**
     * Restituisce il pulsante per visualizzare o pianificare la vista giornaliera.
     *
     * @return il componente JButton della vista giorno
     */
    public JButton getDayButton() { return DayButton; }

    /**
     * Restituisce il pannello del contenuto del giorno.
     *
     * @return il componente JPanel interno
     */
    public JPanel getContenutoGiornaliero() { return ContenutoGiornaliera; }

    /**
     * Restituisce il pannello del contenuto della settimana.
     *
     * @return il componente JPanel interno
     */
    public JPanel getContenutoSettimanale() { return ContenutoSettimanale; }

    /**
     * Restituisce l'etichetta testuale che mostra la data del giorno nell'agenda giornaliera.
     *
     * @return il componente JLabel della data del giorno
     */
    public JLabel getLabelGiornaliera() { return LabelGiornaliera; }

    /**
     * Restituisce l'etichetta testuale che mostra l'intervallo di date nell'agenda settimanale.
     *
     * @return il componente JLabel dell'intervallo settimanale
     */
    public JLabel getLabelSettimanale() { return LabelSettimanale; }

    /**
     * Restituisce il pulsante di navigazione per scorrere l'agenda in avanti (giorno/settimana successiva).
     *
     * @return il componente JButton avanti
     */
    public JButton getAvantiButton() { return AvantiButton; }

    /**
     * Restituisce il pulsante di navigazione per scorrere l'agenda all'indietro (giorno/settimana precedente).
     *
     * @return il componente JButton indietro
     */
    public JButton getIndietroButton() { return IndietroButton; }

    /**
     * Restituisce il pulsante di logout.
     *
     * @return il componente JButton logout
     */
    public JButton getLogoutButton() { return logout; }
}