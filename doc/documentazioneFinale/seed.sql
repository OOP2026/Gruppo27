-- =========================================================================================
-- DATABASE OSPEDALIERO - SCRIPT DI POPOLAMENTO (SEED)
-- =========================================================================================

-- -----------------------------------------------------------------------------------------
-- 1. REPARTI
-- -----------------------------------------------------------------------------------------
INSERT INTO reparti (num, nome) VALUES
    (10, 'Cardiologia'),
    (20, 'Chirurgia Generale'),
    (30, 'Pediatria'),
    (40, 'Ortopedia e Traumatologia'),
    (50, 'Neurologia');

-- -----------------------------------------------------------------------------------------
-- 2. STANZE
-- -----------------------------------------------------------------------------------------
INSERT INTO stanze (numero, reparto_num) VALUES
    -- Reparto 10: Cardiologia
    (101, 10), (102, 10), (103, 10), (104, 10), (105, 10),
    -- Reparto 20: Chirurgia Generale
    (201, 20), (202, 20), (203, 20), (204, 20), (205, 20),
    -- Reparto 30: Pediatria
    (301, 30), (302, 30), (303, 30), (304, 30), (305, 30),
    -- Reparto 40: Ortopedia e Traumatologia
    (401, 40), (402, 40), (403, 40), (404, 40), (405, 40),
    -- Reparto 50: Neurologia
    (501, 50), (502, 50), (503, 50), (504, 50), (505, 50);

-- -----------------------------------------------------------------------------------------
-- 3. LETTI
-- -----------------------------------------------------------------------------------------
INSERT INTO letti (codice_inventario, libero, stanza_numero, reparto_num) VALUES
    -- Cardiologia
    ('L-101-A', FALSE, 101, 10), ('L-101-B', TRUE,  101, 10),
    ('L-102-A', TRUE,  102, 10), ('L-102-B', TRUE,  102, 10),
    ('L-103-A', TRUE,  103, 10), ('L-103-B', TRUE,  103, 10),
    ('L-104-A', TRUE,  104, 10), ('L-104-B', TRUE,  104, 10),
    ('L-105-A', TRUE,  105, 10), ('L-105-B', TRUE,  105, 10),

    -- Chirurgia Generale
    ('L-201-A', FALSE, 201, 20), ('L-201-B', TRUE,  201, 20),
    ('L-202-A', TRUE,  202, 20), ('L-202-B', TRUE,  202, 20),
    ('L-203-A', TRUE,  203, 20), ('L-203-B', TRUE,  203, 20),
    ('L-204-A', TRUE,  204, 20), ('L-204-B', TRUE,  204, 20),
    ('L-205-A', TRUE,  205, 20), ('L-205-B', TRUE,  205, 20),

    -- Pediatria
    ('L-301-A', FALSE, 301, 30), ('L-301-B', TRUE,  301, 30),
    ('L-302-A', TRUE,  302, 30), ('L-302-B', TRUE,  302, 30),
    ('L-303-A', TRUE,  303, 30), ('L-303-B', TRUE,  303, 30),
    ('L-304-A', TRUE,  304, 30), ('L-304-B', TRUE,  304, 30),
    ('L-305-A', TRUE,  305, 30), ('L-305-B', TRUE,  305, 30),

    -- Ortopedia e Traumatologia
    ('L-401-A', FALSE, 401, 40), ('L-401-B', TRUE,  401, 40),
    ('L-402-A', TRUE,  402, 40), ('L-402-B', TRUE,  402, 40),
    ('L-403-A', TRUE,  403, 40), ('L-403-B', TRUE,  403, 40),
    ('L-404-A', TRUE,  404, 40), ('L-404-B', TRUE,  404, 40),
    ('L-405-A', TRUE,  405, 40), ('L-405-B', TRUE,  405, 40),

    -- Neurologia
    ('L-501-A', FALSE, 501, 50), ('L-501-B', TRUE,  501, 50),
    ('L-502-A', TRUE,  502, 50), ('L-502-B', TRUE,  502, 50),
    ('L-503-A', TRUE,  503, 50), ('L-503-B', TRUE,  503, 50),
    ('L-504-A', TRUE,  504, 50), ('L-504-B', TRUE,  504, 50),
    ('L-505-A', TRUE,  505, 50), ('L-505-B', TRUE,  505, 50);

-- -----------------------------------------------------------------------------------------
-- 4. UTENTI (Medici e Amministratori)
-- Password in chiaro per tutti: pass
-- Gli hash sono generati con BCrypt (org.mindrot.jbcrypt), stesso algoritmo usato
-- da util.PasswordHasher; ogni hash è stato verificato corrispondere a "pass" prima
-- dell'inserimento in questo script.
-- -----------------------------------------------------------------------------------------
INSERT INTO utenti (login, password, ruolo, nome, cognome, disponibile) VALUES
    -- Medici
    ('drTramontana', '$2a$10$wHJMo3N12/XkZAXKITn9HeI3Hc6rphXsBXhr26PA436YTCTcsZHcu', 'MEDICO', 'Porfirio',  'Tramontana', TRUE),
    ('drGiordano',   '$2a$10$N/tQ1hLxFWbXZCn/ZiYFauLu3Kma6WlnDjFSwho3NXbxKJMpdZ.VG', 'MEDICO', 'Francesco', 'Giordano',   TRUE),

    -- Amministratori
    ('admTramontana','$2a$10$nWB3zHjhVUXdiUYokE0AaO0OhMuA80LxQy98hXsnHbUUNnu4u5aD2', 'AMMINISTRATORE', 'Porfirio', 'Tramontana', TRUE),
    ('admGada',      '$2a$10$HyntFVUIcZDlto/FIMhTfOVCIhUxZLqU0AQYX0eGIh.CK0Y7jynSC', 'AMMINISTRATORE', 'Mykhaylo', 'Gada',       TRUE);

-- -----------------------------------------------------------------------------------------
-- 5. PAZIENTI (20 anagrafiche)
-- -----------------------------------------------------------------------------------------
INSERT INTO pazienti (cf, nome, cognome, recapito) VALUES
    ('RSSMRA80A01H501A', 'Mario',     'Rossi',     '+39 333 1234567'),
    ('BNCGNN70B12F205B', 'Giovanna',  'Bianchi',   '+39 347 7654321'),
    ('VRDMTT95C23L219C', 'Matteo',    'Verdi',     '+39 320 9876543'),
    ('GLILCA88D04H501D', 'Luca',      'Gialli',    '+39 331 1122334'),
    ('NRISRA92E45F205E', 'Sara',      'Neri',      NULL),
    ('MRNMCO65F16L219F', 'Marco',     'Marini',    '+39 328 4455667'),
    ('FBNLGI78G27H501G', 'Luigi',     'Fabiani',   '+39 339 9988776'),
    ('CSTLRA85H48F205H', 'Laura',     'Costa',     '+39 340 5544332'),
    ('RMNFNC99I09L219I', 'Francesco', 'Romano',    '+39 345 6677889'),
    ('BRBPLA72J20H501J', 'Paola',     'Barbieri',  '+39 366 2233445'),
    ('SNTGPP60K11F205K', 'Giuseppe',  'Santoro',   NULL),
    ('LMBMRT91L52L219L', 'Marta',     'Lombardi',  '+39 348 3344556'),
    ('GLLANT83M23H501M', 'Antonio',   'Galli',     '+39 334 1122998'),
    ('FLCFRD77N14F205N', 'Federico',  'Falco',     '+39 327 7788990'),
    ('MNTLCA89O05L219O', 'Lucia',     'Monti',     '+39 329 1100223'),
    ('PRZMRC94P16H501P', 'Mirco',     'Preziusi',  '+39 333 4455667'),
    ('DLCRTZ81Q47F205Q', 'Patrizia',  'De Luca',   '+39 347 8899001'),
    ('FRRVNT68R28L219R', 'Valentina', 'Ferri',     NULL),
    ('CPLDNL96S09H501S', 'Daniele',   'Capuano',   '+39 338 5566778'),
    ('BSSLNZ75T20F205T', 'Lorenzo',   'Bassi',     '+39 320 6677889');

INSERT INTO turni_lavorativi (medico_login, giorno, inizio, fine) VALUES
    -- MARZO 2026
    ('drTramontana', 'LUNEDI',    '2026-03-30 08:00:00', '2026-03-30 14:00:00'),
    ('drGiordano',   'MARTEDI',   '2026-03-31 14:00:00', '2026-03-31 22:00:00'),

    -- APRILE 2026
    ('drTramontana', 'MERCOLEDI', '2026-04-01 08:00:00', '2026-04-01 14:00:00'),
    ('drTramontana', 'VENERDI',   '2026-04-03 08:00:00', '2026-04-03 14:00:00'),
    ('drGiordano',   'MARTEDI',   '2026-04-07 14:00:00', '2026-04-07 22:00:00'),
    ('drGiordano',   'GIOVEDI',   '2026-04-09 14:00:00', '2026-04-09 22:00:00'),
    ('drGiordano',   'SABATO',    '2026-04-11 08:00:00', '2026-04-11 14:00:00'),
    ('drTramontana', 'LUNEDI',    '2026-04-13 08:00:00', '2026-04-13 14:00:00'),
    ('drTramontana', 'MERCOLEDI', '2026-04-15 08:00:00', '2026-04-15 14:00:00'),
    ('drGiordano',   'SABATO',    '2026-04-25 08:00:00', '2026-04-25 14:00:00'),

    -- MAGGIO 2026
    ('drTramontana', 'LUNEDI',    '2026-05-04 08:00:00', '2026-05-04 14:00:00'),
    ('drTramontana', 'VENERDI',   '2026-05-08 08:00:00', '2026-05-08 14:00:00'),
    ('drGiordano',   'GIOVEDI',   '2026-05-14 14:00:00', '2026-05-14 22:00:00'),
    ('drGiordano',   'MARTEDI',   '2026-05-19 14:00:00', '2026-05-19 22:00:00'),
    ('drTramontana', 'MERCOLEDI', '2026-05-20 08:00:00', '2026-05-20 14:00:00'),

    -- GIUGNO 2026 (Mese Corrente)
    ('drTramontana', 'MERCOLEDI', '2026-06-03 08:00:00', '2026-06-03 14:00:00'),
    ('drTramontana', 'VENERDI',   '2026-06-05 08:00:00', '2026-06-05 14:00:00'),
    ('drGiordano',   'GIOVEDI',   '2026-06-11 14:00:00', '2026-06-11 22:00:00'),
    ('drGiordano',   'SABATO',    '2026-06-13 08:00:00', '2026-06-13 14:00:00'),
    ('drTramontana', 'LUNEDI',    '2026-06-22 08:00:00', '2026-06-22 14:00:00'),
    ('drGiordano',   'MARTEDI',   '2026-06-23 14:00:00', '2026-06-23 22:00:00'),
    ('drGiordano',   'GIOVEDI',   '2026-06-25 14:00:00', '2026-06-25 22:00:00'),
    ('drTramontana', 'VENERDI',   '2026-06-26 08:00:00', '2026-06-26 14:00:00'),
    ('drGiordano',   'SABATO',    '2026-06-27 08:00:00', '2026-06-27 14:00:00'),

    -- LUGLIO 2026 (Turni Futuri)
    ('drTramontana', 'LUNEDI',    '2026-07-06 08:00:00', '2026-07-06 14:00:00'),
    ('drGiordano',   'MARTEDI',   '2026-07-07 14:00:00', '2026-07-07 22:00:00'),
    ('drTramontana', 'MERCOLEDI', '2026-07-08 08:00:00', '2026-07-08 14:00:00'),
    ('drGiordano',   'GIOVEDI',   '2026-07-09 14:00:00', '2026-07-09 22:00:00'),
    ('drGiordano',   'SABATO',    '2026-07-11 08:00:00', '2026-07-11 14:00:00'),

    -- AGOSTO 2026 (Turni Futuri)
    ('drTramontana', 'LUNEDI',    '2026-08-03 08:00:00', '2026-08-03 14:00:00'),
    ('drGiordano',   'GIOVEDI',   '2026-08-06 14:00:00', '2026-08-06 22:00:00'),
    ('drTramontana', 'VENERDI',   '2026-08-07 08:00:00', '2026-08-07 14:00:00'),

    -- SETTEMBRE 2026 (Turni Futuri)
    ('drTramontana', 'LUNEDI',    '2026-09-07 08:00:00', '2026-09-07 14:00:00'),
    ('drGiordano',   'MARTEDI',   '2026-09-08 14:00:00', '2026-09-08 22:00:00'),
    ('drTramontana', 'VENERDI',   '2026-09-11 08:00:00', '2026-09-11 14:00:00'),
    ('drGiordano',   'SABATO',    '2026-09-12 08:00:00', '2026-09-12 14:00:00');

-- -----------------------------------------------------------------------------------------
-- 7. RICOVERI
-- -----------------------------------------------------------------------------------------
INSERT INTO ricoveri (ssn, data_ricovero, data_dimissione_prevista, data_dimissione_effettiva, letto_codice, day_hospital, descrizione, terapia, diagnosi_entrata, diagnosi_uscita, in_corso) VALUES

    -- === RICOVERI IN CORSO (allocati nei 5 letti occupati) ===
    ('RSSMRA80A01H501A', '2026-06-25 09:00:00', '2026-07-02 12:00:00', NULL, 'L-101-A', FALSE, 'Monitoraggio pressorio continuo.', 'Beta-bloccanti EV', 'Sospetta ischemia miocardica', '-', TRUE),
    ('BNCGNN70B12F205B', '2026-06-26 11:30:00', '2026-06-30 14:00:00', NULL, 'L-201-A', FALSE, 'Decorso post-operatorio regolare.', 'Cefazolina 1g, Ketorolac', 'Colecistite acuta', '-', TRUE),
    ('VRDMTT95C23L219C', '2026-06-27 08:15:00', '2026-06-29 10:00:00', NULL, 'L-301-A', FALSE, 'Osservazione pediatrica per disidratazione.', 'Soluzione fisiologica EV', 'Gastroenterite acuta', '-', TRUE),
    ('GLILCA88D04H501D', '2026-06-20 18:00:00', '2026-07-15 12:00:00', NULL, 'L-401-A', FALSE, 'Immobilizzazione arto inferiore dx.', 'Eparina a basso peso molecolare', 'Frattura scomposta femore', '-', TRUE),
    ('NRISRA92E45F205E', '2026-06-24 22:45:00', '2026-07-05 16:00:00', NULL, 'L-501-A', FALSE, 'Valutazione neurologica post-traumatica.', 'Desametasone EV', 'Commozione cerebrale', '-', TRUE),

    -- === RICOVERI STORICI (dimessi) ===
    ('MRNMCO65F16L219F', '2026-05-10 10:00:00', '2026-05-15 12:00:00', '2026-05-14 18:30:00', NULL, FALSE, 'Decorso completato in anticipo.', 'Riposo', 'Lombalgia acuta', 'Lombalgia risolta', FALSE),
    ('FBNLGI78G27H501G', '2026-06-01 09:00:00', '2026-06-05 10:00:00', '2026-06-05 11:00:00', NULL, FALSE, 'Dimesso con terapia domiciliare.', 'Antibiotico orale', 'Polmonite comunitaria', 'Polmonite in via di risoluzione', FALSE),

    -- === DAY HOSPITAL ===
    ('CSTLRA85H48F205H', '2026-06-27 07:30:00', '2026-06-27 18:00:00', NULL, NULL, TRUE, 'Infusione programmata biologici.', 'Infliximab 5mg/kg', 'Artrite Reumatoide', '-', TRUE);

-- -----------------------------------------------------------------------------------------
-- 8. PRESTAZIONI MEDICHE (Da Marzo a Oggi)

-- -----------------------------------------------------------------------------------------
INSERT INTO prestazioni_mediche (medico_login, ssn_paziente, tipo, data_ora, descrizione, esito) VALUES
    -- Storico Marzo
    ('drTramontana', 'MRNMCO65F16L219F', 'VISITA', '2026-03-30 10:00:00', 'Prima visita ortopedica per dolori lombari', 'Prescritta risonanza magnetica'),
    
    -- Storico Aprile
    ('drGiordano',   'BNCGNN70B12F205B', 'VISITA', '2026-04-09 15:30:00', 'Consulenza chirurgica preliminare', 'Si consiglia intervento in elezione'),
    ('drTramontana', 'RSSMRA80A01H501A', 'VISITA', '2026-04-15 09:15:00', 'Elettrocardiogramma da sforzo', 'Parametri stabili, lieve affaticamento'),
    ('drGiordano',   'GLILCA88D04H501D', 'INTERVENTO_CHIRURGICO', '2026-04-25 10:00:00', 'Riduzione frattura e sintesi con placca', 'Intervento ortopedico riuscito'),

    -- Storico Maggio
    ('drTramontana', 'FBNLGI78G27H501G', 'VISITA', '2026-05-08 11:45:00', 'Auscultazione toracica preventiva', 'Rilevati rumori umidi basali'),
    ('drGiordano',   'CSTLRA85H48F205H', 'VISITA', '2026-05-19 18:00:00', 'Valutazione idoneità per Day Hospital', 'Paziente idoneo al trattamento biologico'),

    -- Storico Giugno
    ('drTramontana', 'FBNLGI78G27H501G', 'VISITA', '2026-06-05 10:30:00', 'Visita dimissione post-ricovero', 'Paziente eupnoico, polmonite in risoluzione'),
    ('drGiordano',   'BNCGNN70B12F205B', 'INTERVENTO_CHIRURGICO', '2026-06-11 16:20:00', 'Colecistectomia laparoscopica programmata', 'Concluso senza complicanze'),
    
    -- Recenti / Oggi (26-27 Giugno)
    ('drTramontana', 'RSSMRA80A01H501A', 'VISITA', '2026-06-26 08:30:00', 'Visita cardiologica di controllo in reparto', 'Ritmo sinusale, PA 130/80'),
    ('drGiordano',   'VRDMTT95C23L219C', 'VISITA', '2026-06-27 09:00:00', 'Controllo parametri vitali pediatrici', 'Segni di disidratazione in miglioramento'),
    ('drGiordano',   'CSTLRA85H48F205H', 'VISITA', '2026-06-27 11:30:00', 'Monitoraggio infusione Day Hospital', 'Nessuna reazione avversa al farmaco');