package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PanelImmagine extends JPanel {
    private transient Image immagine;

    public PanelImmagine(String percorsoRisorsa) {

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

            g.drawImage(immagine, 0, 0, getWidth(), getHeight(), this);
        }
    }
}