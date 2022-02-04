package Client;

import GeneralClasses.Sender;
import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

public class Client implements Initializable {

    private static final int SIZE = 256;

    private Path clientDir;
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientViewDir;
    public TextField serverViewDir;
    private DataInputStream is;
    private DataOutputStream os;
    private byte[] buf;

    private void readLoop() {
        try {
            while (true) {
                String command = is.readUTF();
                clientView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            String currentItemSelected = clientView.getSelectionModel()
                                    .getSelectedItem();
                            clientDir = Paths.get(clientDir.toString() + "/" + currentItemSelected);
                            updateClientView();
                        }
                    }
                });
                serverView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            String currentItemSelected = serverView.getSelectionModel()
                                    .getSelectedItem();
                            try {
                                enterToServerDir(currentItemSelected);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                System.out.println("received: " + command);// wait message
                if (command.equals("#list#")) {
                    updateServerDir();
                    Platform.runLater(() -> serverView.getItems().clear());
                    int filesCount = is.readInt();
                    for (int i = 0; i < filesCount; i++) {
                        String fileName = is.readUTF();
                        Platform.runLater(() -> serverView.getItems().add(fileName));
                    }
                } else if (command.equals("#file#")) {
                    Sender.getFile(is, clientDir, SIZE, buf);
                    Platform.runLater(this::updateClientView);
                } else if (command.equals("#update_dir#")) {
                    System.out.println("UPDATE");
                    String serverDir = is.readUTF();
                    serverViewDir.setText(serverDir);
                }
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
            buf = new byte[SIZE];
            clientDir = Paths.get(System.getProperty("user.home"));
            updateClientView();
            Socket socket = new Socket("localhost", 8189);
            System.out.println("Network created...");
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        os.writeUTF("#file#");
        os.writeUTF(fileName);
        Path file = clientDir.resolve(fileName);
        long size = Files.size(file);
        byte[] bytes = Files.readAllBytes(file);
        os.writeLong(size);
        os.write(bytes);
        os.flush();
//        Sender.sendFile(fileName, os, clientDir);
    }


    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeUTF("#get_file#");
        os.writeUTF(fileName);
        os.flush();
    }

    public void upDir(ActionEvent actionEvent) {
        if (clientDir.getParent() != null) {
            clientDir = clientDir.getParent();
        }
        updateClientView();
    }

    public void upServerDir(ActionEvent actionEvent) throws IOException {
        System.out.println("#up_dir#");
        os.writeUTF("#up_dir#");
        os.flush();
    }

    public void updateServerDir() throws IOException {
        os.writeUTF("#get_dir#");
        os.flush();
    }

    public void enterToServerDir(String dir) throws IOException {
        os.writeUTF("#to_dir#");
        os.writeUTF(dir);
        os.flush();
    }
}
