package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

public class adminSignInController {

    @FXML
    private Button backButton;

    @FXML
    private Label confirmationLabel;

    @FXML
    private Button logInButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    void backAction(ActionEvent event) {
        onClose();
        SimpleClient.getClient().navigateTo("startPageView");
    }

    @FXML
    void logInAction(ActionEvent event) {
        // Clear any previous confirmation messages
        confirmationLabel.setText("");

        // Check if text fields are filled
        String username = usernameTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            // Display error message if fields are empty
            confirmationLabel.setText("Please fill in both username and password fields.");
            return;
        }
        try {
            // Create a message to send to the server
            responseDTO message = new responseDTO("logInRequest",new Object[]{username, password});

            // Send the message to the server
            SimpleClient.getClient().sendToServer(message);

            // Optional: Show a message that login attempt is being processed
            confirmationLabel.setText("Processing login...");

        } catch (Exception e) {
            // Handle any exceptions using Platform.runLater to update UI from any thread
            javafx.application.Platform.runLater(() -> {
                confirmationLabel.setText("Error: Could not connect to server.");
            });
            e.printStackTrace();
        }
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

    @FXML
    public void initialize() {
        confirmationLabel.setText("");
    }

}

