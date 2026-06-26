package controller;

import dao.UtenteDAO;
import gui.InterfacciaAmministratore;
import gui.InterfacciaMedico;
import gui.Login;
import implementazioneDao.UtentePostgresDao;
import model.Amministratore;
import model.Medico;
import model.Utente;

import javax.swing.*;
import java.awt.Frame; // Aggiunto import per risolvere l'avviso di MAXIMIZED_BOTH
import java.util.Optional;
/**
 * Controller generico di accesso, coordinamento e instradamento principale dell'applicazione.
 * <p>
 * Questa classe ha il compito di gestire l'interfaccia grafica di Login, catturare le credenziali
 * inserite dall'utente, validarle tramite lo strato DAO e smistare dinamicamente l'applicazione
 * verso la corretta dashboard (Medico o Amministratore), gestendo inoltre il ciclo di chiusura sicura della finestra.
 * </p>
 */
public class Controller {
	private Login view;
	private JFrame mainFrame;
	private final UtenteDAO utenteDAO;

	/**
	 * Costruisce il controller di autenticazione principale, assegna i listener ai pulsanti di login
	 * e definisce il comportamento di chiusura programmata della finestra principale.
	 *
	 * @param view      il pannello grafico dell'interfaccia di Login
	 * @param mainFrame la finestra JFrame principale dell'applicazione
	 */
	public Controller(Login view, JFrame mainFrame) {
		this.view = view;
		this.mainFrame = mainFrame;

		this.utenteDAO = new UtentePostgresDao();

		this.view.setLoginListener(e -> gestisciLogin());
		this.view.setEnterListener(e -> gestisciLogin());

		this.mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		this.mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				int scelta = JOptionPane.showConfirmDialog(
						mainFrame,
						"Sei sicuro di voler uscire?",
						"Conferma Uscita",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
				);
				if (scelta == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

	}
	/**
	 * Raccoglie le credenziali inserite dall'utente nella GUI, interroga il servizio di autenticazione
	 * e procede con l'instradamento o mostra un messaggio di errore.
	 */
	private void gestisciLogin() {
		String u = view.getUsername();
		String p = view.getPassword();
		Utente utenteLoggato = autentica(u, p);

		if (utenteLoggato != null) {
			instradaUtente(utenteLoggato);
		} else {
			JOptionPane.showMessageDialog(mainFrame, "Credenziali errate.", "Errore Login", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * Verifica la validità delle credenziali fornite interrogando lo strato di persistenza dei dati.
	 *
	 * @param username l'identificativo utente inserito
	 * @param password la chiave d'accesso inserita
	 * @return l'oggetto Utente autenticato se le credenziali sono valide, null altrimenti
	 */
	private Utente autentica(String username, String password) {
		Optional<Utente> utente = utenteDAO.findByLogin(username);

		if (utente.isPresent() && utente.get().login(username, password)) {
			return utente.get();
		}

		return null;
	}
	/**
	 * Analizza il tipo specifico di ruolo dell'utente autenticato per istanziare il rispettivo
	 * sottocontrollore logico dedicato e la relativa interfaccia grafica.
	 * <p>
	 * Utilizza l'operatore di controllo dei tipi {@code instanceof} per determinare
	 * se l'utente loggato sia un medico o un amministratore:
	 * <ul>
	 * <li>Se l'utente è un'istanza di **Medico**, sfrutta il pattern matching per effettuare il cast automatico,
	 * istanzia la GUI {@link InterfacciaMedico}, ne crea il controllore dedicato {@link MedicoController} e richiede
	 * il cambio di schermata impostando il titolo personalizzato con il cognome del professionista.</li>
	 * <li>Se l'utente è un'istanza di **Amministratore**, istanzia la GUI {@link InterfacciaAmministratore},
	 * inizializza il rispettivo controllore di business {@link AdminController} e ordina la sostituzione del pannello grafico.</li>
	 * </ul>
	 * </p>
	 *
	 * @param utente l'utente loggato da instradare
	 */
	private void instradaUtente(Utente utente) {

		if (utente instanceof Medico medico) {
			InterfacciaMedico gui = new InterfacciaMedico();

			new MedicoController(gui, medico, mainFrame);

			cambiaSchermata(gui.getPanelMedico(), "Dashboard Medico - " + medico.getCognome());

		} else if (utente instanceof Amministratore amministratore) {
			InterfacciaAmministratore gui = new InterfacciaAmministratore();

			new AdminController(gui, amministratore, mainFrame);

			cambiaSchermata(gui.getPanelAmministratore(), "Dashboard Amministratore");
		}
	}
	/**
	 * Aggiorna il contenitore principale dell'applicazione inserendo il nuovo pannello,
	 * massimizzando la finestra a tutto schermo.
	 *
	 * @param nuovoPanel il pannello JPanel da visualizzare a schermo
	 * @param titolo     il testo da impostare nella barra del titolo della finestra
	 */
	private void cambiaSchermata(JPanel nuovoPanel, String titolo) {
		mainFrame.setTitle(titolo);
		mainFrame.setContentPane(nuovoPanel);
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.revalidate();
		mainFrame.repaint();
		mainFrame.setResizable(true);
	}
}