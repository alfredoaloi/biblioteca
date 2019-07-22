package application;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import clientBiblioteca.Client;
import clientBiblioteca.Customer;
import clientBiblioteca.LentBook;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

public class UtenteRegistratoLibriNoleggiatiController {

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
	private MenuItem ricercaLibriMenuItem;

	@FXML
	private Label fineIncrement;

	@FXML
	private Label giorniRimanenti;

	@FXML
	private Label multa;

	private Main main;

	private Client client;

	Customer customer;

	public void setMain(Main m) {
		main = m;
		client = m.client;
	}

	public void init(Customer customer) {
		this.customer = customer;
		bookTitle.setText("Clicca su un libro per le informazioni");
		stampaLibri(client.libriNoleggiati(customer.getUsername()));
		cercaTextField.setText("");
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
		if (noleggiati.size() == 0) {
			bookTitle.setText("Nessun libro soddisfa i parametri");
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
	}

	@FXML
	void profiloPressed(ActionEvent event) {
		main.setUtenteRegistratoProfiloScene(customer);
	}

	@FXML
	void ricercaLibriPressed(ActionEvent event) {
		main.setUtenteRegistratoRicercaLibriScene(customer);
	}

	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}
}
