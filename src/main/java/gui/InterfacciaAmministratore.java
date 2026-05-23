package gui;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;

public class InterfacciaAmministratore {
    private JPanel panelAmministratore;
    private JTabbedPane tabbedPane1;
    private JButton logoutButton;
    private JButton registraDimissioneButton;
    private JButton registraRicoveriButton;
    private JTable ricoveroTable;
    private JTable dimissioniTable;
    private JTable anagraficaTable;
    private JTable lettiTable;

    private JDateChooser dateChooserFiltro;
    private JPanel datePanelFiltro;
    private JButton btnFiltraData;
    private JButton btnFiltraOggi;
    private JButton btnResetFiltro;

    public InterfacciaAmministratore() {

        dateChooserFiltro = new JDateChooser();
        dateChooserFiltro.setDateFormatString("dd/MM/yyyy");
        dateChooserFiltro.setMinSelectableDate(new java.util.Date());

        if (datePanelFiltro != null) {
            datePanelFiltro.setLayout(new BorderLayout());
            datePanelFiltro.add(dateChooserFiltro, BorderLayout.CENTER);
        }
    }


    public JPanel getPanelAmministratore() {
        return panelAmministratore;
    }

    public JButton getRegistraRicoveriButton() {
        return registraRicoveriButton;
    }

    public JButton getRegistraDimissioneButton() {
        return registraDimissioneButton;
    }

    public JTable getRicoveriTable() {
        return ricoveroTable;
    }

    public JTable getDimissioniTable() {
        return dimissioniTable;
    }

    public JTable getAnagraficaTable() {
        return anagraficaTable;
    }

    public JTable getLettiTable() {
        return lettiTable;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane1;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }
    public JDateChooser getDateChooserFiltro() { return dateChooserFiltro; }
    public JButton getBtnFiltraData() { return btnFiltraData; }
    public JButton getBtnFiltraOggi() { return btnFiltraOggi; }
    public JButton getBtnResetFiltro() { return btnResetFiltro; }
}
