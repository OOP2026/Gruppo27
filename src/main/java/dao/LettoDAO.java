package dao;

import model.Letto;

import java.util.List;
import java.util.Optional;

public interface LettoDAO {

    Optional<Letto> findByCodice(String codiceInventario);

    List<Letto> findByStanza(int numeroStanza, int repartoNum);

    void save(Letto letto, int numeroStanza, int repartoNum);

    void updateStato(String codiceInventario, boolean libero);

    void delete(String codiceInventario);
}
