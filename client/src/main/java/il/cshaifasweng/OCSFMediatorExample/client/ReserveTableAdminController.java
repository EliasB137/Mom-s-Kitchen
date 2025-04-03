package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.HoursEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewReservation;
import il.cshaifasweng.OCSFMediatorExample.client.events.reservationResultEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.Events.CancellationResultEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReserveTableAdminController {

    private boolean searching = false;
    private LocalDate date;
    private String roundedTime;
    private String inOrOut;
    private String numberOfGuest;
    private String restaurantName;
    @FXML
    private ComboBox<String> availableTimesComboBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Button backButton;

    @FXML
    private ChoiceBox<String> insideOrOutsideChoiceBox;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField numberOfGuestsTextField;

    @FXML
    private TextField numberTextField;

    @FXML
    private ComboBox<String> restaurantsComboBox;

    @FXML
    private Button searchForAvailableTimesButton;

    private ObservableList<String> restaurantList = FXCollections.observableArrayList();


    @FXML
    void confirmAction(ActionEvent event) {
        // Check if any text field is empty
        if (nameTextField.getText().trim().isEmpty() ||
                numberTextField.getText().trim().isEmpty() ||
                availableTimesComboBox.getValue().isEmpty()) {

            Platform.runLater(() -> messageLabel.setText("Please fill all fields."));
            return;
        }

        Object[] payload = {nameTextField.getText().trim(), numberTextField.getText().trim(), "Not Needed"
                , "Not Needed", availableTimesComboBox.getValue() , date , numberOfGuest,
                 restaurantName , inOrOut , SimpleClient.getUserID()};

        // Create the DTO with message type and payload
        responseDTO requestData = new responseDTO("confirmReservation", payload);

        // Send the DTO object to the server
        try {
            SimpleClient.getClient().sendToServer(requestData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void searchForAvailableTimesAction(ActionEvent event) {
        if (restaurantsComboBox.getValue().isEmpty() ||
                numberOfGuestsTextField.getText().trim().isEmpty() ||
                insideOrOutsideChoiceBox.getValue().isEmpty()) {
            Platform.runLater(() -> messageLabel.setText("Please fill all fields."));
            return;
        }

        // Your existing code to get the values
        date = LocalDate.now();
        LocalTime time = LocalTime.now();
        roundedTime = roundUpToNext15Minutes(time).toString();
        inOrOut = insideOrOutsideChoiceBox.getValue();
        numberOfGuest = numberOfGuestsTextField.getText();
        restaurantName = restaurantsComboBox.getValue();
        searchForAvailableTimes();
        searching = true;
    }

    @FXML
    public void initialize() {
        List<String> insideOutside = new ArrayList<>();
        insideOutside.add("Inside");
        insideOutside.add("Outside");
        Platform.runLater(() -> {
            insideOrOutsideChoiceBox.getItems().setAll(insideOutside);
        });

        EventBus.getDefault().register(this);  // Register for EventBus updates

        messageLabel.setText("");
        restaurantsComboBox.setItems(restaurantList);
        requestRestaurantList();
    }

    public static LocalTime roundUpToNext15Minutes(LocalTime time) {
        int minutes = time.getMinute();
        int roundedMinutes = ((minutes / 15) + 1) * 15; // Round up to next multiple of 15

        if (roundedMinutes == 60) {
            // If minutes reach 60, increment hour and reset minutes to 0
            time = time.plusHours(1).withMinute(0);
        } else {
            time = time.withMinute(roundedMinutes);
        }

        return time.truncatedTo(ChronoUnit.MINUTES);
    }

    public void searchForAvailableTimes(){
        Platform.runLater(() -> messageLabel.setText(""));
        try {

            // Create a payload array with the data
            Object[] payload = {date, roundedTime, inOrOut, numberOfGuest, restaurantName};

            // Create the DTO with message type and payload
            responseDTO requestData = new responseDTO("getHours", payload);

            // Send the DTO object to the server
            SimpleClient.getClient().sendToServer(requestData);
        } catch (NumberFormatException | IOException e) {
            System.out.println("Invalid number of guests");
        }
    }


    @Subscribe
    public void handleNewReservations(NewReservation event) {
        searchForAvailableTimes();
        Platform.runLater(() -> messageLabel.setText("New reservation was placed, times available may change."));
    }

    @Subscribe
    public void confirmation(reservationResultEvent event) {
        Platform.runLater(() -> {
            if(event.getMessage().equals("failed"))
                messageLabel.setText("The reservation failed :( Please try again");
            else
                messageLabel.setText("The reservation is saved :) Thank you!");
        });
    }

    @FXML
    public void backAction() {
        onClose();
        SimpleClient.getClient().navigateTo("adminHomeView");
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

    private void requestRestaurantList() {
        try {
            SimpleClient.getClient().sendToServer("getRestaurants");
            System.out.println("Requesting restaurant list from server...");
        } catch (IOException e) {
            System.err.println("ERROR: Could not send 'getRestaurants' request!");
            e.printStackTrace();
        }
    }
    @Subscribe
    public void loadRestaurants(List<restaurantDTO> event) {
        System.out.println("EventBus triggered loadRestaurants()!");

        if (event == null || event == null || event.isEmpty()) {
            System.out.println("No restaurants available.");
            return;
        }
        System.out.println("Received " + event.size() + " restaurants from EventBus.");
        restaurantList.clear();
        for (restaurantDTO restaurant : event) {
            System.out.println("Dropdown Adding: " + restaurant.getName());
            restaurantList.add(restaurant.getName());
        }
    }

    @Subscribe
    public void loadhours(HoursEvent event) {
        // Always update UI controls on the JavaFX Application Thread
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Parse the controller time; assuming reservetableController.time is a String "HH:mm".
        LocalTime selectedTime = LocalTime.parse(roundedTime, formatter);

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
                availableTimesComboBox.setItems(FXCollections.observableArrayList(selectedTimes));
            });
        }else if(!availableTimes.isEmpty()) {
            Platform.runLater(() -> {
                messageLabel.setText("there is no space right now to seat you, these are the options in the day.");
                availableTimesComboBox.setItems(FXCollections.observableArrayList(availableTimes));
            });
        }else {
            availableTimesComboBox.setItems(FXCollections.observableArrayList(availableTimes));
            messageLabel.setText("there is no time in this day to seat your party:(");
        }

    }
}
