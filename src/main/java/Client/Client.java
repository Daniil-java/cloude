package Client;

import GeneralClasses.Processors.CloudMessageProcessor;
import GeneralClasses.Sender;
import GeneralClasses.model.CloudMessage;
import GeneralClasses.model.FileMessage;
import GeneralClasses.model.FileRequest;
import com.sun.javafx.scene.control.skin.LabeledText;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

@Slf4j
public class Client implements Initializable {

    private Path clientDir;
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientViewDir;
    public TextField serverViewDir;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private CloudMessageProcessor processor;

    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}", message);
                processor.processMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClientView() { //Находим клиентские файлы
        try {
            clientView.getItems().clear();
            clientViewDir.setText(clientDir.toString());
            Files.list(clientDir)
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientDir = Paths.get(System.getProperty("user.home"));
            updateClientView();
            initMouseListeners();
            processor = new CloudMessageProcessor(clientDir, clientView, serverView);
            Socket socket = new Socket("localhost", 8189);
            System.out.println("Network created...");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMouseListeners() {

//        clientView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.getClickCount() == 2) {
//                    String currentItemSelected = clientView.getSelectionModel()
//                            .getSelectedItem();
//                    clientDir = Paths.get(clientDir.toString() + "/" + currentItemSelected);
//                    updateClientView();
//                }
//            }
//        });
//        serverView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.getClickCount() == 2) {
//                    String currentItemSelected = serverView.getSelectionModel()
//                            .getSelectedItem();
//                    try {
//                        enterToServerDir(currentItemSelected);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Path current = clientDir.resolve(getItem());
                if (Files.isDirectory(current)) {
                    clientDir = current;
                    Platform.runLater(this::updateClientView);
                }
            }
        });

        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // todo Home Work
            }
        });

    }

    private String getItem() {
        return clientView.getSelectionModel().getSelectedItem();
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(clientDir.resolve(fileName)));
    }


    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(fileName));
    }

//    public void upDir(ActionEvent actionEvent) {
//        if (clientDir.getParent() != null) {
//            clientDir = clientDir.getParent();
//        }
//        updateClientView();
//    }
//
//    public void upServerDir(ActionEvent actionEvent) throws IOException {
//        System.out.println("#up_dir#");
//        os.writeUTF("#up_dir#");
//        os.flush();
//    }
//
//    public void updateServerDir() throws IOException {
//        os.writeUTF("#get_dir#");
//        os.flush();
//    }
//
//    public void enterToServerDir(String dir) throws IOException {
//        os.writeUTF("#to_dir#");
//        os.writeUTF(dir);
//        os.flush();
//    }
}

