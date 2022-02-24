package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

public class Dialog {

    public JSONObject getJsonMessage() {
        return jsonMessage;
    }

    private JSONObject jsonMessage = new JSONObject();

    @FXML
    void closeDialog(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void showAlert(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText("ALERT!");
        alert.setContentText(error);
        alert.showAndWait();
    }
}
