package dao;

import model.Ricovero;

import java.util.List;

public interface RicoveroDAO {

    List<Ricovero> findByPaziente(String ssn);

    List<Ricovero> findInCorso();

    void save(Ricovero ricovero);

    void update(Ricovero ricovero);
}
