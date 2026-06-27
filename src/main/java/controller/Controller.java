package controller;

import dao.UtenteDAO;
import gui.InterfacciaAmministratore;
import gui.InterfacciaMedico;
import gui.Login;
import gui.SchermataCaricamento;
import implementazioneDao.UtentePostgresDao;
import model.Amministratore;
import model.Medico;
import model.Utente;

import javax.swing.*;
import java.awt.Frame;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller generico di accesso, coordinamento e instradamento principale dell'applicazione.
 * <p>
 * Questa classe ha il compito di gestire l'interfaccia grafica di Login, catturare le credenziali
 * inserite dall'utente, validarle tramite lo strato DAO e smistare dinamicamente l'applicazione
 * verso la corretta dashboard (Medico o Amministratore), gestendo inoltre il ciclo di chiusura sicura della finestra.
 * </p>
 */
public class Controller {
	private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
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
	 * Raccoglie le credenziali inserite dall'utente nella GUI, avvia un thread separato (SwingWorker)
	 * per interrogare il servizio di autenticazione in cloud mostrando una schermata di caricamento,
	 * e procede con l'instradamento o mostra un messaggio di errore.
	 */
	private void gestisciLogin() {
		String u = view.getUsername();
		String p = view.getPassword();
		if (u.isEmpty() || p.isEmpty()) {
			JOptionPane.showMessageDialog(mainFrame, "Inserisci username e password.", "Errore", JOptionPane.WARNING_MESSAGE);
			return;
		}
		SchermataCaricamento loadingScreen = new SchermataCaricamento(mainFrame);
		SwingWorker<Utente, Void> worker = new SwingWorker<Utente, Void>() {

			@Override
			protected Utente doInBackground() throws Exception {
				return autentica(u, p);
			}

			@Override
			protected void done() {
				try {
					Utente utenteLoggato = get();

					if (utenteLoggato != null) {
						String identificativo;
						if (utenteLoggato instanceof Medico medico) {
							identificativo = "Dr."+ medico.getNome() + " " + medico.getCognome();
						} else {
							identificativo = utenteLoggato.getLogin();
						}
						loadingScreen.mostraBenvenuto(identificativo);
						loadingScreen.setMessaggio("Preparazione Dashboard in corso...");
						loadingScreen.nascondiBarra();
						Timer timer = new Timer(1500, e -> {
							instradaUtente(utenteLoggato);
							loadingScreen.dispose();
						});
						timer.setRepeats(false);
						timer.start();

					} else {
						loadingScreen.dispose();
						JOptionPane.showMessageDialog(mainFrame, "Credenziali errate.", "Errore Login", JOptionPane.ERROR_MESSAGE);
					}
				} catch (InterruptedException ie) {
					loadingScreen.dispose();
					Thread.currentThread().interrupt(); // Best practice: ri-interruzione
					LOGGER.log(Level.SEVERE, "L'operazione di login è stata interrotta.", ie);
				} catch (java.util.concurrent.ExecutionException ee) {
					loadingScreen.dispose();
					LOGGER.log(Level.SEVERE, "Errore di esecuzione durante il login.", ee);
					JOptionPane.showMessageDialog(mainFrame, "Errore di connessione al database.\nRiprova più tardi.", "Errore Server", JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					loadingScreen.dispose();
					LOGGER.log(Level.SEVERE, "Si è verificato un errore imprevisto.", ex);
					JOptionPane.showMessageDialog(mainFrame, "Errore imprevisto durante l'accesso.", "Errore", JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		worker.execute();
		loadingScreen.setVisible(true);
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