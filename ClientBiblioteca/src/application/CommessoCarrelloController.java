package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import clientBiblioteca.Book;
import clientBiblioteca.Client;
import clientBiblioteca.Customer;
import clientBiblioteca.Envelope;
import clientBiblioteca.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class CommessoCarrelloController {

	private Main main;

	private Client client;

	@FXML
	private ImageView bookImg;

	@FXML
	private ImageView acquistaButton;

	@FXML
	private MenuItem profiloMenuItem;

	@FXML
	private MenuItem restituisciMenuItem;

	@FXML
	private MenuItem esciMenuItem;

	@FXML
	private MenuItem amministrazioneMenuItem;

	@FXML
	private Label bookISBN;

	@FXML
	private Label bookAuthor;

	@FXML
	private ImageView aggiungiAltriButton;

	@FXML
	private Label bookLang;

	@FXML
	private Label bookPages;

	@FXML
	private Label bookPublisher;

	@FXML
	private ImageView removeCartButton;

	@FXML
	private TextField cercaTextField;

	@FXML
	private Label bookDescr;

	@FXML
	private Label bookDueDate;

	@FXML
	private ImageView cercaButton;

	@FXML
	private TabPane tabPane;

	@FXML
	private Label bookTitle;

	@FXML
	private Label lendingPeriod;

	@FXML
	private Label fineIncrement;

	private Book activeBook;

	private ArrayList<Book> carrello;

	private User user;

	private Gson gson;

	public void setMain(Main m) {
		main = m;
		client = m.client;
		this.gson = new GsonBuilder().create();
	}

	// inizializza la scena
	public void init(ArrayList<Book> carrello, User user) {
		bookTitle.setText("Clicca su un libro per le informazioni");
		this.carrello = carrello;
		stampaLibri(carrello);
		cercaTextField.setText("");
		removeCartButton.setVisible(false);
		removeCartButton.setDisable(true);
		this.user = user;
	}

	// richiama cercaLibro()
	@FXML
	void cercaTextFieldKeyPressed(KeyEvent event) {
		if (!event.getCode().equals(KeyCode.ENTER))
			return;
		cercaLibro(cercaTextField.getText());
	}

	// richiama cercaLibro()
	@FXML
	void cercaButtonReleased(MouseEvent event) {
		cercaLibro(cercaTextField.getText());
	}

	// cerca dei libri e li stampa
	public void cercaLibro(String nome) {
		nome = nome.toLowerCase();
		ArrayList<Book> trovati = new ArrayList<Book>();
		for (Book book : carrello) {
			if (book.getTitle().toLowerCase().contains(nome))
				trovati.add(book);
		}
		stampaLibri(trovati);
	}

	// stampa i libri nel carrello
	public void stampaLibri(ArrayList<Book> carrello) {
		tabPane.getTabs().clear();
		if (carrello.isEmpty()) {
			bookTitle.setText("Nessun libro nel carrello soddisfa i parametri");
		} else {
			bookTitle.setText("Clicca su un libro per vederne le informazioni");
		}
		bookImg.setImage(null); // manca un metodo getImg() in Book;
		bookISBN.setText(null);
		bookAuthor.setText(null);
		bookPublisher.setText(null);
		bookLang.setText(null);
		bookPages.setText(null);
		bookDescr.setText(null);
		lendingPeriod.setText(null);
		fineIncrement.setText(null);
		removeCartButton.setVisible(false);
		removeCartButton.setDisable(true);
		Tab tab = new Tab("Carrello");
		VBox box = new VBox(10);
		box.setPadding(new Insets(5));
		for (Book book : carrello) {
			Label l = new Label(book.getTitle());
			l.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					vediInfo(book);
				}
			});
			box.getChildren().add(l);
		}
		ScrollPane scroll = new ScrollPane(box);
		tab.setContent(scroll);
		tabPane.getTabs().add(tab);
	}

	// stampa le info relative ad un libro cliccato
	public void vediInfo(Book book) {
		activeBook = book;
		bookTitle.setText(book.getTitle());
		File file = new File("images" + File.separator + book.getImage());
		bookImg.setImage(new Image(file.toURI().toString()));
		bookISBN.setText("ISBN: " + book.getISBN());
		bookAuthor.setText(book.getAuthor());
		bookPublisher.setText(book.getPublisher());
		bookLang.setText(book.getLanguage());
		bookPages.setText("Pagine: " + book.getnPages());
		bookDescr.setText(book.getDescription());
		lendingPeriod.setText("Noleggiabile per " + book.getLendingPeriod() + " giorni");
		fineIncrement.setText("Penale di " + book.getFineIncrement() + " euro/giorno");
		removeCartButton.setVisible(true);
		removeCartButton.setDisable(false);
	}

	// mette il libro selezionato nel carrello (ArrayList<Book>)
	@FXML
	void removeCartReleased(MouseEvent event) {
		activeBook.setnBooks(activeBook.getnBooks() + 1);
		carrello.remove(activeBook);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Libro rimosso dal carrello!");
		alert.setHeaderText(null);
		alert.setContentText(activeBook.getTitle() + " (ISBN: " + activeBook.getISBN() + ") rimosso dal carrello!");
		alert.showAndWait();
		stampaLibri(carrello);
	}

	// stampa il carrello
	@FXML
	void acquistaReleased(MouseEvent event) {
		if (carrello.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Errore");
			alert.setHeaderText(null);
			alert.setContentText("Il carrello e' vuoto");
			alert.showAndWait();
		} else {
			String cognome = dialogReturnsCognome();
			ArrayList<Customer> trovati = new ArrayList<Customer>();
			ArrayList<Customer> customers = client.getCustomersList();
			for (Customer c : customers)
				if (c.getSurname().toLowerCase().contains(cognome))
					trovati.add(c);
			if (trovati.isEmpty()) {
				nessunUtenteTrovato();
			} else {
				Customer customer = dialogOptionListCustomer(trovati);
				if (customer == null)
					return;
				String x = client.setCustomerLentBook(customer.getUsername(), carrello);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				}
				else {
					alertSuccesso();
				}
			}
		}
		client.refreshDB();
		carrello.clear();
		stampaLibri(carrello);
	}
	
	// alert di input
	private void alertSuccesso() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Successo");
		alert.setHeaderText(null);
		alert.setContentText("Operazione eseguita con successo");
		alert.showAndWait();
	}

	// alert di errore
	private void alertErrore(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore");
		alert.setHeaderText(null);
		alert.setContentText(error);
		alert.showAndWait();
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

	// passa a CommessoScene
	@FXML
	void aggiungiAltriReleased(MouseEvent event) {
		main.setCommessoScene(carrello, user);
	}

	// passa alla restituisciScene
	@FXML
	void restituisciPressed(ActionEvent event) {
		main.setRestituisciScene(user);
	}

	// passa a amministrazioneScene
	@FXML
	void amministrazionePressed(ActionEvent event) {
		main.setAmministrazioneScene();
	}

	// passa a commessoProfiloScene
	@FXML
	void profiloPressed(ActionEvent event) {
		System.out.println("profilo comm");
	}

	// passa a publicScene
	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}

}
