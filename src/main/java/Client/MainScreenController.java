package Client;

import GeneralClasses.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenController extends Dialog  implements Initializable {

    @FXML
    private ListView<String> serverView;
    @FXML
    private ListView<String> sharingServerView;
    @FXML
    private TextField serverDirField;

    private List<String> shareListView;
    private Path clientDir;
    private FileChooser fileChooser = new FileChooser();
    private CloudMessage cloudMessage;

    public void upload(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        File selectedFile = fileChooser.showOpenDialog(null);
        App.sendMessage(new FileMessage(clientDir.resolve(selectedFile.toString())));
        processMessageList();
    }

    public void download(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        App.sendMessage(new FileRequest(fileName));
        processDownload();
    }

    public void delete(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        App.sendMessage(new Delete(fileName));
        processMessageList();
    }

    private void enterToServerDir(String currentItemSelected) throws IOException, ClassNotFoundException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        App.sendMessage(new ChangeInServerDir(fileName));
        processMessageList();
    }

    public void upServerDir(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        App.sendMessage(new ChangeOutServerDir());
        processMessageList();
    }

    public void createNewPath(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        NewPathDialogController dialogController = new NewPathDialogController();
        dialogController = (NewPathDialogController) openNewDialog("../NewPathDialog.fxml", dialogController);
        if (dialogController.getDirName() != null) {
            App.sendMessage(new CreateNewPathMessage(dialogController.getDirName()));
            processMessageList();
        }
    }

    public void rename(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        RenameDialogController dialogController = new RenameDialogController();
        dialogController = (RenameDialogController) openNewDialog("../renameDialog.fxml", dialogController);
        if (dialogController.getNewName() != null) {
            App.sendMessage(new RenameMessage(fileName, dialogController.getNewName()));
            processMessageList();
        }
    }

    public void setMyDir(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        String selectedPath = chooser.showDialog(null).getPath();
        System.out.println(selectedPath);
        if (selectedPath != null) {
            clientDir = Paths.get(selectedPath);
        }
    }

    public void share(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        SharingController sharingController = new SharingController();
        sharingController = (SharingController) openNewDialog("../sharingDialog.fxml",  sharingController);
        App.sendMessage(new ShareMessage(sharingController.getJsonMessage(), fileName));
        processMessageShare();
    }

    public void showMyFiles(ActionEvent actionEvent) {
        sharingServerView.setVisible(false);
        serverView.setVisible(true);
    }

    public void showSharingFiles(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        sharingServerView.setVisible(true);
        serverView.setVisible(false);
        App.sendMessage(new ShareFileRequest());
        processMessageShareList();
    }

    public Object openNewDialog(String url, Object dialog) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url));
        Parent parent = fxmlLoader.load();
        dialog = fxmlLoader.<Dialog>getController();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        return dialog;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            sharingServerView.setVisible(false);
            serverDirField.setEditable(false);
            clientDir = Paths.get(System.getProperty("user.home"));
            initMouseListeners();
            processMessageList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMouseListeners() {
        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String currentItemSelected = serverView.getSelectionModel()
                        .getSelectedItem();
                try {
                    enterToServerDir(currentItemSelected);
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public boolean processMessageList() throws IOException, ClassNotFoundException {
        do {
            cloudMessage = App.getMessage();
            if (cloudMessage.getType().equals(CommandType.LIST)) {
                updateView((ListMessage) cloudMessage);
                return true;
            } else if (cloudMessage.getType().equals(CommandType.ACCESS_DENIED_MESSAGE)) {
                return false;
            }
        } while (!cloudMessage.getType().equals(CommandType.LIST));
        return false;
    }

    public boolean processMessageShare() throws IOException, ClassNotFoundException {
        do {
            cloudMessage = App.getMessage();
            if (cloudMessage.getType().equals(CommandType.ACCESSED_MESSAGE)) {
                showAlert("Операция была успешно выполнена");
                return true;
            } else if (cloudMessage.getType().equals(CommandType.ACCESS_DENIED_MESSAGE)) {
                showAlert("Невозможно провести данную операцию. Проверьте правильность написания логина получателя");
                return false;
            }
        } while (true);
    }

    private void processDownload() throws IOException, ClassNotFoundException {
        DirectoryChooser chooser = new DirectoryChooser();
        String selectedPath = chooser.showDialog(null).getPath();
        if (selectedPath != null) {
            clientDir = Paths.get(selectedPath);
        }

        CloudMessage cloudMessage = (FileMessage) App.getMessage();
        Files.write(clientDir.resolve(((FileMessage) cloudMessage).getFileName()), ((FileMessage) cloudMessage).getBytes());
    }

    private void processDownloadSharingFiles() throws IOException, ClassNotFoundException {
        DirectoryChooser chooser = new DirectoryChooser();
        String selectedPath = chooser.showDialog(null).getPath();
        if (selectedPath != null) {
            clientDir = Paths.get(selectedPath);
        }
        CloudMessage cloudMessage = App.getMessage();
        Files.write(clientDir.resolve(((ShareFileMessage) cloudMessage).getFileName()), ((ShareFileMessage) cloudMessage).getBytes());
    }

    public void updateView(ListMessage message) throws IOException, ClassNotFoundException {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(message.getFiles());
        });
        CloudMessage newMessage = App.getMessage();
        updateServerDir((ViewServerDir) newMessage);
    }

    public void updateServerDir(ViewServerDir cloudMessage) {
        serverDirField.setText(cloudMessage.getServerDir().substring(26));
    }

    public void updateShareView(ShareListMessage message) {
        Platform.runLater(() -> {
            shareListView = message.getFiles();
            List<String> tempList = new ArrayList<>();
            for (String string: message.getFiles()) {
                tempList.add(string.substring(26));
            }
            sharingServerView.getItems().clear();
            sharingServerView.getItems().addAll(tempList);
//            sharingServerView.getItems().addAll(message.getFiles());
        });
    }

    public boolean processMessageShareList() throws IOException, ClassNotFoundException {
        do {
            cloudMessage = App.getMessage();
            if (cloudMessage.getType().equals(CommandType.SHARE_LIST)) {
                updateShareView((ShareListMessage) cloudMessage);
                return true;
            } else if (cloudMessage.getType().equals(CommandType.ACCESS_DENIED_MESSAGE)) {
                return false;
            }
        } while (!cloudMessage.getType().equals(CommandType.LIST));
        return false;
    }

    public void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText("ALERT!");
        alert.setContentText(text);
        alert.showAndWait();
    }

    public void downloadSharingFiles(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String fileName = sharingServerView.getSelectionModel().getSelectedItem();
        App.sendMessage(new ShareFileDownload(fileName));
        processDownloadSharingFiles();
    }

}
