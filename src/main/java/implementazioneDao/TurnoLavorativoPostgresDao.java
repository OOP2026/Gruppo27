package implementazioneDao;

import dao.TurnoLavorativoDAO;
import database_connection.ConnessioneDatabase;
import model.TurnoLavorativo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TurnoLavorativoPostgresDao implements TurnoLavorativoDAO {

    @Override
    public List<TurnoLavorativo> findByMedico(String medicoLogin) {
        List<TurnoLavorativo> turni = new ArrayList<>();
        String sql = "SELECT giorno, inizio, fine FROM turni_lavorativi WHERE medico_login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TurnoLavorativo.GiornoSettimana giorno = TurnoLavorativo.GiornoSettimana.valueOf(rs.getString("giorno"));
                    turni.add(new TurnoLavorativo(
                            giorno,
                            rs.getTimestamp("inizio").toLocalDateTime(),
                            rs.getTimestamp("fine").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei turni del medico " + medicoLogin, e);
        }

        return turni;
    }

    @Override
    public void save(TurnoLavorativo turno, String medicoLogin) {
        String sql = "INSERT INTO turni_lavorativi (medico_login, giorno, inizio, fine) VALUES (?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);
            stmt.setString(2, turno.getGiorno().name());
            stmt.setTimestamp(3, Timestamp.valueOf(turno.getInizio()));
            stmt.setTimestamp(4, Timestamp.valueOf(turno.getFine()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del turno del medico " + medicoLogin, e);
        }
    }

    @Override
    public void delete(TurnoLavorativo turno, String medicoLogin) {
        String sql = "DELETE FROM turni_lavorativi WHERE medico_login = ? AND giorno = ? AND inizio = ? AND fine = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);
            stmt.setString(2, turno.getGiorno().name());
            stmt.setTimestamp(3, Timestamp.valueOf(turno.getInizio()));
            stmt.setTimestamp(4, Timestamp.valueOf(turno.getFine()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del turno del medico " + medicoLogin, e);
        }
    }
}
