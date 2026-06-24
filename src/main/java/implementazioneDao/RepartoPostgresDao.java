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
        String sql = "SELECT * FROM reparti WHERE num = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, num);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mappaReparto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del reparto " + num, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Reparto> findAll() {
        List<Reparto> reparti = new ArrayList<>();
        String sql = "SELECT * FROM reparti";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reparti.add(mappaReparto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei reparti", e);
        }

        return reparti;
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

    private Reparto mappaReparto(ResultSet rs) throws SQLException {
        int num = rs.getInt("num");
        Reparto reparto = new Reparto(num, rs.getString("nome"));

        for (Stanza stanza : caricaStanze(num)) {
            reparto.aggiungiStanza(stanza);
        }

        return reparto;
    }

    private List<Stanza> caricaStanze(int repartoNum) throws SQLException {
        List<Stanza> stanze = new ArrayList<>();
        String sql = "SELECT numero FROM stanze WHERE reparto_num = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, repartoNum);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int numeroStanza = rs.getInt("numero");
                    Stanza stanza = new Stanza(numeroStanza);

                    for (Letto letto : caricaLetti(numeroStanza, repartoNum)) {
                        stanza.aggiungiLetto(letto);
                    }

                    stanze.add(stanza);
                }
            }
        }

        return stanze;
    }

    private List<Letto> caricaLetti(int numeroStanza, int repartoNum) throws SQLException {
        List<Letto> letti = new ArrayList<>();
        String sql = "SELECT codice_inventario, libero FROM letti WHERE stanza_numero = ? AND reparto_num = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroStanza);
            stmt.setInt(2, repartoNum);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    letti.add(new Letto(rs.getString("codice_inventario"), rs.getBoolean("libero")));
                }
            }
        }

        return letti;
    }
}
