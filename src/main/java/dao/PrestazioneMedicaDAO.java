package dao;

import model.PrestazioneMedica;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PrestazioneMedicaDAO {

    List<PrestazioneMedica> findByMedico(String medicoLogin);

    Optional<String> findMedicoAssegnato(String ssnPaziente, Date dataRicovero);

    void save(PrestazioneMedica prestazione, String medicoLogin);

    void updateEsito(PrestazioneMedica prestazione, String medicoLogin);
}
