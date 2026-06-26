package implementazioneDao;

import dao.PrestazioneMedicaDAO;
import database_connection.ConnessioneDatabase;
import model.PrestazioneMedica;
import model.Ricovero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Optional<String> findMedicoAssegnato(String ssn, java.util.Date dataRicovero) {
        // La query ora cerca solo le prestazioni avvenute DOPO l'inizio del ricovero
        String sql = "SELECT u.nome, u.cognome " +
                "FROM prestazioni_mediche p " +
                "JOIN utenti u ON p.medico_login = u.login " +
                "WHERE p.ssn_paziente = ? AND p.data_ora >= ? " +
                "ORDER BY p.data_ora DESC " +
                "LIMIT 1";

        Connection conn = database_connection.ConnessioneDatabase.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizeSsn(ssn));

            // Convertiamo la java.util.Date del ricovero in java.sql.Timestamp per il database
            stmt.setTimestamp(2, new java.sql.Timestamp(dataRicovero.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nomeCompleto = rs.getString("nome") + " " + rs.getString("cognome");
                    return Optional.of(nomeCompleto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del medico assegnato al paziente " + ssn, e);
        }

        return Optional.empty();
    }

    @Override
    public Map<String, String> findMedicoAssegnatoBatch(List<Ricovero> ricoveri) {
        Map<String, String> risultato = new HashMap<>();
        if (ricoveri == null || ricoveri.isEmpty()) {
            return risultato;
        }

        // Raccogliamo gli ssn distinti coinvolti, normalizzati, per costruire la clausola IN
        List<String> ssnDistinti = new ArrayList<>();
        for (Ricovero r : ricoveri) {
            String ssn = normalizeSsn(r.getSsn());
            if (ssn != null && !ssnDistinti.contains(ssn)) {
                ssnDistinti.add(ssn);
            }
        }
        if (ssnDistinti.isEmpty()) {
            return risultato;
        }

        // Una singola query recupera TUTTE le prestazioni (con medico associato) per tutti
        // gli ssn coinvolti, evitando di interrogare il database una volta per ogni ricovero.
        // Il filtro "solo prestazioni dopo l'inizio del ricovero" e la scelta della più recente
        // vengono fatti qui in Java sul risultato unico, invece che in N query separate.
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ssnDistinti.size(); i++) {
            placeholders.append(i == 0 ? "?" : ", ?");
        }

        String sql = "SELECT p.ssn_paziente, p.data_ora, u.nome, u.cognome " +
                "FROM prestazioni_mediche p " +
                "JOIN utenti u ON p.medico_login = u.login " +
                "WHERE p.ssn_paziente IN (" + placeholders + ") " +
                "ORDER BY p.ssn_paziente, p.data_ora DESC";

        Connection conn = ConnessioneDatabase.getInstance();
        // Per ogni ssn conserviamo solo la prestazione più recente già incontrata (grazie
        // all'ORDER BY la prima riga vista per ogni ssn è già la più recente in assoluto;
        // qui applichiamo anche il vincolo "successiva alla data di inizio ricovero").
        Map<String, java.sql.Timestamp> dataRicoveroPerSsn = new HashMap<>();
        for (Ricovero r : ricoveri) {
            String ssn = normalizeSsn(r.getSsn());
            if (ssn != null && r.getDataRicovero() != null) {
                dataRicoveroPerSsn.put(ssn, new java.sql.Timestamp(r.getDataRicovero().getTime()));
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < ssnDistinti.size(); i++) {
                stmt.setString(i + 1, ssnDistinti.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ssn = rs.getString("ssn_paziente");

                    // Se per questo ssn abbiamo già trovato un medico (la riga più recente
                    // grazie all'ORDER BY), le righe successive per lo stesso ssn vanno ignorate
                    if (risultato.containsKey(ssn)) {
                        continue;
                    }

                    java.sql.Timestamp dataRicovero = dataRicoveroPerSsn.get(ssn);
                    java.sql.Timestamp dataPrestazione = rs.getTimestamp("data_ora");

                    if (dataRicovero != null && dataPrestazione.before(dataRicovero)) {
                        // Questa prestazione è precedente all'inizio del ricovero corrente:
                        // non è valida come "medico assegnato" per questo ricovero.
                        continue;
                    }

                    risultato.put(ssn, rs.getString("nome") + " " + rs.getString("cognome"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero batch dei medici assegnati", e);
        }

        return risultato;
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