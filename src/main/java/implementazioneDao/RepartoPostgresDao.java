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

public class RepartoPostgresDao implements RepartoDAO {

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