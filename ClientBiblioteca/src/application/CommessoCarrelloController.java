package application;

import java.io.File;
import java.util.ArrayList;

import clientBiblioteca.Book;
import clientBiblioteca.Client;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
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

	private Book activeBook;

	private ArrayList<Book> carrello;

	public void setMain(Main m) {
		main = m;
		client = m.client;
	}

	// inizializza la scena
	public void init(ArrayList<Book> carrello) {
		bookTitle.setText("Clicca su un libro per le informazioni");
		this.carrello = carrello;
		stampaLibri(carrello);
		cercaTextField.setText("");
		removeCartButton.setVisible(false);
		removeCartButton.setDisable(true);
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
		bookTitle.setText(book.getTitle());
		File file = new File("images" + File.separator + book.getImage());
		bookImg.setImage(new Image(file.toURI().toString()));
		bookISBN.setText("ISBN: " + book.getISBN());
		bookAuthor.setText(book.getAuthor());
		bookPublisher.setText(book.getPublisher());
		bookLang.setText(book.getLanguage());
		bookPages.setText("Pagine: " + book.getnPages());
		bookDescr.setText(book.getDescription());
		activeBook = book;
		removeCartButton.setVisible(true);
		removeCartButton.setDisable(false);
	}

	// mette il libro selezionato nel carrello (ArrayList<Book>)
	@FXML
	void removeCartReleased(MouseEvent event) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Libro rimosso dal carrello!");
		alert.setHeaderText(null);
		alert.setContentText(activeBook.getTitle() + " (ISBN: " + activeBook.getISBN() + ") rimosso dal carrello!");
		alert.showAndWait();
		carrello.remove(activeBook);
		stampaLibri(carrello);
	}

	// stampa il carrello
	@FXML
	void acquistaReleased(MouseEvent event) {
		System.out.println(carrello);
	}

	// passa a CommessoScene
	@FXML
	void aggiungiAltriReleased(MouseEvent event) {
		main.setCommessoScene(carrello);
	}

	// passa alla restituisciScene
	@FXML
	void restituisciPressed(ActionEvent event) {
		main.setRestituisciScene();
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
