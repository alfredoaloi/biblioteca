package application;

import clientBiblioteca.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class UtenteRegistratoProfiloController {

	private Main main;

	@FXML
	private MenuItem libriNoleggiatiMenuItem;

	@FXML
	private MenuItem esciMenuItem;

	@FXML
	private MenuItem ricercaLibriMenuItem;

	private Client client;

	public void setMain(Main m) {
		main = m;
		client = m.client;
	}

	// inizializza la scena
	public void init() {
		System.out.println("initProfilo");
	}

	// passa alla UtenteRegistratoLibriNoleggiariScene
	@FXML
	void libriNoleggiatiPressed(ActionEvent event) {
		main.setUtenteRegistratoLibriNoleggiatiScene();
	}

	// passa alla UtenteRegistratoRicercaLibriScene
	@FXML
	void ricercaLibriPressed(ActionEvent event) {
		main.setUtenteRegistratoRicercaLibriScene();
	}

	// passa alla PublicScene
	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}

}
