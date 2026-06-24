package implementazioneDao;

import dao.UtenteDAO;
import database_connection.ConnessioneDatabase;
import model.Amministratore;
import model.Medico;
import model.Utente;
import util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtentePostgresDao implements UtenteDAO {

    @Override
    public Optional<Utente> findByLogin(String login) {
        String sql = "SELECT * FROM utenti WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mappaUtente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca dell'utente con login " + login, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Utente> findAll() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utenti";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                utenti.add(mappaUtente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero degli utenti", e);
        }

        return utenti;
    }

    @Override
    public void save(Utente utente) {
        // NOTA: questo metodo presuppone che utente.getPassword() contenga la password in CHIARO
        // (es. un Utente appena creato in fase di registrazione). Viene hashata qui prima di salvarla.
        String sql = "INSERT INTO utenti (login, password, ruolo, nome, cognome, disponibile) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utente.getLogin());
            stmt.setString(2, PasswordHasher.hash(utente.getPassword()));

            if (utente instanceof Medico medico) {
                stmt.setString(3, "MEDICO");
                stmt.setString(4, medico.getNome());
                stmt.setString(5, medico.getCognome());
                stmt.setBoolean(6, medico.isDisponibile());
            } else if (utente instanceof Amministratore) {
                stmt.setString(3, "AMMINISTRATORE");
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
                stmt.setBoolean(6, true);
            } else {
                throw new IllegalArgumentException("Tipo di utente non supportato: " + utente.getClass());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'utente " + utente.getLogin(), e);
        }
    }

    @Override
    public void update(Utente utente) {
        // NOTA: questo metodo aggiorna solo i dati anagrafici, MAI la password,
        // per evitare di rihashare accidentalmente un hash già salvato.
        // Per cambiare la password usare updatePassword().
        String sql = "UPDATE utenti SET nome = ?, cognome = ?, disponibile = ? WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (utente instanceof Medico medico) {
                stmt.setString(1, medico.getNome());
                stmt.setString(2, medico.getCognome());
                stmt.setBoolean(3, medico.isDisponibile());
            } else {
                stmt.setNull(1, Types.VARCHAR);
                stmt.setNull(2, Types.VARCHAR);
                stmt.setBoolean(3, true);
            }

            stmt.setString(4, utente.getLogin());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dell'utente " + utente.getLogin(), e);
        }
    }

    @Override
    public void updatePassword(String login, String nuovaPasswordInChiaro) {
        String sql = "UPDATE utenti SET password = ? WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordHasher.hash(nuovaPasswordInChiaro));
            stmt.setString(2, login);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento della password dell'utente " + login, e);
        }
    }

    @Override
    public void delete(String login) {
        String sql = "DELETE FROM utenti WHERE login = ?";
        Connection conn = ConnessioneDatabase.getInstance();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione dell'utente " + login, e);
        }
    }

    private Utente mappaUtente(ResultSet rs) throws SQLException {
        String login = rs.getString("login");
        String password = rs.getString("password");
        String ruolo = rs.getString("ruolo");

        if ("MEDICO".equals(ruolo)) {
            Medico medico = new Medico(login, password, rs.getString("nome"), rs.getString("cognome"));
            medico.setDisponibile(rs.getBoolean("disponibile"));
            return medico;
        }

        return new Amministratore(login, password);
    }
}
