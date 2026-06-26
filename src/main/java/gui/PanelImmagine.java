package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
/**
 * Componente grafico personalizzato estensione di {@link JPanel} addetto alla renderizzazione
 * di immagini di sfondo o loghi aziendali adattabili alle dimensioni del contenitore.
 */
public class PanelImmagine extends JPanel {
    private transient Image immagine;
    /**
     * Carica l'immagine dal percorso specificato all'interno delle risorse di progetto.
     * <p>
     * Utilizza la riflessione della classe tramite {@code getClass().getResource(percorsoRisorsa)}
     * per recuperare l'URL assoluto della risorsa nel file JAR. Se l'URL è valido, istanzia un oggetto {@link ImageIcon}
     * estraendone la struttura primitiva {@link Image} tramite il metodo {@code getImage()}; in caso contrario, reindirizza
     * un avviso sul canale di errore dello standard output.
     * </p>
     *
     * @param percorsoRisorsa il path relativo dell'immagine
     */
    public PanelImmagine(String percorsoRisorsa) {
        URL url = getClass().getResource(percorsoRisorsa);
        if (url != null) {
            this.immagine = new ImageIcon(url).getImage();
        } else {
            System.err.println("Errore: Immagine non trovata in " + percorsoRisorsa);
        }
    }
    /**
     * Sovrascrive il disegno del componente per proiettare l'immagine scalandone larghezza
     * e altezza in base ai confini del pannello.
     * <p>
     * Invoca inizialmente il metodo della superclasse {@code super.paintComponent(g)}
     * per preservare il corretto disegno dei bordi e degli sfondi di sistema. Successivamente, se l'oggetto immagine è valido,
     * richiama {@code g.drawImage(immagine, 0, 0, getWidth(), getHeight(), this)} ereditando dinamicamente la larghezza
     * ({@code getWidth()}) e l'altezza ({@code getHeight()}) attuali del pannello Swing per stirare o comprimere l'immagine senza sgranare.
     * </p>
     *
     * @param g l'oggetto grafico di disegno del framework Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (immagine != null) {
            g.drawImage(immagine, 0, 0, getWidth(), getHeight(), this);
        }
    }
}