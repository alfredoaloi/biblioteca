package application;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import clientBiblioteca.Book;
import clientBiblioteca.Category;
import clientBiblioteca.Client;
import clientBiblioteca.Customer;
import clientBiblioteca.Envelope;
import clientBiblioteca.LentBook;
import clientBiblioteca.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class RestituisciController {

	private Main main;

	@FXML
	private ImageView restituisciButton;

	@FXML
	private ImageView bookImg;

	@FXML
	private MenuItem profiloMenuItem;

	@FXML
	private MenuItem esciMenuItem;

	@FXML
	private Label bookISBN;

	@FXML
	private Label bookAuthor;

	@FXML
	private Label bookLang;

	@FXML
	private Label bookPages;

	@FXML
	private Label bookPublisher;

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
	private MenuItem amministrazioneMenuItem;

	@FXML
	private MenuItem homeMenuItem;

	@FXML
	private Label fineIncrement;

	@FXML
	private Label giorniRimanenti;

	@FXML
	private Label multa;

	private Client client;

	private LentBook activeBook;

	private User user;

	private Customer activeCustomer;

	private Gson gson;

	public void setMain(Main m) {
		main = m;
		client = m.client;
		this.gson = new GsonBuilder().create();
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
		ArrayList<LentBook> trovati = new ArrayList<LentBook>();
		for (LentBook book : client.libriNoleggiati(activeCustomer.getUsername())) {
			if (book.getTitle().toLowerCase().contains(nome))
				trovati.add(book);
		}
		stampaLibri(trovati);
	}

	// stampa i libri nel carrello
	public void stampaLibri(ArrayList<LentBook> noleggiati) {
		tabPane.getTabs().clear();
		if (noleggiati.isEmpty()) {
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
		fineIncrement.setText(null);
		giorniRimanenti.setText(null);
		multa.setText(null);
		restituisciButton.setVisible(false);
		restituisciButton.setDisable(true);
		Tab tab = new Tab("Libri noleggiati");
		VBox box = new VBox(10);
		box.setPadding(new Insets(5));
		for (LentBook book : noleggiati) {
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
	public void vediInfo(LentBook book) {
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
		fineIncrement.setText("Penale di " + book.getFineIncrement() + " euro/giorno");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = dateFormat.parse(book.getDeadlineDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date now = new Date();
		long diff = date.getTime() - now.getTime();
		if (diff < 0)
			giorniRimanenti.setText("Scaduto");
		else
			giorniRimanenti
					.setText(Long.toString(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)) + " giorni rimanenti");
		multa.setText(Double.toString(book.getFine()));
		restituisciButton.setDisable(false);
		restituisciButton.setVisible(true);
	}

	// inizializza la scena
	public void init(User user) {
		bookTitle.setText("Clicca su un libro per le informazioni");
		cercaTextField.setText("");
		restituisciButton.setVisible(false);
		restituisciButton.setDisable(true);
		this.user = user;
		activeCustomer = null;

		String cognome = dialogReturnsCognome();
		if (cognome == null) {
			main.setCommessoScene(new ArrayList<Book>(), user);
			return;
		}
		ArrayList<Customer> trovati = new ArrayList<Customer>();
		ArrayList<Customer> customers = client.getCustomersList();
		for (Customer c : customers)
			if (c.getSurname().toLowerCase().contains(cognome))
				trovati.add(c);
		if (trovati.isEmpty()) {
			nessunUtenteTrovato();
			main.setCommessoScene(new ArrayList<Book>(), user);
			return;
		} else {
			Customer customer = dialogOptionListCustomer(trovati);
			if (customer == null) {
				main.setCommessoScene(new ArrayList<Book>(), user);
				return;
			}
			activeCustomer = customer;
			stampaLibri(client.libriNoleggiati(activeCustomer.getUsername()));
		}
	}

//alert di input
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

	@FXML
	public void restituisciReleased(MouseEvent event) {
		String x = client.returnBook(activeBook);
		client.refreshDB();
		stampaLibri(client.libriNoleggiati(activeCustomer.getUsername()));
		JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
		if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
			Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
			}.getType());
			alertErrore(reportEnvelope.getContent());
		} else {
			alertSuccesso();
			client.refreshDB();
			stampaLibri(client.libriNoleggiati(activeCustomer.getUsername()));
		}
	}

	// passa alla amministrazioneScene
	@FXML
	void amministrazionePressed(ActionEvent event) {
		main.setAmministrazioneScene();
	}

	// passa alla
	@FXML
	void profiloPressed(ActionEvent event) {
		System.out.println("profilo commesso");
	}

	// passa alla commessoScene
	@FXML
	void homePressed(ActionEvent event) {
		main.setCommessoScene(new ArrayList<Book>(), user);
	}

	// passa alla publicScene
	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}
}
