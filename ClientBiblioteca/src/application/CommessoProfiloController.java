package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import clientBiblioteca.Book;
import clientBiblioteca.Client;
import clientBiblioteca.Customer;
import clientBiblioteca.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

public class CommessoProfiloController {

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
	private MenuItem restituisciMenuItem;

	@FXML
	private MenuItem esciMenuItem;

	@FXML
	private MenuItem homeMenuItem;

	@FXML
	private MenuItem amministrazioneMenuItem;

	private Client client;

	private User user;

	public void setMain(Main m) {
		main = m;
		client = m.client;
	}

	// inizializza la scena
	public void init(User c) {
		user = c;
		usernameLabel.setText("Username:	" + c.getUsername());
		nomeLabel.setText("Nome:		" + c.getName());
		cognomeLabel.setText("Cognome:	" + c.getSurname());
		emailLabel.setText("E-mail:		" + c.getE_mail());
	}

	// passa alla UtenteRegistratoLibriNoleggiariScene
	@FXML
	void amministrazionePressed(ActionEvent event) {
		main.setAmministrazioneScene(user);
	}

	// passa alla UtenteRegistratoRicercaLibriScene
	@FXML
	void homePressed(ActionEvent event) {
		main.setCommessoScene(new ArrayList<Book>(), user);
	}

	@FXML
	void restituisciPressed(ActionEvent event) {
		String cognome = dialogReturnsCognome();
		if (cognome == null)
			return;
		ArrayList<Customer> trovati = new ArrayList<Customer>();
		ArrayList<Customer> customers = client.getCustomersList();
		for (Customer c : customers)
			if (c.getSurname().toLowerCase().contains(cognome))
				trovati.add(c);
		if (trovati.isEmpty()) {
			nessunUtenteTrovato();
			return;
		} else {
			Customer customer = dialogOptionListCustomer(trovati);
			if (customer == null)
				return;
			main.setRestituisciScene(user, customer);
		}
	}

	// ritorna una stringa
	private String dialogReturnsCognome() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Inserisci il cognome");
		dialog.setHeaderText(null);
		dialog.setContentText("Inserisci il cognome");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get().toLowerCase();
		else
			return null;
	}

	// alert di errore
	private void nessunUtenteTrovato() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Nesun utente trovato");
		alert.setHeaderText(null);
		alert.setContentText("Nessun utente trovato");
		alert.showAndWait();
	}

	// ritorna un utente data una sottostringa del cognome
	private Customer dialogOptionListCustomer(ArrayList<Customer> trovati) {
		ArrayList<String> utenti = new ArrayList<String>();
		for (Customer c : trovati)
			utenti.add(c.toString());
		List<String> choices = utenti;

		ChoiceDialog<String> dialog = new ChoiceDialog<String>("---", choices);
		dialog.setTitle("Scegli un utente");
		dialog.setHeaderText(null);
		dialog.setContentText("Scegli un utente");

		String temp;
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			temp = result.get();
			for (Customer c : trovati) {
				if (c.toString().equals(temp))
					return c;
			}
		}
		return null;
	}

	// passa alla PublicScene
	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}

}
