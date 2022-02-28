package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.awt.*;

public class RenameDialogController extends Dialog{

    @FXML
    private TextField renameTextField;

    private String newName;

    public void rename(ActionEvent actionEvent) {
        if (renameTextField.getText().trim().isEmpty()) {
            showAlert("Введите новое имя");
            renameTextField.clear();
        } else {
            newName = renameTextField.getText();
            closeDialog(actionEvent);
        }
    }

    public String getNewName() {
        return newName;
    }

    public void cancel(ActionEvent actionEvent) {
        closeDialog(actionEvent);
    }
}
