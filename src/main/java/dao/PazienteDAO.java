package dao;

import model.Paziente;

import java.util.List;
import java.util.Optional;

public interface PazienteDAO {

    Optional<Paziente> findByCf(String cf);

    List<Paziente> findAll();

    void save(Paziente paziente);

    void update(Paziente paziente);

    void delete(String cf);
}
