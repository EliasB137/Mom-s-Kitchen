package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.HoursEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class reservetableconfirmController {

    @FXML
    private ComboBox<String> comboBox_id;

    @FXML
    private Label confirm_Label;

    @FXML
    private Button confrim_Button;

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
                sentence1.setText("there is no space in the time you should, here are some different options");
                sentence2.setText("Please pick one of them");
                comboBox_id.setItems(FXCollections.observableArrayList(availableTimes));
            });
        }else {
            comboBox_id.setItems(FXCollections.observableArrayList(availableTimes));
            sentence2.setText("");
            sentence1.setText("there is no time in this day to seat your party, please choose a different day.");
        }

    }

}
