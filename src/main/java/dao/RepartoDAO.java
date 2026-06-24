package dao;

import model.Reparto;

import java.util.List;
import java.util.Optional;

public interface RepartoDAO {

    Optional<Reparto> findByNum(int num);

    List<Reparto> findAll();

    void save(Reparto reparto);

    void delete(int num);
}
