package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class NewPathDialogController extends Dialog{
    @FXML
    private TextField pathTextField;
    private String dirName;
    public String getDirName() {
        return dirName;
    }

    @FXML
    void createNewPathInDialog(ActionEvent actionEvent) {
        if (pathTextField.getText().trim().isEmpty()) {
            showAlert();
            pathTextField.clear();
        } else {
            dirName = pathTextField.getText();
            cancelCreatingNewPath(actionEvent);
        }
    }

    @FXML
    void cancelCreatingNewPath(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New path creating");
        alert.setHeaderText("ALERT!");
        alert.setContentText("Пожалуйста назовите новую папку");
        alert.showAndWait();
    }
}
