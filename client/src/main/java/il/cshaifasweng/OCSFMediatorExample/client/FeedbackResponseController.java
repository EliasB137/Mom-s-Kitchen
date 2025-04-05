package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.FeedbackDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class FeedbackResponseController {

    @FXML private Label feedbackInfoLabel;
    @FXML private TextArea responseTextArea;
    @FXML private CheckBox compensationCheckbox;

    private FeedbackDTO selectedFeedback;

    @FXML
    public void initialize() {
        selectedFeedback = FeedbackReviewController.feedbackToRespond;

        if (selectedFeedback != null) {
            feedbackInfoLabel.setText("Responding to feedback from: " + selectedFeedback.getFullName() + " (" + selectedFeedback.getEmail() + ")");
        } else {
            feedbackInfoLabel.setText("No feedback selected!");
            responseTextArea.setDisable(true);
            compensationCheckbox.setDisable(true);
        }
    }

    @FXML
    private void handleSendResponse() {
        if (selectedFeedback == null) return;

        String response = responseTextArea.getText().trim();
        boolean compensated = compensationCheckbox.isSelected();

        if (response.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Empty Response");
            alert.setContentText("Please enter a response before sending.");
            alert.show();
            return;
        }

        Object[] payload = new Object[] {
                selectedFeedback.getFullName(),
                selectedFeedback.getEmail(),
                selectedFeedback.getFeedback(),
                response,
                compensated
        };

        try {
            responseDTO request = new responseDTO("respondToFeedback", payload);
            SimpleClient.getClient().sendToServer(request);
            System.out.println("Sending respondToFeedback request to server");
        } catch (IOException e) {
            System.err.println("Error sending feedback response: " + e.getMessage());
        }

        // Go back after response
        SimpleClient.getClient().navigateTo("FeedbackReviewView");
    }

    @FXML
    private void handleBack() {
        SimpleClient.getClient().navigateTo("FeedbackReviewView");
    }
}
