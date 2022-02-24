package Client;

import GeneralClasses.model.CommandType;
import GeneralClasses.model.LoginAndPasswordMessage;
import GeneralClasses.model.RegistryLoginAndPasswordMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.IOException;

public class RegistryDialogController extends Dialog {

    public JSONObject getJsonMessage() {
        return jsonMessage;
    }

    private JSONObject jsonMessage = new JSONObject();

    public TextField loginRegistryField;
    public TextField passwordRegistryField;
    public TextField emailField;

    private String loginRegistry;
    private String passwordRegistry;
    private String email;

    public void createLogin(ActionEvent actionEvent) throws IOException {
        loginRegistry = loginRegistryField.getText();
        passwordRegistry = passwordRegistryField.getText();
        email = emailField.getText();

        jsonMessage.put("login", loginRegistry);
        jsonMessage.put("password", passwordRegistry);
        jsonMessage.put("email", email);

        App.sendMessage(new RegistryLoginAndPasswordMessage(jsonMessage));

        do {
            switch (App.getAuthMessage().getType()) {
                case REGISTRY_SUCCESSFUL:
                    App.clearAuthMessage();
                    closeDialog(actionEvent);
                    break;
                case REGISTRY_DENIED:
                    showAlert("Данный логин уже занят");
                    App.clearAuthMessage();
                    break;
            }
        } while (!App.getAuthMessage().getType().equals(CommandType.REGISTRY_SUCCESSFUL));
    }

    @FXML
    void closeDialog(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
