package implementazioneDao;

import dao.RicoveroDAO;
import database_connection.ConnessioneDatabase;
import model.Letto;
import model.Ricovero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RicoveroPostgresDao implements RicoveroDAO {

    @Override
    public List<Ricovero> findByPaziente(String ssn) {
        String sql = "SELECT * FROM ricoveri WHERE ssn = ?";
        return eseguiQuery(sql, ssn);
    }

    @Override
    public List<Ricovero> findInCorso() {
        String sql = "SELECT * FROM ricoveri WHERE in_corso = TRUE";
        return eseguiQuery(sql, null);
    }

    private List<Ricovero> eseguiQuery(String sql, String ssn) {
        List<Ricovero> ricoveri = new ArrayList<>();
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (ssn != null) {
                stmt.setString(1, ssn);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ricoveri.add(mappaRicovero(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei ricoveri", e);
        }

        return ricoveri;
    }

    @Override
    public void save(Ricovero ricovero) {
        String sql = "INSERT INTO ricoveri (ssn, data_ricovero, data_dimissione_prevista, letto_codice, diagnosi_entrata, diagnosi_uscita, in_corso) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ricovero.getSsn());
            stmt.setTimestamp(2, new Timestamp(ricovero.getDataRicovero().getTime()));
            stmt.setTimestamp(3, toTimestamp(ricovero.getDataDimissionePrevista()));

            Letto letto = ricovero.getLettoAssegnato();
            stmt.setString(4, letto != null ? letto.getCodiceInventario() : null);

            stmt.setString(5, ricovero.getDiagnosiEntrata());
            stmt.setString(6, ricovero.getDiagnosiUscita());
            stmt.setBoolean(7, ricovero.isInCorso());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del ricovero per il paziente " + ricovero.getSsn(), e);
        }
    }

    @Override
    public void update(Ricovero ricovero) {
        String sql = "UPDATE ricoveri SET data_dimissione_prevista = ?, data_dimissione_effettiva = ?, "
                + "letto_codice = ?, diagnosi_entrata = ?, diagnosi_uscita = ?, in_corso = ? "
                + "WHERE ssn = ? AND data_ricovero = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, toTimestamp(ricovero.getDataDimissionePrevista()));
            stmt.setTimestamp(2, toTimestamp(ricovero.getDataDimissioneEffettiva()));

            Letto letto = ricovero.getLettoAssegnato();
            stmt.setString(3, letto != null ? letto.getCodiceInventario() : null);

            stmt.setString(4, ricovero.getDiagnosiEntrata());
            stmt.setString(5, ricovero.getDiagnosiUscita());
            stmt.setBoolean(6, ricovero.isInCorso());
            stmt.setString(7, ricovero.getSsn());
            stmt.setTimestamp(8, new Timestamp(ricovero.getDataRicovero().getTime()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento del ricovero per il paziente " + ricovero.getSsn(), e);
        }
    }

    private Timestamp toTimestamp(java.util.Date data) {
        return data != null ? new Timestamp(data.getTime()) : null;
    }

    private Ricovero mappaRicovero(ResultSet rs) throws SQLException {
        Letto letto = null;
        String codiceLetto = rs.getString("letto_codice");

        if (codiceLetto != null) {
            letto = new Letto(codiceLetto, true);
        }

        Ricovero ricovero = new Ricovero(
                rs.getString("ssn"),
                rs.getTimestamp("data_ricovero"),
                rs.getTimestamp("data_dimissione_prevista"),
                letto,
                rs.getString("diagnosi_entrata")
        );

        ricovero.setDataDimissioneEffettiva(rs.getTimestamp("data_dimissione_effettiva"));
        ricovero.setDiagnosiUscita(rs.getString("diagnosi_uscita"));
        ricovero.setInCorso(rs.getBoolean("in_corso"));

        return ricovero;
    }
}
