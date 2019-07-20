package application;

import java.util.ArrayList;

import clientBiblioteca.Book;
import clientBiblioteca.Client;
import clientBiblioteca.Customer;
import clientBiblioteca.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

	Client client = new Client();
	Stage stage;
	Scene publicScene;
	Scene utenteRegistratoLibriNoleggiatiScene;
	Scene utenteRegistratoRicercaLibriScene;
	Scene utenteRegistratoProfiloScene;
	Scene commessoScene;
	Scene commessoCarrelloScene;
	Scene amministrazioneScene;
	Scene restituisciScene;
	PublicController publicController;
	UtenteRegistratoLibriNoleggiatiController utenteRegistratoLibriNoleggiatiController;
	UtenteRegistratoRicercaLibriController utenteRegistratoRicercaLibriController;
	UtenteRegistratoProfiloController utenteRegistratoProfiloController;
	CommessoController commessoController;
	CommessoCarrelloController commessoCarrelloController;
	AmministrazioneController amministrazioneController;
	RestituisciController restituisciController;
	FXMLLoader loader;

	public void setPublicScene() {
		publicController.init();
		stage.setScene(publicScene);
	}

	public void setUtenteRegistratoLibriNoleggiatiScene(Customer customer) {
		System.out.println(customer.getUsername());
		utenteRegistratoLibriNoleggiatiController.init(customer);
		stage.setScene(utenteRegistratoLibriNoleggiatiScene);
	}

	public void setUtenteRegistratoRicercaLibriScene(Customer customer) {
		utenteRegistratoRicercaLibriController.init(customer);
		stage.setScene(utenteRegistratoRicercaLibriScene);
	}

	public void setUtenteRegistratoProfiloScene(Customer c) {
		utenteRegistratoProfiloController.init(c);
		stage.setScene(utenteRegistratoProfiloScene);
	}

	public void setCommessoScene(ArrayList<Book> carrello, User user) {
		commessoController.init(carrello);
		stage.setScene(commessoScene);
	}

	public void setCommessoCarrelloScene(ArrayList<Book> carrello) {
		commessoCarrelloController.init(carrello);
		stage.setScene(commessoCarrelloScene);
	}

	public void setAmministrazioneScene() {
		amministrazioneController.init();
		stage.setScene(amministrazioneScene);
	}
	
	public void setRestituisciScene() {
		restituisciController.init();
		stage.setScene(restituisciScene);
	}
	
	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		try {
			loader = new FXMLLoader(getClass().getResource("Public.fxml"));
			AnchorPane rootPublic = loader.load();
			publicController = loader.getController();
			publicController.setMain(this);
			loader = new FXMLLoader(getClass().getResource("UtenteRegistratoLibriNoleggiati.fxml"));
			AnchorPane rootUtenteRegistratoLibriNoleggiati = loader.load();
			utenteRegistratoLibriNoleggiatiController = loader.getController();
			utenteRegistratoLibriNoleggiatiController.setMain(this);
			loader = new FXMLLoader(getClass().getResource("UtenteRegistratoRicercaLibri.fxml"));
			AnchorPane rootUtenteRegistratoRicercaLibri = loader.load();
			utenteRegistratoRicercaLibriController = loader.getController();
			utenteRegistratoRicercaLibriController.setMain(this);
			loader = new FXMLLoader(getClass().getResource("UtenteRegistratoProfilo.fxml"));
			AnchorPane rootUtenteRegistratoProfilo = loader.load();
			utenteRegistratoProfiloController = loader.getController();
			utenteRegistratoProfiloController.setMain(this);
			loader = new FXMLLoader(getClass().getResource("Commesso.fxml"));
			AnchorPane rootCommesso = loader.load();
			commessoController = loader.getController();
			commessoController.setMain(this);
			loader = new FXMLLoader(getClass().getResource("CommessoCarrello.fxml"));
			AnchorPane rootCommessoCarrello = loader.load();
			commessoCarrelloController = loader.getController();
			commessoCarrelloController.setMain(this);
			loader = new FXMLLoader(getClass().getResource("Amministrazione.fxml"));
			AnchorPane rootAmministrazione = loader.load();
			amministrazioneController = loader.getController();
			amministrazioneController.setMain(this);
			loader = new FXMLLoader(getClass().getResource("Restituisci.fxml"));
			AnchorPane rootRestituisci = loader.load();
			restituisciController = loader.getController();
			restituisciController.setMain(this);

			publicScene = new Scene(rootPublic, 800, 600);
			utenteRegistratoLibriNoleggiatiScene = new Scene(rootUtenteRegistratoLibriNoleggiati, 800, 600);
			utenteRegistratoRicercaLibriScene = new Scene(rootUtenteRegistratoRicercaLibri, 800, 600);
			utenteRegistratoProfiloScene = new Scene(rootUtenteRegistratoProfilo, 800, 600);
			commessoScene = new Scene(rootCommesso, 800, 600);
			commessoCarrelloScene = new Scene(rootCommessoCarrello, 800, 600);
			amministrazioneScene = new Scene(rootAmministrazione, 800, 600);
			restituisciScene = new Scene(rootRestituisci, 800, 600);

			setPublicScene();
			primaryStage = stage;
			primaryStage.setMinWidth(600);
			primaryStage.setMinHeight(400);
			primaryStage.setWidth(800);
			primaryStage.setHeight(600);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
