package implementazioneDao;

import dao.PazienteDAO;
import database_connection.ConnessioneDatabase;
import model.Paziente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PazientePostgresDao implements PazienteDAO {

    @Override
    public Optional<Paziente> findByCf(String cf) {
        String sql = "SELECT * FROM pazienti WHERE cf = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeCf(cf));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mappaPaziente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del paziente con cf " + cf, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Paziente> findAll() {
        List<Paziente> pazienti = new ArrayList<>();
        String sql = "SELECT * FROM pazienti";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pazienti.add(mappaPaziente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei pazienti", e);
        }

        return pazienti;
    }

    @Override
    public void save(Paziente paziente) {
        String sql = "INSERT INTO pazienti (cf, nome, cognome, recapito) VALUES (?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeCf(paziente.getCf()));
            stmt.setString(2, paziente.getNome());
            stmt.setString(3, paziente.getCognome());
            stmt.setString(4, paziente.getRecapito());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del paziente " + paziente.getCf(), e);
        }
    }

    @Override
    public void update(Paziente paziente) {
        String sql = "UPDATE pazienti SET nome = ?, cognome = ?, recapito = ? WHERE cf = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paziente.getNome());
            stmt.setString(2, paziente.getCognome());
            stmt.setString(3, paziente.getRecapito());
            stmt.setString(4, normalizeCf(paziente.getCf()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento del paziente " + paziente.getCf(), e);
        }
    }

    @Override
    public void delete(String cf) {
        String sql = "DELETE FROM pazienti WHERE cf = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeCf(cf));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del paziente " + cf, e);
        }
    }

    private String normalizeCf(String cf) {
        return cf == null ? null : cf.trim().toUpperCase();
    }

    private Paziente mappaPaziente(ResultSet rs) throws SQLException {
        return new Paziente(
                rs.getString("cf"),
                rs.getString("nome"),
                rs.getString("cognome"),
                rs.getString("recapito")
        );
    }
}