package dao;

import model.Stanza;

import java.util.List;

public interface StanzaDAO {

    List<Stanza> findByReparto(int repartoNum);

    void save(Stanza stanza, int repartoNum);

    void delete(int numero, int repartoNum);
}
