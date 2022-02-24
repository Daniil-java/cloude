package Client;

import GeneralClasses.Processors.CloudMessageProcessor;
import GeneralClasses.model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

@Slf4j
public class Client {

    @FXML
    private AnchorPane mainScreen;

    private Path clientDir;
//    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientViewDir;
    public TextField serverViewDir;

//    private ObjectDecoderInputStream is;
//    private ObjectEncoderOutputStream os;
//    private CloudMessageProcessor processor;

    FileChooser fileChooser = new FileChooser();

//    private void readLoop() {
//        try {
//            while (true) {
//                CloudMessage message = (CloudMessage) is.readObject();
//                log.info("received: {}", message);
//                processor.processMessage(message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        try {
//            clientDir = Paths.get(System.getProperty("user.home"));
//            updateClientView();
//            initMouseListeners();
//            processor = new CloudMessageProcessor(clientDir, clientView, serverView);
//            processor = new CloudMessageProcessor(serverView);
//            Socket socket = new Socket("localhost", 8189);
//            System.out.println("Network created...");
//            os = new ObjectEncoderOutputStream(socket.getOutputStream());
//            is = new ObjectDecoderInputStream(socket.getInputStream());
//            Thread readThread = new Thread(this::readLoop);
//            readThread.setDaemon(true);
//            readThread.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void initMouseListeners() {
        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String currentItemSelected = serverView.getSelectionModel()
                        .getSelectedItem();
                try {
                    enterToServerDir(currentItemSelected);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        File selectedFile = fileChooser.showOpenDialog(null);
//        os.writeObject(new FileMessage(clientDir.resolve(selectedFile.toString())));
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
//        os.writeObject(new FileRequest(fileName));
    }

    public void delete(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
//        os.writeObject(new Delete(fileName));
    }

    private void enterToServerDir(String currentItemSelected) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
//        os.writeObject(new ChangeInServerDir(fileName));
    }

    public void upServerDir(ActionEvent actionEvent) throws IOException {
//        os.writeObject(new ChangeOutServerDir());
    }
//
//    public void viewServerDir(ActionEvent actionEvent) throws IOException {
//        os.writeObject(new ChangeServerDir());
//    }

    public void createNewPath(ActionEvent actionEvent) throws IOException {
        NewPathDialogController dialogController = new NewPathDialogController();
        dialogController = (NewPathDialogController) openNewDialog("../NewPathDialog.fxml", dialogController);

        if (dialogController.getDirName() != null) {
//            os.writeObject(new CreateNewPathMessage(dialogController.getDirName()));
        }
    }

//    public void tryToAuthUser() throws IOException, ClassNotFoundException {
//        AuthDialogController dialogController = new AuthDialogController();
//        dialogController = (AuthDialogController) openNewDialog("../EnterDialog.fxml", dialogController);
//
//        os.writeObject(new LoginAndPasswordMessage(dialogController.getJsonMessage()));
//
//    }

    public void toRegistry() throws IOException {
        RegistryDialogController dialogController = new RegistryDialogController();
        dialogController = (RegistryDialogController) openNewDialog( "../RegistryDialog.fxml", dialogController);

//        os.writeObject(new RegistryLoginAndPasswordMessage(dialogController.getJsonMessage()));
        System.out.println(dialogController.getJsonMessage().toString());
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
        stage.show();
        return dialog;
    }

//    private void updateClientView() { //Находим клиентские файлы
//        try {
//            clientView.getItems().clear();
//            clientViewDir.setText(clientDir.toString());
//            Files.list(clientDir)
//                    .map(p -> p.getFileName().toString())
//                    .forEach(f -> clientView.getItems().add(f));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}

