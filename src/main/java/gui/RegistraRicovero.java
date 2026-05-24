package gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import model.Letto;
import model.Reparto;
import model.Stanza;

public class RegistraRicovero extends JDialog{
    private JPanel mainPanel;
    private JTextField SSNTextField;
    private JTextField DiagnosiTextField;
    private JCheckBox dayHospitalCheckBox;
    private JTextArea descrizioneTextArea;
    private JButton annullaButton;
    private JPanel datePanel;
    private JButton salvaButton;
    private JPanel datePanel2;
    private JComboBox<Reparto> RepartoComboBox;
    private JComboBox<Letto> LettoComboBox;
    private JDateChooser dateChooserRicovero;
    private JDateChooser dateChooserDimissione;
    private boolean confermato = false;

    public RegistraRicovero(JFrame parent, List<Reparto> selectReparti) {
        super(parent,"Registrazione Nuovo Ricovero",true);
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    //Data ricovero
        dateChooserRicovero = new JDateChooser();
        dateChooserRicovero.setDateFormatString("dd/MM/yyyy");
        dateChooserRicovero.setDate(new Date());
        dateChooserRicovero.setMinSelectableDate(new Date());
        datePanel.setLayout(new BorderLayout());
        datePanel.add(dateChooserRicovero, BorderLayout.CENTER);
    //Data dimissione prevista
        dateChooserDimissione = new JDateChooser();
        dateChooserDimissione.setDateFormatString("dd/MM/yyyy");
        dateChooserDimissione.setDate(new Date());
        dateChooserDimissione.setMinSelectableDate(new Date());
        datePanel2.setLayout(new BorderLayout());
        datePanel2.add(dateChooserDimissione, BorderLayout.CENTER);

        if(selectReparti != null){
            for(Reparto r: selectReparti){
                RepartoComboBox.addItem(r);
            }
        }

        aggiornaLettiDisponibili((Reparto) RepartoComboBox.getSelectedItem());
        //Quando cambia il reparto nel box, vengono aggiornati i letti disponibili
        RepartoComboBox.addActionListener(e -> {
            Object item = RepartoComboBox.getSelectedItem();
            if (item instanceof Reparto) {
                Reparto repartoSelezionato = (Reparto) item;
                aggiornaLettiDisponibili(repartoSelezionato);
            }
        });

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void aggiornaLettiDisponibili(Reparto reparto){
        LettoComboBox.removeAllItems();
        if(reparto == null || reparto.getStanze() == null) return;
        for(Stanza stanza : reparto.getStanze()){
            if(stanza.getLetti() != null){
                for (Letto letto : stanza.getLetti()){
                    if (letto.isLibero()){
                        LettoComboBox.addItem(letto);
                    }
                }
            }
        }
        LettoComboBox.revalidate();
        LettoComboBox.repaint();
    }

    public String getSSN() { return SSNTextField.getText(); }
    public String getDiagnosiEntrata(){ return DiagnosiTextField.getText();}
    public boolean isDayHospital() { return dayHospitalCheckBox.isSelected(); }
    public String getDescrizione() { return descrizioneTextArea.getText(); }
    public JDateChooser getDateChooserRicovero() { return dateChooserRicovero; }
    public JDateChooser getDateChooserDimissione() {return dateChooserDimissione;}
    public JButton getAnnullaButton() { return annullaButton; }
    public JButton getSalvaButton() { return salvaButton; }
    public boolean isConfermato() { return confermato; }
    public void setConfermato(boolean confermato) { this.confermato = confermato; }
    public Reparto getRepartoSelezionato() { return (Reparto) RepartoComboBox.getSelectedItem(); }
    public Letto getLettoSelezionato() { return (Letto) LettoComboBox.getSelectedItem(); }

}