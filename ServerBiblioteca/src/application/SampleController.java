package application;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class SampleController {

    @FXML
    private TextArea logTextArea;

    @FXML
    private Button terminaServerButton;

    @FXML
    private Button avviaServerButton;
    
    private ServerConnection serverConnection;
    
    @FXML
    void avviaServer(ActionEvent event) {
    	try {
    		serverConnection = new ServerConnection(8000);
			avviaServerButton.setDisable(true);
			terminaServerButton.setDisable(false);
			logTextArea.appendText("Server avviato.\n");
		} catch (IOException | SQLException | ParseException e) {
			logTextArea.appendText("ERRORE: IMPOSSIBILE AVVIARE IL SERVER: " + e.getMessage() + "\n");
		}
    }

    @FXML
    void terminaServer(ActionEvent event) {
    	try {
			serverConnection.close();
			avviaServerButton.setDisable(false);
			terminaServerButton.setDisable(true);
			logTextArea.appendText("Server terminato.\n");
		} catch (IOException | SQLException e) {
			logTextArea.appendText("ERRORE: IMPOSSIBILE TERMINARE IL SERVER\n");
		}
    }

}