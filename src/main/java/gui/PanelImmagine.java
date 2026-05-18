package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PanelImmagine extends JPanel {
    private Image immagine;

    public PanelImmagine(String percorsoRisorsa) {
        // Carica l'immagine dalle risorse del progetto
        URL url = getClass().getResource(percorsoRisorsa);
        if (url != null) {
            this.immagine = new ImageIcon(url).getImage();
        } else {
            System.err.println("Errore: Immagine non trovata in " + percorsoRisorsa);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (immagine != null) {
            // Questo disegnerà l'immagine adattandola alla dimensione del pannello
            g.drawImage(immagine, 0, 0, getWidth(), getHeight(), this);
        }
    }
}