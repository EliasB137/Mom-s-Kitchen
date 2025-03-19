package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;

public class momStartController {

    @FXML
    private Button View_Menu;

    @FXML
    void View_Menu_Click(ActionEvent event) throws IOException {// Check connection before sending request
        System.out.println("View Menu button clicked! Sending request...");
        SimpleClient.getClient().sendToServer("getDishes");

        // Load the menu screen (primary.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 640, 480);
        App.primaryStage.setScene(scene);
        App.primaryStage.show();
    }





}
