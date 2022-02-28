package Client;

import GeneralClasses.Processors.CloudMessageProcessor;
import GeneralClasses.model.CloudMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
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
public class Model implements Initializable {

    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private CloudMessageProcessor processor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
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

    public ObjectDecoderInputStream getIs() {
        return is;
    }

    public ObjectEncoderOutputStream getOs() {
        return os;
    }
}
