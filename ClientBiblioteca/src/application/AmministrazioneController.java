package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import clientBiblioteca.Book;
import clientBiblioteca.Category;
import clientBiblioteca.Client;
import clientBiblioteca.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class AmministrazioneController {

	@FXML
	private TextField t4;

	@FXML
	private Label delLibro;

	@FXML
	private TextField t5;

	@FXML
	private TextField t6;

	@FXML
	private TextField t7;

	@FXML
	private MenuItem restituisciMenuItem;

	@FXML
	private MenuItem profiloMenuItem;

	@FXML
	private TextArea t8;

	@FXML
	private MenuItem homeMenuItem;

	@FXML
	private Label addLibro;

	@FXML
	private Label l1;

	@FXML
	private MenuItem esciMenuItem;

	@FXML
	private Label l2;

	@FXML
	private Label l3;

	@FXML
	private Label modUtente;

	@FXML
	private Label l4;

	@FXML
	private Label l5;

	@FXML
	private Label addUtente;

	@FXML
	private Label l6;

	@FXML
	private Label l7;

	@FXML
	private Label l8;

	@FXML
	private Label delUtente;

	@FXML
	private Label modLibro;

	@FXML
	private TextField t1;

	@FXML
	private ImageView inviaButton;

	@FXML
	private TextField t2;

	@FXML
	private TextField t3;

	@FXML
	private Label addCommesso;

	@FXML
	private Label modCommesso;

	@FXML
	private Label delCommesso;

	boolean utente;

	boolean mod;

	private Main main;

	private Client client;

	public void setMain(Main m) {
		this.main = m;
		this.client = m.client;
	}

	// inizializza la scena
	public void init() {
		l1.setVisible(false);
		t1.setVisible(false);
		l2.setVisible(false);
		t2.setVisible(false);
		l3.setVisible(false);
		t3.setVisible(false);
		l4.setVisible(false);
		t4.setVisible(false);
		l5.setVisible(false);
		t5.setVisible(false);
		l6.setVisible(false);
		t6.setVisible(false);
		l7.setVisible(false);
		t7.setVisible(false);
		l8.setVisible(false);
		t8.setVisible(false);
		inviaButton.setVisible(false);
		inviaButton.setDisable(true);
	}

	// aggiunge un nuovo utente
	@FXML
	void addUtenteReleased(MouseEvent event) {
		utente = true;
		mod = false;
		l1.setText("Username	");
		l1.setVisible(true);
		t1.setText("");
		t1.setVisible(true);
		t1.setEditable(true);
		l2.setText("Password		");
		l2.setVisible(true);
		t2.setText("");
		t2.setVisible(true);
		l3.setText("Nome		");
		l3.setVisible(true);
		t3.setText("");
		t3.setVisible(true);
		l4.setText("Cognome		");
		l4.setVisible(true);
		t4.setText("");
		t4.setVisible(true);
		l5.setText("E-mail		");
		l5.setVisible(true);
		t5.setText("");
		t5.setVisible(true);
		l6.setVisible(false);
		t6.setVisible(false);
		l7.setVisible(false);
		t7.setVisible(false);
		l8.setVisible(false);
		t8.setVisible(false);
		inviaButton.setDisable(false);
		inviaButton.setVisible(true);
	}

	// modificaun utente
	@FXML
	void modUtenteReleased(MouseEvent event) {
		utente = true;
		mod = true;
		String cognome = dialogReturnsCognome();
		if (cognome == null)
			return;
		// cerca il nome tra i cognomi della gente con for ecc in un arraylist di user
		ArrayList<User> trovati = new ArrayList<User>();
		// for tutti gli user
		User aCaso = new User("Alfredo", "ciao", "Alfredo", "Aloi", "alfreduzzo@a.j");
		if (aCaso.getSurname().toLowerCase().contains(cognome))
			trovati.add(aCaso);
		if (trovati.isEmpty()) {
			nessunUtenteTrovato();
		} else {
			User user = dialogOptionListUser(trovati);
			if (user == null)
				return;
			System.out.println(user);
			l1.setText("Username	");
			l1.setVisible(true);
			t1.setText(user.getUsername());
			t1.setEditable(false);
			t1.setVisible(true);
			l2.setText("Password		");
			l2.setVisible(true);
			t2.setText(user.getPassword());
			t2.setVisible(true);
			l3.setText("Nome		");
			l3.setVisible(true);
			t3.setText(user.getName());
			t3.setVisible(true);
			l4.setText("Cognome		");
			l4.setVisible(true);
			t4.setText(user.getSurname());
			t4.setVisible(true);
			l5.setText("E-mail		");
			l5.setVisible(true);
			t5.setText(user.getE_mail());
			t5.setVisible(true);
			l6.setVisible(false);
			t6.setVisible(false);
			l7.setVisible(false);
			t7.setVisible(false);
			l8.setVisible(false);
			t8.setVisible(false);
			inviaButton.setDisable(false);
			inviaButton.setVisible(true);
		}
	}

	// elimina un utente
	@FXML
	void delUtenteReleased(MouseEvent event) {
		String cognome = dialogReturnsCognome();
		// cerca il nome tra i cognomi della gente con for ecc in un arraylist di user
		ArrayList<User> trovati = new ArrayList<User>();
		// for tutti gli user
		User aCaso = new User("Alfredo", "ciao", "Alfredo", "Aloi", "alfreduzzo@a.j");
		if (aCaso.getSurname().toLowerCase().contains(cognome))
			trovati.add(aCaso);
		if (trovati.isEmpty()) {
			nessunUtenteTrovato();
		} else {
			User user = dialogOptionListUser(trovati);
			if (user == null)
				return;
			if (sicuroElimina())
				delConfermata();
			// delete user
			else
				System.out.println(user + " non eliminato");
		}
	}

	// aggiunge un nuovo libro
	@FXML
	void addLibroReleased(MouseEvent event) {
		utente = false;
		mod = false;
		l1.setText("Titolo		");
		l1.setVisible(true);
		t1.setText("");
		t1.setVisible(true);
		t1.setEditable(true);
		l2.setText("Autore		");
		l2.setVisible(true);
		t2.setText("");
		t2.setVisible(true);
		l3.setText("Pagine		");
		l3.setVisible(true);
		t3.setText("");
		t3.setVisible(true);
		l4.setText("Editore		");
		l4.setVisible(true);
		t4.setText("");
		t4.setVisible(true);
		l5.setText("Lingua		");
		l5.setVisible(true);
		t5.setText("");
		t5.setVisible(true);
		l6.setText("ISBN			");
		l6.setVisible(true);
		t6.setText("");
		t6.setVisible(true);
		t6.setEditable(true);
		l7.setText("Categoria		");
		l7.setVisible(true);
		t7.setText("");
		t7.setVisible(true);
		l8.setText("Descrizione	");
		l8.setVisible(true);
		t8.setText("");
		t8.setVisible(true);
		inviaButton.setDisable(false);
		inviaButton.setVisible(true);
	}

	// modifica un libro
	@FXML
	void modLibroReleased(MouseEvent event) {
		utente = false;
		mod = true;
		String titolo = dialogReturnsTitolo();
		if (titolo == null)
			return;
		// cerca il titolo tra i titoli dedei libri
		ArrayList<Book> trovati = new ArrayList<Book>();
		for (Category c : client.getCategoryList()) {
			for (Book b : c.getBooks()) {
				if (b.getTitle().toLowerCase().contains(titolo)) {
					trovati.add(b);
				}
			}
		}
		if (trovati.isEmpty()) {
			nessunLibroTrovato();
		} else {
			Book book = dialogOptionListBook(trovati);
			if (book == null)
				return;
			l1.setText("Titolo		");
			l1.setVisible(true);
			t1.setText(book.getTitle());
			t1.setVisible(true);
			l2.setText("Autore		");
			l2.setVisible(true);
			t2.setText(book.getAuthor());
			t2.setVisible(true);
			l3.setText("Pagine		");
			l3.setVisible(true);
			t3.setText(Integer.toString(book.getnPages()));
			t3.setVisible(true);
			l4.setText("Editore		");
			l4.setVisible(true);
			t4.setText(book.getPublisher());
			t4.setVisible(true);
			l5.setText("Lingua		");
			l5.setVisible(true);
			t5.setText(book.getLanguage());
			t5.setVisible(true);
			l6.setText("ISBN			");
			l6.setVisible(true);
			t6.setText(Integer.toString(book.getISBN()));
			t6.setVisible(true);
			t6.setEditable(false);
			l7.setText("Categoria		");
			l7.setVisible(true);
			t7.setText("ATTENZIONE, NON LO SO!!!!");
			t7.setVisible(true);
			l8.setText("Descrizione	");
			l8.setVisible(true);
			t8.setText(book.getDescription());
			t8.setVisible(true);
			inviaButton.setDisable(false);
			inviaButton.setVisible(true);
		}
	}

	// elimina un libro
	@FXML
	void delLibroReleased(MouseEvent event) {
		String titolo = dialogReturnsTitolo();
		if (titolo == null)
			return;
		// cerca il titolo tra i titoli dedei libri
		ArrayList<Book> trovati = new ArrayList<Book>();
		for (Category c : client.getCategoryList()) {
			for (Book b : c.getBooks()) {
				if (b.getTitle().toLowerCase().contains(titolo)) {
					trovati.add(b);
				}
			}
		}
		if (trovati.isEmpty()) {
			nessunLibroTrovato();
		} else {
			Book book = dialogOptionListBook(trovati);
			if (book == null)
				return;
			if (sicuroElimina())
				delConfermata();
			// delete
			else
				System.out.println(book + " non eliminato");

		}
	}

	// aggiunge un nuovo libro
	@FXML
	void addCommessoReleased(MouseEvent event) {
		utente = false;
		mod = false;
		l1.setText("Titolo		");
		l1.setVisible(true);
		t1.setText("");
		t1.setVisible(true);
		t1.setEditable(true);
		l2.setText("Autore		");
		l2.setVisible(true);
		t2.setText("");
		t2.setVisible(true);
		l3.setText("Pagine		");
		l3.setVisible(true);
		t3.setText("");
		t3.setVisible(true);
		l4.setText("Editore		");
		l4.setVisible(true);
		t4.setText("");
		t4.setVisible(true);
		l5.setText("Lingua		");
		l5.setVisible(true);
		t5.setText("");
		t5.setVisible(true);
		l6.setText("ISBN			");
		l6.setVisible(true);
		t6.setText("");
		t6.setVisible(true);
		t6.setEditable(true);
		l7.setText("Categoria		");
		l7.setVisible(true);
		t7.setText("");
		t7.setVisible(true);
		l8.setText("Descrizione	");
		l8.setVisible(true);
		t8.setText("");
		t8.setVisible(true);
		inviaButton.setDisable(false);
		inviaButton.setVisible(true);
	}

	// modifica un libro
	@FXML
	void modCommessoReleased(MouseEvent event) {
		utente = false;
		mod = true;
		String titolo = dialogReturnsTitolo();
		if (titolo == null)
			return;
		// cerca il titolo tra i titoli dedei libri
		ArrayList<Book> trovati = new ArrayList<Book>();
		for (Category c : client.getCategoryList()) {
			for (Book b : c.getBooks()) {
				if (b.getTitle().toLowerCase().contains(titolo)) {
					trovati.add(b);
				}
			}
		}
		if (trovati.isEmpty()) {
			nessunLibroTrovato();
		} else {
			Book book = dialogOptionListBook(trovati);
			if (book == null)
				return;
			l1.setText("Titolo		");
			l1.setVisible(true);
			t1.setText(book.getTitle());
			t1.setVisible(true);
			l2.setText("Autore		");
			l2.setVisible(true);
			t2.setText(book.getAuthor());
			t2.setVisible(true);
			l3.setText("Pagine		");
			l3.setVisible(true);
			t3.setText(Integer.toString(book.getnPages()));
			t3.setVisible(true);
			l4.setText("Editore		");
			l4.setVisible(true);
			t4.setText(book.getPublisher());
			t4.setVisible(true);
			l5.setText("Lingua		");
			l5.setVisible(true);
			t5.setText(book.getLanguage());
			t5.setVisible(true);
			l6.setText("ISBN			");
			l6.setVisible(true);
			t6.setText(Integer.toString(book.getISBN()));
			t6.setVisible(true);
			t6.setEditable(false);
			l7.setText("Categoria		");
			l7.setVisible(true);
			t7.setText("ATTENZIONE, NON LO SO!!!!");
			t7.setVisible(true);
			l8.setText("Descrizione	");
			l8.setVisible(true);
			t8.setText(book.getDescription());
			t8.setVisible(true);
			inviaButton.setDisable(false);
			inviaButton.setVisible(true);
		}
	}

	// elimina un libro
	@FXML
	void delCommessoReleased(MouseEvent event) {
		String titolo = dialogReturnsTitolo();
		if (titolo == null)
			return;
		// cerca il titolo tra i titoli dedei libri
		ArrayList<Book> trovati = new ArrayList<Book>();
		for (Category c : client.getCategoryList()) {
			for (Book b : c.getBooks()) {
				if (b.getTitle().toLowerCase().contains(titolo)) {
					trovati.add(b);
				}
			}
		}
		if (trovati.isEmpty()) {
			nessunLibroTrovato();
		} else {
			Book book = dialogOptionListBook(trovati);
			if (book == null)
				return;
			if (sicuroElimina())
				delConfermata();
			// delete
			else
				System.out.println(book + " non eliminato");

		}
	}

	@FXML
	void inviaReleased(MouseEvent event) {
		// aggiungi utente
		if (utente && !mod) {
			String username = t1.getText();
			String password = t2.getText();
			String nome = t3.getText();
			String cognome = t4.getText();
			String email = t5.getText();

			if (username.equals("") || password.equals("") || nome.equals("") || cognome.equals("") || email.equals(""))
				campoNullo();
			else {
				User user = new User(username, password, nome, cognome, email);
				utenteInserito(user.toString());
				init();
				System.out.println(user);
			}
		}
		if (utente && mod) {
			String username = t1.getText();
			String password = t2.getText();
			String nome = t3.getText();
			String cognome = t4.getText();
			String email = t5.getText();

			if (username.equals("") || password.equals("") || nome.equals("") || cognome.equals("") || email.equals(""))
				campoNullo();
			else {
				// for ogni utente nel db, delete if username==user.username
				User user = new User(username, password, nome, cognome, email);
				utenteModificato(user.toString());
				init();
				System.out.println(user.toString());
			}
		}
		if (!utente && !mod) {
			String titolo = t1.getText();
			String autore = t2.getText();
			String pagine = t3.getText();
			String editore = t4.getText();
			String lingua = t5.getText();
			String ISBN = t6.getText();
			String categoria = t7.getText();
			String descrizione = t8.getText();
			if (titolo.equals("") || autore.equals("") || pagine.equals("") || editore.equals("") || lingua.equals("")
					|| ISBN.equals("") || categoria.equals("") || descrizione.equals(""))
				campoNullo();
			else if (!Pattern.matches("\\d*", pagine) || !Pattern.matches("\\d*", ISBN))
				noNumero();
			else {
				// for ogni utente nel db, delete if username==user.username
//			//	Book book = new Book(titolo, autore, Integer.parseInt(pagine), editore, lingua, descrizione,
//						Integer.parseInt(ISBN));
//				System.out.println(book);
//				libroInserito(book.toString());
//				init();
			}
		}
		if (!utente && mod) {
			String titolo = t1.getText();
			String autore = t2.getText();
			String pagine = t3.getText();
			String editore = t4.getText();
			String lingua = t5.getText();
			String ISBN = t6.getText();
			String categoria = t7.getText();
			String descrizione = t8.getText();
			if (titolo.equals("") || autore.equals("") || pagine.equals("") || editore.equals("") || lingua.equals("")
					|| ISBN.equals("") || categoria.equals("") || descrizione.equals(""))
				campoNullo();
			else if (!Pattern.matches("\\d*", pagine) || !Pattern.matches("\\d*", ISBN))
				noNumero();
			else {
//				Book book = new Book(titolo, autore, Integer.parseInt(pagine), editore, lingua, descrizione,
//						Integer.parseInt(ISBN));
//				System.out.println(book);
//				libroModificato(book.toString());
//				init();
			}
		}
	}

	// alert di errore
	private void campoNullo() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore nell'inserimento dei dati");
		alert.setHeaderText(null);
		alert.setContentText("Uno o più campi sono nulli");
		alert.showAndWait();
	}

	// alert di info
	private void delConfermata() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Eliminato");
		alert.setHeaderText(null);
		alert.setContentText("Eliminazione avvenuta con successo");
		alert.showAndWait();
	}

	// alert di info
	private void utenteInserito(String x) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Utente inserito");
		alert.setHeaderText(null);
		alert.setContentText("L'utente " + x + " e' stato aggiunto con successo!");
		alert.showAndWait();
	}

	// alert di info
	private void utenteModificato(String x) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Utente inserito");
		alert.setHeaderText(null);
		alert.setContentText("L'utente " + x + " e' stato modificato con successo!");
		alert.showAndWait();
	}

	// alert di info
	private void libroInserito(String x) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Libro inserito");
		alert.setHeaderText(null);
		alert.setContentText("Il libro " + x + " e' stato inserito con successo!");
		alert.showAndWait();
	}

	// alert di info
	private void libroModificato(String x) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Libro inserito");
		alert.setHeaderText(null);
		alert.setContentText("Il libro " + x + " e' stato modificato con successo!");
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

	// ritorna una stringa
	private String dialogReturnsTitolo() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Inserisci il titolo");
		dialog.setHeaderText(null);
		dialog.setContentText("Inserisci il titolo");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get().toLowerCase();
		else
			return null;
	}

	// ritorna un utente data una sottostringa del cognome
	private User dialogOptionListUser(ArrayList<User> trovati) {
		ArrayList<String> utenti = new ArrayList<String>();
		for (User u : trovati)
			utenti.add(u.toString());
		List<String> choices = utenti;

		ChoiceDialog<String> dialog = new ChoiceDialog<String>("---", choices);
		dialog.setTitle("Scegli un utente");
		dialog.setHeaderText(null);
		dialog.setContentText("Scegli l'utente");

		String temp;
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			temp = result.get();
			for (User u : trovati) {
				if (u.toString().equals(temp))
					return u;
			}
		}
		return null;
	}

	// ritorna un libro data la sottostringa del titolo
	private Book dialogOptionListBook(ArrayList<Book> trovati) {
		ArrayList<String> libri = new ArrayList<String>();
		for (Book b : trovati)
			libri.add(b.toString());
		List<String> choices = libri;
		ChoiceDialog<String> dialog = new ChoiceDialog<String>("---", choices);
		dialog.setTitle("Scegli un libro");
		dialog.setHeaderText(null);
		dialog.setContentText("Scegli il libro");

		String temp;
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			temp = result.get();
			for (Book b : trovati) {
				if (b.toString().equals(temp))
					return b;
			}
		}
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

	// alert di errore
	private void nessunLibroTrovato() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Nesun libro trovato");
		alert.setHeaderText(null);
		alert.setContentText("Nessun libro trovato");
		alert.showAndWait();
	}

	// alert di conferma
	private boolean sicuroElimina() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("ATTENZIONE!");
		alert.setHeaderText(null);
		alert.setContentText("Sei sicuro di voler procedere alla eliminazione?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
			return true;
		else
			return false;
	}

	// alert di errore
	private void noNumero() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore");
		alert.setHeaderText(null);
		alert.setContentText("Il campo \"Pagine\" e/o il campo \"ISBN\" devono essere dei numeri!");
		alert.showAndWait();
	}

	// passa alla restituisciScene
	@FXML
	void restituisciPressed(ActionEvent event) {
//		main.setRestituisciScene();
	}

	// passa a commessoScene
	@FXML
	void homePressed(ActionEvent event) {
		// main.setCommessoScene(new ArrayList<Book>());
	}

	// passa a utenteRegistratoProfiloScene
	@FXML
	void profiloPressed(ActionEvent event) {
		System.out.println("profilo");
	}

	// passa a publicScene
	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}

}
