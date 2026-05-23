package gui;

import javax.swing.*;
import java.awt.*;
import model.Reparto;

public class GestioneLetti {
    private JPanel mainPanel;
    private JComboBox<Reparto> cmbReparti;
    private JPanel panelMappaLetti;
    private JScrollPane scrollMappa;

    public GestioneLetti() {
        if (panelMappaLetti != null) {
            panelMappaLetti.setLayout(new BoxLayout(panelMappaLetti, BoxLayout.Y_AXIS));
        }
    }

    public JPanel getMainPanel() { return mainPanel; }
    public JComboBox<Reparto> getCmbReparti() { return cmbReparti; }
    public JPanel getPanelMappaLetti() { return panelMappaLetti; }
}