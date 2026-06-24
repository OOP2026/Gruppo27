package implementazioneDao;

import dao.LettoDAO;
import database_connection.ConnessioneDatabase;
import model.Letto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LettoPostgresDao implements LettoDAO {

    @Override
    public Optional<Letto> findByCodice(String codiceInventario) {
        String sql = "SELECT codice_inventario, libero FROM letti WHERE codice_inventario = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceInventario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Letto(rs.getString("codice_inventario"), rs.getBoolean("libero")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del letto " + codiceInventario, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Letto> findByStanza(int numeroStanza, int repartoNum) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei letti della stanza " + numeroStanza, e);
        }

        return letti;
    }

    @Override
    public void save(Letto letto, int numeroStanza, int repartoNum) {
        String sql = "INSERT INTO letti (codice_inventario, libero, stanza_numero, reparto_num) VALUES (?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, letto.getCodiceInventario());
            stmt.setBoolean(2, letto.isLibero());
            stmt.setInt(3, numeroStanza);
            stmt.setInt(4, repartoNum);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del letto " + letto.getCodiceInventario(), e);
        }
    }

    @Override
    public void updateStato(String codiceInventario, boolean libero) {
        String sql = "UPDATE letti SET libero = ? WHERE codice_inventario = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, libero);
            stmt.setString(2, codiceInventario);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dello stato del letto " + codiceInventario, e);
        }
    }

    @Override
    public void delete(String codiceInventario) {
        String sql = "DELETE FROM letti WHERE codice_inventario = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceInventario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del letto " + codiceInventario, e);
        }
    }
}
