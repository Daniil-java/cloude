package GeneralClasses.Processors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import Client.Dialog;
import GeneralClasses.model.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

public class CloudMessageProcessor {

//    private Path clientDir;
//    public ListView<String> clientView;
    public ListView<String> serverView;

//    public CloudMessageProcessor(Path clientDir,
//                                 ListView<String> clientView,
//                                 ListView<String> serverView) {
//        this.clientDir = clientDir;
//        this.clientView = clientView;
//        this.serverView = serverView;
//    }

    public CloudMessageProcessor(ListView<String> serverView) {
        this.serverView = serverView;
    }

    public void processMessage(CloudMessage message) throws IOException {
        switch (message.getType()) {
            case LIST:
                processMessage((ListMessage) message);
                break;
            case FILE:
                processMessage((FileMessage) message);
                break;
//            case ACCESS_DENIED_MESSAGE:
//                processMessage((AccessDeniedMessage) message);
//                showAlert("Проверьте логин/пароль");
//                break;
//            case ACCESSED_MESSAGE:
//
//                break;
        }
    }

    public void processMessage(AccessDeniedMessage message) {
//        showAlert("Проверьте логин/пароль");
//        Platform.runLater(this::showAlert);
    }


//    public void processMessage(FileMessage message) throws IOException {
//        Files.write(clientDir.resolve(message.getFileName()), message.getBytes());
//        Platform.runLater(this::updateClientView);
//    }

    public void processMessage(ListMessage message) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(message.getFiles());
        });
    }

//    public void showAlert(String text) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("ERROR");
//        alert.setHeaderText("ALERT!");
////        alert.setContentText("Проверьте логин/пароль");
//        alert.setContentText(text);
//        alert.showAndWait();
//    }

//    private void updateClientView() {
//        try {
//            clientView.getItems().clear();
//            Files.list(clientDir)
//                    .map(p -> p.getFileName().toString())
//                    .forEach(f -> clientView.getItems().add(f));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
