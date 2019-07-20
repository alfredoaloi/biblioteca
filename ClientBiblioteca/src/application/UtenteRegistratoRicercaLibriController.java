package application;

import java.io.File;
import java.util.ArrayList;

import clientBiblioteca.Book;
import clientBiblioteca.Category;
import clientBiblioteca.Client;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

public class UtenteRegistratoRicercaLibriController {

	private Main main;

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
	private MenuItem libriNoleggiatiMenuItem;

	@FXML
	private TextField cercaTextField;

	@FXML
	private Label bookDescr;

	@FXML
	private ImageView cercaButton;

	@FXML
	private TabPane tabPane;

	@FXML
	private Label bookTitle;

	private Client client;

	public void setMain(Main m) {
		main = m;
		client = m.client;
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
		ArrayList<Category> categorie = client.getCategoryList();
		ArrayList<Category> trovati = new ArrayList<Category>();
		for (Category cat : categorie) {
			trovati.add(new Category(cat.getCategoryType()));
			ArrayList<Book> books = new ArrayList<Book>();
			for (Book b : cat.getBooks()) {
				String tmp = b.getTitle().toLowerCase();
				if (tmp.contains(nome))
					books.add(b);
			}
			if (books.isEmpty()) {
				trovati.remove(trovati.size() - 1);
			} else {
				Book[] tmpBooks = new Book[books.size()];
				for (int i = 0; i < books.size(); i++) {
					tmpBooks[i] = books.get(i);
				}
				trovati.get(trovati.size() - 1).setBooks(tmpBooks);
			}
		}
		stampaCategorie(trovati);
	}

	// stampa i libri, per categoria, dato un ArrayList<Category>
	public void stampaCategorie(ArrayList<Category> categorie) {
		tabPane.getTabs().clear();
		if (categorie.isEmpty()) {
			bookTitle.setText("Nessun libro corrisponde ai parametri di ricerca");
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
		for (Category categoria : categorie) {
			Tab tab = new Tab(categoria.getCategoryType());
			VBox box = new VBox(10);
			box.setPadding(new Insets(5));
			for (Book book : categoria.getBooks()) {
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
	}

	// inizializza la scena
	public void init() {
		bookTitle.setText("Clicca su un libro per le informazioni");
		stampaCategorie(client.getCategoryList());
		cercaTextField.setText("");
	}

	// passa alla utenteRegistratoProfiloScene
	@FXML
	void profiloPressed(ActionEvent event) {
		main.setUtenteRegistratoProfiloScene();
	}

	// passa alla utenteRegistratoLibriNoleggiatiScene
	@FXML
	void libriNoleggiatiPressed(ActionEvent event) {
		//main.setUtenteRegistratoLibriNoleggiatiScene();
	}

	// passa alla publicScene
	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}
}
