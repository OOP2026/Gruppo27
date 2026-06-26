package implementazioneDao;

import dao.RepartoDAO;
import database_connection.ConnessioneDatabase;
import model.Letto;
import model.Reparto;
import model.Stanza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Implementazione Postgres dell'interfaccia RepartoDAO.
 * Gestisce la ricostruzione e l'integrità strutturale dell'albero dei reparti (Reparto -> Stanze -> Letti)
 * interfacciandosi con le tabelle mediante query relazionali basate su clausole LEFT JOIN.
 */
public class RepartoPostgresDao implements RepartoDAO {
    /**
     * Ricostruisce l'albero strutturale di un singolo reparto comprensivo di tutte le stanze e letti associati.
     * <p>
     * Esegue una complessa interrogazione relazionale che concatena le tabelle
     * "reparti", "stanze" e "letti" tramite due clausole {@code LEFT JOIN}, filtrate dall'identificativo del reparto.
     * All'interno del ciclo di scansione del {@link ResultSet}:
     * <ul>
     * <li>Se l'oggetto modello Reparto è ancora nullo, lo istanzia leggendo le colonne del record iniziale.</li>
     * <li>Estrae il numero della stanza; se presente (verificato tramite {@code !rs.wasNull()}), controlla in una mappa
     * di supporto locale {@code stanzeMap} se l'oggetto Stanza è già stato creato, inserendolo nel reparto solo in caso
     * di prima occorrenza per evitare duplicati causati dal prodotto cartesiano del JOIN.</li>
     * <li>Estrae l'eventuale codice identificativo del letto e, se valorizzato, istanzia l'oggetto modello Letto
     * inserendolo direttamente all'interno della stanza di riferimento.</li>
     * </ul>
     * </p>
     *
     * @param num il numero identificativo del reparto da cercare
     * @return un Optional contenente il modello Reparto strutturato ed integrato se trovato, altrimenti un Optional vuoto
     * @throws RuntimeException incapsula una {@link SQLException} in caso di anomalie di esecuzione della query relazionale
     */
    @Override
    public Optional<Reparto> findByNum(int num) {
        String sql = "SELECT r.num AS r_num, r.nome AS r_nome, " +
                "s.numero AS s_num, " +
                "l.codice_inventario, l.libero " +
                "FROM reparti r " +
                "LEFT JOIN stanze s ON r.num = s.reparto_num " +
                "LEFT JOIN letti l ON s.numero = l.stanza_numero " +
                "WHERE r.num = ? " +
                "ORDER BY s.numero";

        Connection conn = ConnessioneDatabase.getInstance();
        Reparto reparto = null;
        java.util.Map<Integer, Stanza> stanzeMap = new java.util.HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, num);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Se è la prima riga, inizializziamo l'oggetto Reparto
                    if (reparto == null) {
                        reparto = new Reparto(rs.getInt("r_num"), rs.getString("r_nome"));
                    }

                    // Carichiamo le stanze (se esistono)
                    int numStanza = rs.getInt("s_num");
                    if (!rs.wasNull()) {
                        // FIX: Sostituito computeIfAbsent con un controllo classico per evitare l'errore della Lambda
                        Stanza stanza = stanzeMap.get(numStanza);
                        if (stanza == null) {
                            stanza = new Stanza(numStanza);
                            reparto.aggiungiStanza(stanza);
                            stanzeMap.put(numStanza, stanza);
                        }

                        // Carichiamo i letti (se esistono)
                        String codLetto = rs.getString("codice_inventario");
                        if (codLetto != null) {
                            Letto letto = new Letto(codLetto, rs.getBoolean("libero"));
                            stanza.aggiungiLetto(letto);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del reparto " + num, e);
        }

        return Optional.ofNullable(reparto);
    }
    /**
     * Estrae e mappa l'intero organigramma strutturale di tutti i reparti presenti nella clinica.
     * <p>
     * Similmente al metodo singolo, invoca una {@code SELECT} globale con due
     * {@code LEFT JOIN} ordinate tramite {@code ORDER BY r.num, s.numero}. Per gestire in sicurezza l'aggregazione
     * multilivello senza mescolare stanze aventi lo stesso numero in reparti differenti, il metodo sfrutta una mappa di
     * supporto {@code repartiMap} indicizzata per ID reparto e una mappa {@code stanzeMap} che utilizza una **chiave composta
     * stringa** formattata come "NumeroReparto-NumeroStanza". Questo previene collisioni logiche e assicura il corretto
     * inserimento dei letti nelle rispettive sottosezioni prima di restituire i valori aggregati sotto forma di {@link ArrayList}.
     * </p>
     *
     * @return una lista completa di tutti gli oggetti Reparto interamente popolati con stanze e letti
     * @throws RuntimeException incapsula una {@link SQLException} se la scansione massiva fallisce
     */
    @Override
    public List<Reparto> findAll() {
        String sql = "SELECT r.num AS r_num, r.nome AS r_nome, " +
                "s.numero AS s_num, " +
                "l.codice_inventario, l.libero " +
                "FROM reparti r " +
                "LEFT JOIN stanze s ON r.num = s.reparto_num " +
                "LEFT JOIN letti l ON s.numero = l.stanza_numero " +
                "ORDER BY r.num, s.numero";

        Connection conn = ConnessioneDatabase.getInstance();
        java.util.Map<Integer, Reparto> repartiMap = new java.util.LinkedHashMap<>();
        // Usiamo una chiave composta "IdReparto-NumeroStanza" per evitare conflitti tra stanze con lo stesso numero in reparti diversi
        java.util.Map<String, Stanza> stanzeMap = new java.util.HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int numReparto = rs.getInt("r_num");

                // Gestione Reparto senza Lambda
                Reparto reparto = repartiMap.get(numReparto);
                if (reparto == null) {
                    reparto = new Reparto(numReparto, rs.getString("r_nome"));
                    repartiMap.put(numReparto, reparto);
                }

                int numStanza = rs.getInt("s_num");
                if (!rs.wasNull()) {
                    String stanzaKey = numReparto + "-" + numStanza;

                    // Gestione Stanza senza Lambda
                    Stanza stanza = stanzeMap.get(stanzaKey);
                    if (stanza == null) {
                        stanza = new Stanza(numStanza);
                        reparto.aggiungiStanza(stanza);
                        stanzeMap.put(stanzaKey, stanza);
                    }

                    // Gestione Letto
                    String codLetto = rs.getString("codice_inventario");
                    if (codLetto != null) {
                        Letto letto = new Letto(codLetto, rs.getBoolean("libero"));
                        stanza.aggiungiLetto(letto);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei reparti", e);
        }
        return new ArrayList<>(repartiMap.values());
    }
    /**
     * Inserisce e rende persistente un nuovo reparto ospedaliero.
     * <p>
     * Invia un'istruzione atomica di inserimento {@code INSERT INTO} sulla tabella
     * "reparti", iniettando il numero intero identificativo ed il nome testuale recuperati dal modello mediante i metodi get.
     * </p>
     *
     * @param reparto l'oggetto modello Reparto da memorizzare
     * @throws RuntimeException incapsula una {@link SQLException} se l'operazione di scrittura fallisce
     */
    @Override
    public void save(Reparto reparto) {
        String sql = "INSERT INTO reparti (num, nome) VALUES (?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reparto.getNum());
            stmt.setString(2, reparto.getNome());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del reparto " + reparto.getNum(), e);
        }
    }
    /**
     * Elimina un reparto dal database in base al suo numero identificativo.
     * <p>
     * Prepara ed esegue un comando {@code DELETE FROM} applicando un filtro restrittivo
     * sulla colonna della chiave primaria "num", disattivando o eliminando la riga corrispondente dal database.
     * </p>
     *
     * @param num il numero identificativo del reparto da rimuovere
     * @throws RuntimeException incapsula una {@link SQLException} se la rimozione viene bloccata da vincoli relazionali attivi
     */
    @Override
    public void delete(int num) {
        String sql = "DELETE FROM reparti WHERE num = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, num);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del reparto " + num, e);
        }
    }
}