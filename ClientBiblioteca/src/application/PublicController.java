package application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import clientBiblioteca.AccessCredentials;
import clientBiblioteca.Book;
import clientBiblioteca.Category;
import clientBiblioteca.Client;
import clientBiblioteca.Envelope;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

// button color = #077807

public class PublicController {

	private Main main;

	@FXML
	private ImageView bookImg;

	@FXML
	private Label bookPublisher;

	@FXML
	private TextField cercaTextField;

	@FXML
	private Label bookDescr;

	@FXML
	private ImageView loginButton;

	@FXML
	private Label bookISBN;

	@FXML
	private Label bookAuthor;

	@FXML
	private ImageView cercaButton;

	@FXML
	private TabPane tabPane;

	@FXML
	private Label bookTitle;

	@FXML
	private Label bookLang;

	@FXML
	private Label bookPages;

	private Client client;

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

	// premuto il bottone di Login
	@FXML
	void loginButtonReleased(MouseEvent event) {

		Pair<String, String> loginResult = showLogin();
		if (loginResult == null)
			return;

		String x = client.loginUser(loginResult);
		JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
		if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
			Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
			}.getType());
			alertErrore(reportEnvelope.getContent());
		} else {
			Envelope<String> userCredentialsEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
			}.getType());
			if (userCredentialsEnvelope.getObject().equalsIgnoreCase("EMPLOYEE"))
				main.setCommessoScene(new ArrayList<Book>());
			else if (userCredentialsEnvelope.getObject().equalsIgnoreCase("CUSTOMER"))
				main.setUtenteRegistratoLibriNoleggiatiScene();
		}
	}

	// alert di errore
	private void alertErrore(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore");
		alert.setHeaderText(null);
		alert.setContentText(error);
		alert.showAndWait();
	}

	// interfaccia di login, restituisce username e password (anche nulli)
	private Pair<String, String> showLogin() {
		Dialog<Pair<String, String>> dialog = new Dialog<Pair<String, String>>();
		dialog.setTitle("Login");
		dialog.setHeaderText("Inserisci le tue credenziali");

		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Annulla", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, cancelButtonType);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		username.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				loginButton.setDisable(newValue.trim().isEmpty());
			}
		});

		dialog.getDialogPane().setContent(grid);
		username.requestFocus();

		dialog.setResultConverter(new Callback<ButtonType, Pair<String, String>>() {

			@Override
			public Pair<String, String> call(ButtonType param) {
				if (param == loginButtonType)
					return new Pair<String, String>(username.getText(), password.getText());
				return null;
			}
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		if (result.isPresent())
			return result.get();
		else
			return null;
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
}
