package dao;

import model.Utente;

import java.util.List;
import java.util.Optional;

public interface UtenteDAO {

    Optional<Utente> findByLogin(String login);

    List<Utente> findAll();

    void save(Utente utente);

    void update(Utente utente);

    void updatePassword(String login, String nuovaPasswordInChiaro);

    void delete(String login);
}
