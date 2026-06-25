package implementazioneDao;

import dao.PrestazioneMedicaDAO;
import database_connection.ConnessioneDatabase;
import model.PrestazioneMedica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestazioneMedicaPostgresDao implements PrestazioneMedicaDAO {

    @Override
    public List<PrestazioneMedica> findByMedico(String medicoLogin) {
        List<PrestazioneMedica> prestazioni = new ArrayList<>();
        String sql = "SELECT tipo, data_ora, esito, ssn_paziente, descrizione FROM prestazioni_mediche WHERE medico_login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PrestazioneMedica.Prestazione tipo = PrestazioneMedica.Prestazione.valueOf(rs.getString("tipo"));
                    PrestazioneMedica prestazione = new PrestazioneMedica(
                            tipo,
                            rs.getTimestamp("data_ora").toLocalDateTime(),
                            rs.getString("esito")
                    );
                    prestazione.setSsnPaziente(rs.getString("ssn_paziente"));
                    prestazione.setDescrizione(rs.getString("descrizione"));
                    prestazioni.add(prestazione);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero delle prestazioni del medico " + medicoLogin, e);
        }

        return prestazioni;
    }

    @Override
    public Optional<String> findMedicoAssegnato(String ssnPaziente) {
        String sql = "SELECT medico_login FROM prestazioni_mediche WHERE ssn_paziente = ? ORDER BY data_ora DESC LIMIT 1";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeSsn(ssnPaziente));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString("medico_login"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del medico assegnato al paziente " + ssnPaziente, e);
        }

        return Optional.empty();
    }

    @Override
    public void save(PrestazioneMedica prestazione, String medicoLogin) {
        String sql = "INSERT INTO prestazioni_mediche (medico_login, ssn_paziente, tipo, data_ora, descrizione, esito) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicoLogin);
            stmt.setString(2, normalizeSsn(prestazione.getSsnPaziente()));
            stmt.setString(3, prestazione.getTipo().name());
            stmt.setTimestamp(4, Timestamp.valueOf(prestazione.getDataOra()));
            stmt.setString(5, prestazione.getDescrizione());
            stmt.setString(6, prestazione.getEsito());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della prestazione del medico " + medicoLogin, e);
        }
    }

    @Override
    public void updateEsito(PrestazioneMedica prestazione, String medicoLogin) {
        String sql = "UPDATE prestazioni_mediche SET esito = ? WHERE medico_login = ? AND tipo = ? AND data_ora = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prestazione.getEsito());
            stmt.setString(2, medicoLogin);
            stmt.setString(3, prestazione.getTipo().name());
            stmt.setTimestamp(4, Timestamp.valueOf(prestazione.getDataOra()));

            int righeAggiornate = stmt.executeUpdate();
            if (righeAggiornate == 0) {
                throw new RuntimeException("Nessuna prestazione trovata per medico=" + medicoLogin
                        + ", tipo=" + prestazione.getTipo() + ", data_ora=" + prestazione.getDataOra()
                        + ": l'aggiornamento dell'esito non ha avuto effetto.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dell'esito della prestazione del medico " + medicoLogin, e);
        }
    }

    private String normalizeSsn(String ssn) {
        return (ssn == null || ssn.isBlank()) ? null : ssn.trim().toUpperCase();
    }
}