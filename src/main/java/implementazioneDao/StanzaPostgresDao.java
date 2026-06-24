package implementazioneDao;

import dao.StanzaDAO;
import database_connection.ConnessioneDatabase;
import model.Letto;
import model.Stanza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StanzaPostgresDao implements StanzaDAO {

    @Override
    public List<Stanza> findByReparto(int repartoNum) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero delle stanze del reparto " + repartoNum, e);
        }

        return stanze;
    }

    @Override
    public void save(Stanza stanza, int repartoNum) {
        String sql = "INSERT INTO stanze (numero, reparto_num) VALUES (?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stanza.getNumero());
            stmt.setInt(2, repartoNum);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della stanza " + stanza.getNumero(), e);
        }
    }

    @Override
    public void delete(int numero, int repartoNum) {
        String sql = "DELETE FROM stanze WHERE numero = ? AND reparto_num = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            stmt.setInt(2, repartoNum);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione della stanza " + numero, e);
        }
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
