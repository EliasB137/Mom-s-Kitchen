package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class reservetableconfirmController {

    @FXML
    private Button backButton;

    @FXML
    private TextField CCTextField;

    @FXML
    private TextField NameTextField;

    @FXML
    private TextField NumberTextField;

    @FXML
    private TextField IDTextField;

    @FXML
    private ComboBox<String> comboBox_id;

    @FXML
    private Label confirm_Label;

    @FXML
    private Button confrim_Button;

    @FXML
    private TextField emailTextField;

    @FXML
    private Label sentence1;

    @FXML
    private Label sentence2;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);  // Register for EventBus updates

        confirm_Label.setText("");
    }

    @FXML
    void confirmPressed(ActionEvent event) {
        // Check if any text field is empty
        if (NameTextField.getText().trim().isEmpty() ||
                NumberTextField.getText().trim().isEmpty() ||
                CCTextField.getText().trim().isEmpty() ||
                emailTextField.getText().trim().isEmpty() ||
                comboBox_id.getValue().isEmpty()||
                IDTextField.getText().trim().isEmpty()) {

            confirm_Label.setText("Please fill all fields.");
            return;
        }

        Object[] payload = {NameTextField.getText().trim(), NumberTextField.getText().trim(), CCTextField.getText().trim()
                , emailTextField.getText().trim(), comboBox_id.getValue(),reservetableController.date
                , reservetableController.numberOfGuest, reservetableController.restaurant.getName(),reservetableController.inOrOut,IDTextField.getText().trim()};

        // Create the DTO with message type and payload
        responseDTO requestData = new responseDTO("confirmReservation", payload);

        // Send the DTO object to the server
        try {
            SimpleClient.getClient().sendToServer(requestData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void loadhours(HoursEvent event) {
        // Always update UI controls on the JavaFX Application Thread
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Parse the controller time; assuming reservetableController.time is a String "HH:mm".
        LocalTime selectedTime = LocalTime.parse(reservetableController.time, formatter);

        List<String> availableTimes = event.getPayload();
        List<String> selectedTimes = new ArrayList<>();

        for (String timeStr : availableTimes) {
            LocalTime availableTime = LocalTime.parse(timeStr, formatter);
            // Check if availableTime is exactly the same as selectedTime
            // or within the range [selectedTime, selectedTimePlusOne].
            if (!availableTime.isBefore(selectedTime) && !availableTime.isAfter(selectedTime.plusHours(1))) {
                selectedTimes.add(timeStr);
                // If you want to act on this time, you could do it here.
                // For instance, update some UI element:
                // reservetableController.updateAvailableTime(availableTime);
            }
        }
        if(!selectedTimes.isEmpty()) {
            Platform.runLater(() -> {
                comboBox_id.setItems(FXCollections.observableArrayList(selectedTimes));
                sentence1.setText("these are times that are in 1 hours of your selected time");
                sentence2.setText("Please pick one of them");

            });
        }else if(!availableTimes.isEmpty()) {
            Platform.runLater(() -> {
                sentence1.setText("there is no space in the time you chose, here are some different options");
                sentence2.setText("Please pick one of them");
                comboBox_id.setItems(FXCollections.observableArrayList(availableTimes));
            });
        }else {
            Platform.runLater(() -> {
                comboBox_id.setItems(FXCollections.observableArrayList(availableTimes));
                sentence2.setText("");
                sentence1.setText("there is no time in this day to seat your party, please choose a different day.");
            });
        }

    }

    @Subscribe
    public void confirmation(reservationResultEvent event) {
        Platform.runLater(() -> {
            if(event.getMessage().equals("failed"))
                confirm_Label.setText("The reservation failed :( Please try again");
            else
                confirm_Label.setText("The reservation is saved :) Thank you!");
        });
    }


    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void backAction(ActionEvent event) {
        onClose();
        SimpleClient.getClient().navigateTo("reservetableView");
    }
}
