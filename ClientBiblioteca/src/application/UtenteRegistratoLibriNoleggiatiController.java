package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class UtenteRegistratoLibriNoleggiatiController {

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
	private MenuItem ricercaLibriMenuItem;

	public void setMain(Main m) {
		main = m;
	}

	@FXML
	void cercaTextFieldKeyPressed(KeyEvent event) {
		if (!event.getCode().equals(KeyCode.ENTER))
			return;
		System.out.println(cercaTextField.getText());
		// cercaLibro(cercaTextField.getText());
	}

	@FXML
	void cercaButtonReleased(MouseEvent event) {
		System.out.println(cercaTextField.getText());
		// cercaLibro(cercaTextField.getText());
	}

	void cercaLibro(String nome) {

	}

	@FXML
	void profiloPressed(ActionEvent event) {
		System.out.println("profilo");
	}

	@FXML
	void ricercaLibriPressed(ActionEvent event) {
		System.out.println("ricerca libri");
		
		main.setUtenteRegistratoRicercaLibriScene();
	}

	@FXML
	void esciPressed(ActionEvent event) {
		System.out.println("esci");
		
		main.setPublicScene();
	}
}
