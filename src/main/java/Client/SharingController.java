package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.awt.*;

public class SharingController extends Dialog{

    private JSONObject jsonMessage = new JSONObject();
    @FXML
    public TextField userLoginField;
    private String login;

    public void share(ActionEvent actionEvent) {
        login = userLoginField.getText();
        jsonMessage.put("login", login);
        cancel(actionEvent);
    }


    public void cancel(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public JSONObject getJsonMessage() {
        return jsonMessage;
    }
}
