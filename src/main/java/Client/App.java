package Client;

import GeneralClasses.Processors.CloudMessageProcessor;
import GeneralClasses.model.CloudMessage;
import GeneralClasses.model.LoginAndPasswordMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

@Slf4j
public class App extends Application {

    public ListView<String> serverView = new ListView<>();

    private static ObjectDecoderInputStream is;
    private static ObjectEncoderOutputStream os;
    private CloudMessageProcessor processor;
    private Socket socket;

    private static CloudMessage authMessage;

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Parent parent = FXMLLoader.load(getClass().getResource("../layout.fxml"));
////        Parent parent = FXMLLoader.load(getClass().getResource("../EnterDialog.fxml"));
//        primaryStage.setScene(new Scene(parent));
//        primaryStage.show();
//    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            connectServer();
            openAuthDialog();
            openMainScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAuthDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../EnterDialog.fxml"));
        Parent parent = fxmlLoader.load();
        AuthDialogController dialog = fxmlLoader.<AuthDialogController>getController();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    public void openMainScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../layout.fxml"));
        Parent parent = fxmlLoader.load();
        Client client = fxmlLoader.<Client>getController();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    public void connectServer() throws IOException {
        processor = new CloudMessageProcessor(serverView);
        socket = new Socket("localhost", 8189);
        System.out.println("Network created...");
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());
        Thread readThread = new Thread(this::readLoop);
        readThread.setDaemon(true);
        readThread.start();
    }

    public static void sendMessage(CloudMessage cloudMessage) throws IOException {
        os.writeObject(cloudMessage);
    }

    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}", message);
                processor.processMessage(message);
                authMessage = message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CloudMessage getAuthMessage() {
        return authMessage;
    }

    public static void clearAuthMessage() {
        App.authMessage = null;
    }
}