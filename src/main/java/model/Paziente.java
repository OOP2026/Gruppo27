package model;
/**
 * Rappresenta la scheda anagrafica di un paziente trattato dalla struttura sanitaria.
 * <p>
 * Memorizza in modo permanente il Codice Fiscale (usato come chiave primaria logica del sistema)
 * ed i dati di contatto ed identificativi personali dell'individuo.
 * </p>
 */
public class Paziente {
    private final String cf;
    private String nome;
    private String cognome;
    private String recapito;
    /**
     * Costruisce un oggetto Paziente valorizzandone tutti i campi anagrafici obbligatori.
     *
     * @param cf       il Codice Fiscale unico del paziente
     * @param nome     il nome del paziente
     * @param cognome  il cognome del paziente
     * @param recapito il recapito telefonico o indirizzo di contatto
     */
    public Paziente(String cf, String nome, String cognome, String recapito) {
        this.cf = cf;
        this.nome = nome;
        this.cognome = cognome;
        this.recapito = recapito;
    }
    /**
     * Restituisce il nome del paziente.
     *
     * @return la stringa del nome
     */
    public String getNome(){ return nome; }
    /**
     * Modifica il nome del paziente.
     *
     * @param nome il nuovo nome da impostare
     */
    public void setNome(String nome){
        this.nome = nome;
    }
    /**
     * Restituisce il cognome del paziente.
     *
     * @return la stringa del cognome
     */
    public String getCognome(){
        return cognome;
    }
    /**
     * Modifica il cognome del paziente.
     *
     * @param cognome il nuovo cognome da impostare
     */
    public void setCognome(String cognome){
        this.cognome = cognome;
    }
    /**
     * Restituisce il Codice Fiscale (CF) immutabile del paziente.
     *
     * @return la stringa del codice fiscale
     */
    public String getCf(){
        return cf;
    }
    /**
     * Restituisce il recapito telefonico registrato per il paziente.
     *
     * @return la stringa del recapito
     */
    public String getRecapito(){
        return recapito;
    }
    /**
     * Modifica il recapito telefonico del paziente.
     *
     * @param recapito il nuovo recapito da registrare
     */
    public void setRecapito(String recapito){
        this.recapito = recapito;
    }
}
