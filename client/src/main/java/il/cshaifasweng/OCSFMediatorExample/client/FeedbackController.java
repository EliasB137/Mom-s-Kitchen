package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.FeedbackDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class FeedbackController {

    @FXML private RadioButton dineInRadio;
    @FXML private RadioButton deliveryRadio;
    @FXML private TextField tableField;
    @FXML private TextField restaurantField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField cardIdField;
    @FXML private TextArea feedbackField;
    @FXML private Label statusLabel;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        dineInRadio.setToggleGroup(group);
        deliveryRadio.setToggleGroup(group);

        // Default selection
        dineInRadio.setSelected(true);
        toggleFields();

        dineInRadio.setOnAction(event -> toggleFields());
        deliveryRadio.setOnAction(event -> toggleFields());
        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));
    }

    private void toggleFields() {
        boolean isDineIn = dineInRadio.isSelected();

        tableField.setDisable(!isDineIn);
        tableField.setVisible(isDineIn);

        restaurantField.setDisable(isDineIn);
        restaurantField.setVisible(!isDineIn);
    }




    @FXML
    private void handleSubmit() {
        boolean isDelivery = deliveryRadio.isSelected();

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String cardId = cardIdField.getText().trim();
        String feedback = feedbackField.getText().trim();
        String tableNumber = tableField.getText().trim();
        String restaurantName = restaurantField.getText().trim();

        if (fullName.isEmpty() || email.isEmpty() || cardId.isEmpty() || feedback.isEmpty()) {
            statusLabel.setText(" Please fill all required fields.");
            return;
        }

        if (isDelivery && restaurantName.isEmpty()) {
            statusLabel.setText(" Please enter the restaurant name.");
            return;
        }

        if (!isDelivery && tableNumber.isEmpty()) {
            statusLabel.setText(" Please enter the table number.");
            return;
        }

        FeedbackDTO feedbackDTO = new FeedbackDTO(
                fullName,
                email,
                cardId,
                isDelivery,
                tableNumber,
                restaurantName,
                feedback,
                false,
                false
        );

        try {
            SimpleClient.getClient().sendToServer(new responseDTO("submitFeedback", new Object[]{feedbackDTO}));
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText(" Feedback submitted successfully!");
            clearForm();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText(" Failed to submit feedback.");
        }
    }

    private void clearForm() {
        fullNameField.clear();
        emailField.clear();
        cardIdField.clear();
        feedbackField.clear();
        tableField.clear();
        restaurantField.clear();
    }
}
