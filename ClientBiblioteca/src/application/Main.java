package application;

import clientBiblioteca.Client;
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
	PublicController publicController;
	UtenteRegistratoLibriNoleggiatiController utenteRegistratoLibriNoleggiatiController;
	UtenteRegistratoRicercaLibriController utenteRegistratoRicercaLibriController;
	UtenteRegistratoProfiloController utenteRegistratoProfiloController;
	FXMLLoader loader;

	public void setPublicScene() {
		publicController.init();
		stage.setScene(publicScene);
	}

	public void setUtenteRegistratoLibriNoleggiatiScene() {
		utenteRegistratoLibriNoleggiatiController.init();
		stage.setScene(utenteRegistratoLibriNoleggiatiScene);
	}

	public void setUtenteRegistratoRicercaLibriScene() {
		utenteRegistratoRicercaLibriController.init();
		stage.setScene(utenteRegistratoRicercaLibriScene);
	}

	public void setUtenteRegistratoProfiloScene() {
		utenteRegistratoProfiloController.init();
		stage.setScene(utenteRegistratoProfiloScene);
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

			publicScene = new Scene(rootPublic, 800, 600);
			utenteRegistratoLibriNoleggiatiScene = new Scene(rootUtenteRegistratoLibriNoleggiati, 800, 600);
			utenteRegistratoRicercaLibriScene = new Scene(rootUtenteRegistratoRicercaLibri, 800, 600);
			utenteRegistratoProfiloScene = new Scene(rootUtenteRegistratoProfilo, 800, 600);

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
