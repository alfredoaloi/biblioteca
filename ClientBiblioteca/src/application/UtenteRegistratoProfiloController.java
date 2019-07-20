package application;

import clientBiblioteca.Client;
import clientBiblioteca.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class UtenteRegistratoProfiloController {

	private Main main;

	@FXML
	private Label cognomeLabel;

	@FXML
	private Label usernameLabel;

	@FXML
	private Label nomeLabel;

	@FXML
	private Label emailLabel;

	@FXML
	private MenuItem libriNoleggiatiMenuItem;

	@FXML
	private MenuItem esciMenuItem;

	@FXML
	private MenuItem ricercaLibriMenuItem;

	private Client client;

	private Customer customer;

	public void setMain(Main m) {
		main = m;
		client = m.client;
	}

	// inizializza la scena
	public void init(Customer c) {
		customer = c;
		usernameLabel.setText("Username:	" + c.getUsername());
		nomeLabel.setText("Nome:		" + c.getName());
		cognomeLabel.setText("Cognome:	" + c.getSurname());
		emailLabel.setText("E-mail:		" + c.getE_mail());
	}

	// passa alla UtenteRegistratoLibriNoleggiariScene
	@FXML
	void libriNoleggiatiPressed(ActionEvent event) {
		main.setUtenteRegistratoLibriNoleggiatiScene(customer);
	}

	// passa alla UtenteRegistratoRicercaLibriScene
	@FXML
	void ricercaLibriPressed(ActionEvent event) {
		main.setUtenteRegistratoRicercaLibriScene(customer);
	}

	// passa alla PublicScene
	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}

}
