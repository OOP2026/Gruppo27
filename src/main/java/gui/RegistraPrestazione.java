package gui;

import model.PrestazioneMedica;

import javax.swing.*;

public class RegistraPrestazione extends JDialog {
    private JTextArea descrizionePrestazione;
    private JButton salvaButton;
    private JButton annullaButton;
    private JPanel dataOraPanel;
    private JComboBox<String> tipoPrestazioneBox;
    private JTextArea EsitoPrestazione;
    private JPanel mainPrestazione;

    public RegistraPrestazione(JFrame parent) {
        super(parent,"Registra Prestazione",true);
        setContentPane(mainPrestazione);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public JButton getSalvaButton() {return  salvaButton;}
    public JButton getAnnullaButton() {return annullaButton;}

}
