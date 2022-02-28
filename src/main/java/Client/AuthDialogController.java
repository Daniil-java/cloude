package Client;

import GeneralClasses.Processors.CloudMessageProcessor;
import GeneralClasses.model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class AuthDialogController extends Dialog {

    private JSONObject jsonMessage = new JSONObject();

    @FXML
    private TextField loginField;

    @FXML
    private TextField passField;

    private String login;
    private String password;

    public JSONObject getJsonMessage() {
        return jsonMessage;
    }


    public void authButton(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        login = loginField.getText();
        password = passField.getText();

        jsonMessage.put("login", login);
        jsonMessage.put("password", password);

        App.sendMessage(new LoginAndPasswordMessage(jsonMessage));

        do {
            switch (App.getAuthMessage().getType()) {
                case ACCESSED_MESSAGE:
                    closeDialog(actionEvent);
                    break;
                case ACCESS_DENIED_MESSAGE:
                    showAlert("Проверьте логин/пароль");
                    App.clearAuthMessage();
                    break;
            }
        } while (!App.getAuthMessage().getType().equals(CommandType.ACCESSED_MESSAGE));

    }

    public void registryButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../RegistryDialog.fxml"));
        Parent parent = fxmlLoader.load();
        RegistryDialogController dialogController = fxmlLoader.<RegistryDialogController>getController();

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void closeDialog(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText("ALERT!");
        alert.setContentText(text);

        alert.showAndWait();
    }
}
