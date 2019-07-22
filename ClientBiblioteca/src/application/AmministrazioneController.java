package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
	private TextField t9;

	@FXML
	private TextField t10;

	@FXML
	private TextField t11;

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
	private Label l9;

	@FXML
	private Label l10;

	@FXML
	private Label l11;

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

	boolean mod;

	private Main main;

	private Client client;

	private User user;

	private enum operazione {
		ADD_UTENTE, MOD_UTENTE, DEL_UTENTE, ADD_LIBRO, MOD_LIBRO, DEL_LIBRO, ADD_COMMESSO, MOD_COMMESSO, DEL_COMMESSO
	}

	private operazione op;

	private Gson gson;

	public void setMain(Main m) {
		this.main = m;
		this.client = m.client;
		gson = new GsonBuilder().create();
	}

	public void setUser(User user) {
		this.user = user;
		init();
	}

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
		l9.setVisible(false);
		t9.setVisible(false);
		l10.setVisible(false);
		t10.setVisible(false);
		l11.setVisible(false);
		t11.setVisible(false);
		inviaButton.setVisible(false);
		inviaButton.setDisable(true);
	}

	@FXML
	void addUtenteReleased(MouseEvent event) {
		op = operazione.ADD_UTENTE;
		init();
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
		inviaButton.setDisable(false);
		inviaButton.setVisible(true);
	}

	@FXML
	void modUtenteReleased(MouseEvent event) {
		op = operazione.MOD_UTENTE;
		String cognome = dialogReturnsCognome();
		if (cognome == null)
			return;
		ArrayList<Customer> trovati = new ArrayList<Customer>();
		for (Customer c : client.getCustomersList())
			if (c.getSurname().toLowerCase().contains(cognome))
				trovati.add(c);
		if (trovati.isEmpty()) {
			alertErrore("Nessun utente trovato");
		} else {
			Customer customer = dialogOptionListCustomer(trovati);
			if (customer == null)
				return;
			init();
			l1.setText("Username	");
			l1.setVisible(true);
			t1.setText(customer.getUsername());
			t1.setEditable(false);
			t1.setVisible(true);
			l2.setText("Password		");
			l2.setVisible(true);
			t2.setText(customer.getPassword());
			t2.setVisible(true);
			l3.setText("Nome		");
			l3.setVisible(true);
			t3.setText(customer.getName());
			t3.setVisible(true);
			l4.setText("Cognome		");
			l4.setVisible(true);
			t4.setText(customer.getSurname());
			t4.setVisible(true);
			l5.setText("E-mail		");
			l5.setVisible(true);
			t5.setText(customer.getE_mail());
			t5.setVisible(true);
			inviaButton.setDisable(false);
			inviaButton.setVisible(true);
		}
	}

	@FXML
	void delUtenteReleased(MouseEvent event) {
		op = operazione.DEL_UTENTE;
		String cognome = dialogReturnsCognome();
		if (cognome == null)
			return;
		ArrayList<Customer> trovati = new ArrayList<Customer>();
		for (Customer c : client.getCustomersList())
			if (c.getSurname().toLowerCase().contains(cognome))
				trovati.add(c);
		if (trovati.isEmpty()) {
			alertErrore("Nessun utente trovato");
		} else {
			Customer customer = dialogOptionListCustomer(trovati);
			if (customer == null)
				return;
			init();
			if (sicuroElimina()) {
				String x = client.delCustomer(customer.getUsername());
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					alertSuccesso();
					client.refreshDB();
				}
			}
		}
	}

	@FXML
	void addLibroReleased(MouseEvent event) {
		op = operazione.ADD_LIBRO;
		init();
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
		l9.setText("Prestabile per (giorni)	");
		l9.setVisible(true);
		t9.setText("");
		t9.setVisible(true);
		l10.setText("Incremento/giorno		");
		l10.setVisible(true);
		t10.setText("");
		t10.setVisible(true);
		l11.setText("Copertina (.jpg)		");
		l11.setVisible(true);
		t11.setText("");
		t11.setVisible(true);
		inviaButton.setDisable(false);
		inviaButton.setVisible(true);

		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Attenzione");
		alert.setHeaderText(null);
		alert.setContentText("Inserendo solo l'ISBN di un libro esistente, verra' aggiunta un'altra copia del libro");
		alert.showAndWait();
	}

	@FXML
	void modLibroReleased(MouseEvent event) {
		op = operazione.MOD_LIBRO;
		String titolo = dialogReturnsTitolo();
		if (titolo == null)
			return;
		ArrayList<Book> trovati = new ArrayList<Book>();
		for (Category c : client.getCategoryList()) {
			for (Book b : c.getBooks()) {
				if (b.getTitle().toLowerCase().contains(titolo)) {
					trovati.add(b);
				}
			}
		}
		if (trovati.isEmpty()) {
			alertErrore("Nessun libro trovato");
		} else {
			Book book = dialogOptionListBook(trovati);
			if (book == null)
				return;
			Category category = null;
			for (Category c : client.getCategoryList()) {
				for (Book b : c.getBooks()) {
					if (b.equals(book)) {
						category = c;
					}
				}
			}
			init();
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
			t7.setText(category.getCategoryType());
			t7.setVisible(true);
			l8.setText("Descrizione	");
			l8.setVisible(true);
			t8.setText(book.getDescription());
			t8.setVisible(true);
			l9.setText("Prestabile per (giorni)	");
			l9.setVisible(true);
			t9.setText(Integer.toString(book.getLendingPeriod()));
			t9.setVisible(true);
			l10.setText("Incremento/giorno		");
			l10.setVisible(true);
			t10.setText(Integer.toString(book.getFineIncrement()));
			t10.setVisible(true);
			l11.setText("Copertina (.jpg)		");
			l11.setVisible(true);
			t11.setText(book.getImage());
			t11.setVisible(true);
			inviaButton.setDisable(false);
			inviaButton.setVisible(true);
		}
	}

	@FXML
	void delLibroReleased(MouseEvent event) {
		op = operazione.DEL_LIBRO;
		String titolo = dialogReturnsTitolo();
		if (titolo == null)
			return;
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
			init();
			if (sicuroElimina()) {
				String x = client.delBook(book);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					alertSuccesso();
					client.refreshDB();
				}
			}
		}
	}

	@FXML
	void addCommessoReleased(MouseEvent event) {
		op = operazione.ADD_COMMESSO;
		init();
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
		inviaButton.setDisable(false);
		inviaButton.setVisible(true);
	}

	@FXML
	void modCommessoReleased(MouseEvent event) {
		op = operazione.MOD_COMMESSO;
		String cognome = dialogReturnsCognome();
		if (cognome == null)
			return;
		ArrayList<User> trovati = new ArrayList<User>();
		for (User c : client.getEmployeesList())
			if (c.getSurname().toLowerCase().contains(cognome) && !c.getUsername().equals(user.getUsername()))
				trovati.add(c);
		if (trovati.isEmpty()) {
			nessunUtenteTrovato();
		} else {
			User user = dialogOptionListUser(trovati);
			if (user == null)
				return;
			init();
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
			inviaButton.setDisable(false);
			inviaButton.setVisible(true);
		}
	}

	@FXML
	void delCommessoReleased(MouseEvent event) {
		op = operazione.DEL_COMMESSO;
		String cognome = dialogReturnsCognome();
		if (cognome == null)
			return;
		ArrayList<User> trovati = new ArrayList<User>();
		for (User c : client.getEmployeesList())
			if (c.getSurname().toLowerCase().contains(cognome) && !c.getUsername().equals(user.getUsername()))
				trovati.add(c);
		if (trovati.isEmpty()) {
			alertErrore("Nessun utente trovato");
		} else {
			User user = dialogOptionListUser(trovati);
			if (user == null)
				return;
			init();
			if (sicuroElimina()) {
				String x = client.delUser(user.getUsername());
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					alertSuccesso();
					client.refreshDB();
				}
			}
		}
	}

	@FXML
	void inviaReleased(MouseEvent event) {
		if (op == operazione.ADD_UTENTE) {
			String username = t1.getText();
			String password = t2.getText();
			String nome = t3.getText();
			String cognome = t4.getText();
			String email = t5.getText();

			for (Customer c : client.getCustomersList()) {
				if (username.equals(c.getUsername()))
					alertErrore("E' presente un altro utente con questo username, riprova con un altro");
			}
			for (User u : client.getEmployeesList()) {
				if (username.equals(u.getUsername()))
					alertErrore("E' presente un altro utente con questo username, riprova con un altro");
			}

			if (username.equals("") || password.equals("") || nome.equals("") || cognome.equals("") || email.equals(""))
				alertErrore("Uno o piu' campi sono nulli");
			// [a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]+
			else if (!Pattern.matches(
					"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
					email))
				alertErrore("E-mail non valida");
			else if (password.length() < 8 || password.length() > 16)
				alertErrore("La password deve essere lunga almeno 8 caratteri e massimo 16");
			else {
				Customer customer = new Customer(username, password, nome, cognome, email, "");
				String x = client.addCustomer(customer);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					utenteInserito(customer);
					client.refreshDB();
					init();
				}
			}
		}

		if (op == operazione.MOD_UTENTE) {
			String username = t1.getText();
			String password = t2.getText();
			String nome = t3.getText();
			String cognome = t4.getText();
			String email = t5.getText();

			if (username.equals("") || password.equals("") || nome.equals("") || cognome.equals("") || email.equals(""))
				alertErrore("Uno o piu' campi sono nulli");
			else if (!Pattern.matches(
					"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
					email))
				alertErrore("Uno o piu' campi sono nulli");
			else {
				Customer customer = new Customer(username, password, nome, cognome, email, "");
				String x = client.modCustomer(customer);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					utenteModificato(customer);
					client.refreshDB();
				}
				init();
			}
		}

		if (op == operazione.ADD_LIBRO) {
			for (Category c : client.getCategoryList()) {
				for (Book b : c.getBooks()) {
					if (t6.getText().equals(Integer.toString(b.getISBN()))) {
						t1.setText(b.getTitle());
						t2.setText(b.getAuthor());
						t3.setText(Integer.toString(b.getnPages()));
						t4.setText(b.getPublisher());
						t5.setText(b.getLanguage());
						t7.setText(c.getCategoryType());
						t8.setText(b.getDescription());
						t9.setText(Integer.toString(b.getLendingPeriod()));
						t10.setText(Integer.toString(b.getFineIncrement()));
						t11.setText(b.getImage());
						break;
					}
				}
			}

			String titolo = t1.getText();
			String autore = t2.getText();
			String pagine = t3.getText();
			String editore = t4.getText();
			String lingua = t5.getText();
			String ISBN = t6.getText();
			String categoria = t7.getText();
			String descrizione = t8.getText();
			String prestabile = t9.getText();
			String fineIncr = t10.getText();
			String immagine = t11.getText();

			boolean ok = false;
			for (String s : client.getImageReceiver().getImageNames()) {
				if (s.equals(immagine))
					ok = true;
			}

			if (titolo.equals("") || autore.equals("") || pagine.equals("") || editore.equals("") || lingua.equals("")
					|| ISBN.equals("") || categoria.equals("") || descrizione.equals("") || prestabile.equals("")
					|| fineIncr.equals("") || immagine.equals(""))
				alertErrore("Uno o piu' campi sono nulli");
			else if (!Pattern.matches("\\d*", pagine) || !Pattern.matches("\\d*", ISBN)
					|| !Pattern.matches("\\d*", prestabile) || !Pattern.matches("\\d*", fineIncr))
				alertErrore(
						"Il/I campo/i \"Pagine\"/\"ISBN\"/\"Prestabile per (giorni)\"/\"Incremento/giorno\" devono essere numeri");
			else if (!ok)
				alertErrore("Immagine " + immagine + " non trovata nel server");
			else {
				Category category = new Category(categoria);
				Book[] temp = new Book[1];
				temp[0] = new Book(0, titolo, autore, Integer.parseInt(pagine), editore, lingua, descrizione,
						Integer.parseInt(ISBN), 0, immagine, Integer.parseInt(prestabile), Integer.parseInt(fineIncr));
				category.setBooks(temp);
				String x = client.addCategory(category);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					libroInserito(temp[0]);
					client.refreshDB();
				}
			}
		}

		if (op == operazione.MOD_LIBRO) {
			String titolo = t1.getText();
			String autore = t2.getText();
			String pagine = t3.getText();
			String editore = t4.getText();
			String lingua = t5.getText();
			String ISBN = t6.getText();
			String categoria = t7.getText();
			String descrizione = t8.getText();
			String prestabile = t9.getText();
			String fineIncr = t10.getText();
			String immagine = t11.getText();
			boolean ok = false;
			for (String s : client.getImageReceiver().getImageNames()) {
				if (s.equals(immagine))
					ok = true;
			}
			if (titolo.equals("") || autore.equals("") || pagine.equals("") || editore.equals("") || lingua.equals("")
					|| ISBN.equals("") || categoria.equals("") || descrizione.equals("") || prestabile.equals("")
					|| fineIncr.equals("") || immagine.equals(""))
				alertErrore("Uno o piu' campi sono nulli");
			else if (!Pattern.matches("\\d*", pagine) || !Pattern.matches("\\d*", ISBN)
					|| !Pattern.matches("\\d*", prestabile) || !Pattern.matches("\\d*", fineIncr))
				alertErrore(
						"Il/I campo/i \"Pagine\"/\"ISBN\"/\"Prestabile per (giorni)\"/\"Incremento/giorno\" devono essere numeri");
			else if (!ok)
				alertErrore("Immagine " + immagine + " non trovata nel server");
			else {
				Category category = new Category(categoria);
				Book[] temp = new Book[1];
				temp[0] = new Book(0, titolo, autore, Integer.parseInt(pagine), editore, lingua, descrizione,
						Integer.parseInt(ISBN), 0, immagine, Integer.parseInt(prestabile), Integer.parseInt(fineIncr));
				category.setBooks(temp);
				String x = client.modCategory(category);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					libroModificato(temp[0]);
					client.refreshDB();
					init();
				}
			}
		}

		if (op == operazione.ADD_COMMESSO) {
			String username = t1.getText();
			String password = t2.getText();
			String nome = t3.getText();
			String cognome = t4.getText();
			String email = t5.getText();

			for (Customer c : client.getCustomersList()) {
				if (username.equals(c.getUsername()))
					alertErrore("E' presente un altro utente con questo username, riprova con un altro");
			}
			for (User u : client.getEmployeesList()) {
				if (username.equals(u.getUsername()))
					alertErrore("E' presente un altro utente con questo username, riprova con un altro");
			}

			if (username.equals("") || password.equals("") || nome.equals("") || cognome.equals("") || email.equals(""))
				alertErrore("Uno o piu' campi sono nulli");
			else if (!Pattern.matches(
					"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
					email))
				alertErrore("E-mail non valida");
			else if (password.length() < 8 || password.length() > 16)
				alertErrore("La password deve essere lunga almeno 8 caratteri e massimo 16");
			else {
				User user = new User(username, password, nome, cognome, email);
				String x = client.addUser(user);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					utenteInserito(user);
					client.refreshDB();
				}
				init();
			}
		}

		if (op == operazione.MOD_COMMESSO) {
			String username = t1.getText();
			String password = t2.getText();
			String nome = t3.getText();
			String cognome = t4.getText();
			String email = t5.getText();

			if (username.equals("") || password.equals("") || nome.equals("") || cognome.equals("") || email.equals(""))
				alertErrore("Uno o piu' campi sono nulli");
			else if (!Pattern.matches(
					"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
					email))
				alertErrore("E-mail non valida");
			else if (password.length() < 8 || password.length() > 16)
				alertErrore("La password deve essere lunga almeno 8 caratteri e massimo 16");
			else {
				User user = new User(username, password, nome, cognome, email);
				String x = client.modUser(user);
				JsonObject jsonObject = new JsonParser().parse(x).getAsJsonObject();
				if (jsonObject.get("object").toString().equals("\"FAILURE\"")) {
					Envelope<String> reportEnvelope = gson.fromJson(x, new TypeToken<Envelope<String>>() {
					}.getType());
					alertErrore(reportEnvelope.getContent());
				} else {
					utenteModificato(user);
					client.refreshDB();
				}
				init();
			}
		}
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

	private void utenteInserito(User x) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Utente inserito");
		alert.setHeaderText(null);
		alert.setContentText("L'utente " + x.toString() + " e' stato aggiunto con successo!");
		alert.showAndWait();
	}

	private void utenteModificato(User user) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Utente modificato");
		alert.setHeaderText(null);
		alert.setContentText("L'utente " + user.toString() + " e' stato modificato con successo!");
		alert.showAndWait();
	}

	private void libroInserito(Book x) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Libro inserito");
		alert.setHeaderText(null);
		alert.setContentText("Il libro " + x.toString() + " e' stato inserito con successo!");
		alert.showAndWait();
	}

	private void libroModificato(Book x) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Libro inserito");
		alert.setHeaderText(null);
		alert.setContentText("Il libro " + x.toString() + " e' stato modificato con successo!");
		alert.showAndWait();
	}

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

	private User dialogOptionListUser(ArrayList<User> trovati) {
		ArrayList<String> utenti = new ArrayList<String>();
		for (User c : trovati)
			utenti.add(c.toString());
		List<String> choices = utenti;

		ChoiceDialog<String> dialog = new ChoiceDialog<String>("---", choices);
		dialog.setTitle("Scegli un utente");
		dialog.setHeaderText(null);
		dialog.setContentText("Scegli l'utente");

		String temp;
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			temp = result.get();
			for (User c : trovati) {
				if (c.toString().equals(temp))
					return c;
			}
		}
		return null;
	}

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

	private void nessunLibroTrovato() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Nesun libro trovato");
		alert.setHeaderText(null);
		alert.setContentText("Nessun libro trovato");
		alert.showAndWait();
	}

	private boolean sicuroElimina() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Attenzione!");
		alert.setHeaderText(null);
		alert.setContentText("Sei sicuro di voler procedere alla eliminazione?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
			return true;
		else
			return false;
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

	private void nessunUtenteTrovato() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Nesun utente trovato");
		alert.setHeaderText(null);
		alert.setContentText("Nessun utente trovato");
		alert.showAndWait();
	}

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
	void homePressed(ActionEvent event) {
		main.setCommessoScene(new ArrayList<Book>(), user);
	}

	@FXML
	void profiloPressed(ActionEvent event) {
		main.setCommessoProfiloScene(user);
	}

	@FXML
	void esciPressed(ActionEvent event) {
		main.setPublicScene();
	}

}
