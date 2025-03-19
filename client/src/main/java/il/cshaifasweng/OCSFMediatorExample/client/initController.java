package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.client;

public class initController {

    @FXML
    private TextField IP_Label;

    @FXML
    private Button btn;

    @FXML
    private TextField port_Label;

    @FXML
    void btn_connect(ActionEvent event) throws IOException {
        SimpleClient.PORT = Integer.parseInt(port_Label.getText());
        SimpleClient.IP = IP_Label.getText();
        client = SimpleClient.getClient();
        try {
            client.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("startPageView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 640, 480);
        App.primaryStage.setScene(scene);
        App.primaryStage.show();
    }

}
