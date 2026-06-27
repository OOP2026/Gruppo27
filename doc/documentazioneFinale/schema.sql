CREATE TABLE utenti (
    login VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    ruolo VARCHAR(20) NOT NULL CHECK (ruolo IN ('MEDICO', 'AMMINISTRATORE')),
    nome VARCHAR(50),
    cognome VARCHAR(50),
    disponibile BOOLEAN DEFAULT TRUE
);

CREATE TABLE reparti (
    num INTEGER PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE stanze (
    numero INTEGER NOT NULL,
    reparto_num INTEGER NOT NULL REFERENCES reparti(num) ON DELETE CASCADE,
    PRIMARY KEY (numero, reparto_num)
);

CREATE TABLE letti (
    codice_inventario VARCHAR(20) PRIMARY KEY,
    libero BOOLEAN NOT NULL DEFAULT TRUE,
    stanza_numero INTEGER NOT NULL,
    reparto_num INTEGER NOT NULL,
    FOREIGN KEY (stanza_numero, reparto_num) REFERENCES stanze(numero, reparto_num) ON DELETE CASCADE
);

CREATE TABLE pazienti (
    cf VARCHAR(16) PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    recapito VARCHAR(50)
);

CREATE TABLE ricoveri (
    id SERIAL PRIMARY KEY,
    ssn VARCHAR(16) NOT NULL REFERENCES pazienti(cf) ON DELETE CASCADE,
    data_ricovero TIMESTAMP NOT NULL,
    data_dimissione_prevista TIMESTAMP,
    data_dimissione_effettiva TIMESTAMP,
    letto_codice VARCHAR(20) REFERENCES letti(codice_inventario),
    day_hospital BOOLEAN NOT NULL DEFAULT FALSE,
    descrizione TEXT,
    terapia TEXT,
    diagnosi_entrata VARCHAR(255),
    diagnosi_uscita VARCHAR(255) DEFAULT '-',
    in_corso BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE turni_lavorativi (
    id SERIAL PRIMARY KEY,
    medico_login VARCHAR(50) NOT NULL REFERENCES utenti(login) ON DELETE CASCADE,
    giorno VARCHAR(10) NOT NULL CHECK (giorno IN ('LUNEDI','MARTEDI','MERCOLEDI','GIOVEDI','VENERDI','SABATO','DOMENICA')),
    inizio TIMESTAMP NOT NULL,
    fine TIMESTAMP NOT NULL
);

CREATE TABLE prestazioni_mediche (
    id SERIAL PRIMARY KEY,
    medico_login VARCHAR(50) NOT NULL REFERENCES utenti(login) ON DELETE CASCADE,
    ssn_paziente VARCHAR(16) REFERENCES pazienti(cf) ON DELETE SET NULL,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('INTERVENTO_CHIRURGICO','VISITA')),
    data_ora TIMESTAMP NOT NULL,
    descrizione TEXT,
    esito VARCHAR(255)
);