package dao;

import model.TurnoLavorativo;

import java.util.List;

public interface TurnoLavorativoDAO {

    List<TurnoLavorativo> findByMedico(String medicoLogin);

    void save(TurnoLavorativo turno, String medicoLogin);

    void delete(TurnoLavorativo turno, String medicoLogin);
}
