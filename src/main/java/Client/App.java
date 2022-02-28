package Client;

import GeneralClasses.model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class App extends Application {

    private static Stage stage = new Stage();

    public static ListView<String> serverView = new ListView<>();

    private static ObjectDecoderInputStream is;
    private static ObjectEncoderOutputStream os;
    private Socket socket;

    private static CloudMessage message;
    private static CloudMessage authMessage; //Заглушка :/

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            connectServer();
            authMessage = App.getMessage();
            authMessage = App.getMessage();
            openAuthDialog();
            openMainScreen();
            stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAuthDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../EnterDialog.fxml"));
        Parent parent = fxmlLoader.load();
        AuthDialogController dialog = fxmlLoader.<AuthDialogController>getController();
        Scene scene = new Scene(parent);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void openMainScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../layout.fxml"));
        Parent parent = fxmlLoader.load();
        MainScreenController client = fxmlLoader.<MainScreenController>getController();
        Scene scene = new Scene(parent);
        Stage mainStage = new Stage();
        mainStage.initModality(Modality.APPLICATION_MODAL);
        mainStage.setScene(scene);
        mainStage.showAndWait();
    }

    public void connectServer() throws IOException {
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
//        try {
//            while (true) {
////                CloudMessage message = (CloudMessage) is.readObject();
//                CloudMessage message = getMessage();
//                log.info("received: {}", message);
////                processor.processMessage(message);
//                authMessage = message;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static CloudMessage getMessage() throws IOException, ClassNotFoundException {
        message = (CloudMessage) is.readObject();
        return message;
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
}