package application;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import clientBiblioteca.Book;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	private TextArea bookDescr;

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

	private Customer customer;

	private Gson gson;

	public void setMain(Main m) {
		main = m;
		client = m.client;
		this.gson = new GsonBuilder().create();
	}

	@FXML
	void cercaTextFieldKeyPressed(KeyEvent event) {
		if (!event.getCode().equals(KeyCode.ENTER))
			return;
		cercaLibro(cercaTextField.getText());
	}

	@FXML
	void cercaButtonReleased(MouseEvent event) {
		cercaLibro(cercaTextField.getText());
	}

	public void cercaLibro(String nome) {
		nome = nome.toLowerCase();
		ArrayList<LentBook> trovati = new ArrayList<LentBook>();
		for (LentBook book : client.libriNoleggiati(customer.getUsername())) {
			if (book.getTitle().toLowerCase().contains(nome))
				trovati.add(book);
		}
		stampaLibri(trovati);
	}

	public void stampaLibri(ArrayList<LentBook> noleggiati) {
		tabPane.getTabs().clear();
		if (noleggiati.isEmpty()) {
			bookTitle.setText("Nessun libro noleggiato soddisfa i parametri");
		} else {
			bookTitle.setText("Clicca su un libro per vederne le informazioni");
		}
		bookImg.setImage(null);
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
		multa.setText("Mora ad oggi: " + Double.toString(book.getFine()) + " euro");
		restituisciButton.setDisable(false);
		restituisciButton.setVisible(true);
	}

	public void init(User user, Customer customer) {
		bookTitle.setText("Clicca su un libro per le informazioni");
		cercaTextField.setText("");
		restituisciButton.setVisible(false);
		restituisciButton.setDisable(true);
		this.user = user;
		this.customer = customer;
		stampaLibri(client.libriNoleggiati(customer.getUsername()));
	}

	private void alertSuccesso() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Successo");
		alert.setHeaderText(null);
		alert.setContentText("Operazione eseguita con successo");
		alert.showAndWait();
	}

	private void alertErrore(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore");
		alert.setHeaderText(null);
		alert.setContentText(error);
		alert.showAndWait();
	}

	@FXML
	public void restituisciReleased(MouseEvent event) {
		String x = client.returnBook(activeBook);
		client.refreshDB();
		stampaLibri(client.libriNoleggiati(customer.getUsername()));
		JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
		if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
			Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
			}.getType());
			alertErrore(reportEnvelope.getContent());
		} else {
			alertSuccesso();
			client.refreshDB();
			stampaLibri(client.libriNoleggiati(customer.getUsername()));
		}
	}

	@FXML
	void amministrazionePressed(ActionEvent event) {
		main.setAmministrazioneScene(user);
	}

	@FXML
	void profiloPressed(ActionEvent event) {
		main.setCommessoProfiloScene(user);
	}

	@FXML
	void homePressed(ActionEvent event) {
		main.setCommessoScene(new ArrayList<Book>(), user);
	}

	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}
}
