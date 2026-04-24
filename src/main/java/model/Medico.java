package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Medico extends Utente {
    private String nome;
    private String cognome;
    private List<LocalDateTime> turni = new ArrayList<>();

    public Medico(String login, String password, String nome, String cognome){
        super(login, password);
        this.nome = nome;
        this.cognome = cognome;
    }
    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public String getCognome(){
        return cognome;
    }
    public void setCognome(String cognome){
        this.cognome = cognome;
    }

    public void registraPrestazione(Paziente p,String descrizionePrestazione){
        if(p!=null){
            LocalDateTime oraAttuale = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.println("Prestazione "+descrizionePrestazione+" registrata per il paziente: "+p.getNome()+" "+p.getCognome()+" "+p.getCf());
            System.out.println("Data/Ora "+ oraAttuale.format(formatter));
        }else{
            System.out.println("Paziente non valido");
        }
    }
    public void visualizzaTurni(){
        System.out.println("Turni Dottore "+ cognome);
        if(turni.isEmpty()){
            System.out.println("Nessun Turno in calendario");
        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for(LocalDateTime t : turni){
                System.out.println(t.format(formatter));
            }
        }
    }
    public void aggiungiTurnO(LocalDateTime dataOra){
        this.turni.add(dataOra);
    }
    public boolean rimuoviTurno(LocalDateTime dataOraTurno){
        boolean rimosso = this.turni.remove(dataOraTurno);
        if(rimosso){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.println("Turno "+dataOraTurno.format(formatter)+" rimosso dal calendario");
        }else{
            System.out.println("Nessun turno trovato per la data specificata");
        }
        return rimosso;
    }
}