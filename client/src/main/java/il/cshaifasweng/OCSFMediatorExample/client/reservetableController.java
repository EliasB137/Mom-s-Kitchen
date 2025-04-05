package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class reservetableController {

    public static restaurantDTO restaurant;
    public static String time;
    public static LocalDate date;
    public static String inOrOut;
    public static String numberOfGuest;

    @FXML
    private Button backButton;

    @FXML
    private Button countinuebtn;

    @FXML
    private Label fieldsNotFilled;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> insideOrOutsideComboBox;

    @FXML
    private TextField numberOfGuests;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    void continueBtnPressed(ActionEvent event) {
        LocalDate today = LocalDate.now();
        LocalDate selectedDate = datePicker.getValue();
        String timeString = timeComboBox.getValue();
        if(timeString.equals("closed")){
            Platform.runLater(() -> fieldsNotFilled.setText("Restaurant is closed!"));
            return;
        }
        if (datePicker.getValue() == null) {
            System.out.println("Date is null");
            Platform.runLater(() -> fieldsNotFilled.setText("Fields not filled(Date)"));
            return;
        }
        if (timeComboBox.getValue() == null) {
            System.out.println("Time is null");
            Platform.runLater(() -> fieldsNotFilled.setText("Fields not filled(Time)"));
            return;
        }
        if (insideOrOutsideComboBox.getValue() == null) {
            System.out.println("Inside/Outside choice is null");
            Platform.runLater(() -> fieldsNotFilled.setText("Fields not filled(Inside/Outside)"));
            return;
        }
        if (numberOfGuests.getText().isEmpty()) {
            System.out.println("Number of guests is empty");
            Platform.runLater(() -> fieldsNotFilled.setText("Fields not filled(number of guests)"));
            return;
        }
        if (selectedDate.isBefore(today)) {
            Platform.runLater(() -> fieldsNotFilled.setText("Please select a future date for reservation."));
            return;
        }
        if (selectedDate.isEqual(today)) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime selectedTime = LocalTime.parse(timeString, timeFormatter);
            LocalTime now = LocalTime.now();

            if (selectedTime.isBefore(now)) {
                Platform.runLater(() -> fieldsNotFilled.setText("Please select a future time for today."));
                return;
            }
        }
        Platform.runLater(() -> fieldsNotFilled.setText(""));
        try {

            // Your existing code to get the values
            date = datePicker.getValue();
            time = timeComboBox.getValue();
            inOrOut = insideOrOutsideComboBox.getValue();
            numberOfGuest = numberOfGuests.getText();

            // Create a payload array with the data
            Object[] payload = {date, time, inOrOut, numberOfGuest, restaurant.getName()};

            // Create the DTO with message type and payload
            responseDTO requestData = new responseDTO("getHours", payload);

            // Send the DTO object to the server
            SimpleClient.getClient().sendToServer(requestData);

            onClose();

            SimpleClient.getClient().navigateTo("reservetableconfirmView");
        } catch (NumberFormatException | IOException e) {
            System.out.println("Invalid number of guests");
        }
    }

    @FXML
    void daypicked(ActionEvent event) {
        date = datePicker.getValue();
        System.out.println("date picked: " + date.toString().toLowerCase());
        if (date != null) {

            String availableHours = restaurant.getOpeningHourForDay(date.getDayOfWeek().toString().toLowerCase());
            System.out.println(availableHours);

            Platform.runLater(() -> {
                if(availableHours.equals("closed")) {
                    List<String> timeSlots = new ArrayList<>();
                    timeSlots.add(availableHours);
                    timeComboBox.getItems().setAll(timeSlots);
                } else {
                    String[] parts = availableHours.split("-");
                    LocalTime startTime = LocalTime.parse(parts[0] + ":00"); // Convert "8:00" to "08:00:00"
                    LocalTime endTime = LocalTime.parse(parts[1] + ":00");   // Convert "22:00" to "22:00:00"
                    endTime = endTime.minusHours(1);

                    List<String> timeSlots = new ArrayList<>();
                    LocalTime currentTime = startTime;
                    while (currentTime.isBefore(endTime)) {
                        timeSlots.add(currentTime.toString()); // Converts to "08:00", "08:15", etc.
                        currentTime = currentTime.plusMinutes(15);
                    }
                    timeComboBox.getItems().setAll(timeSlots);
                }
            });
        }
    }

    @FXML
    public void initialize() {
        List<String> insideOutside = new ArrayList<>();
        insideOutside.add("Inside");
        insideOutside.add("outside");

        Platform.runLater(() -> {
            insideOrOutsideComboBox.getItems().setAll(insideOutside);
            datePicker.setOnAction(this::daypicked);
        });

        EventBus.getDefault().register(this);  // Register for EventBus updates

        try {
            SimpleClient.getClient().sendToServer("getRestaurantByName:" + SimpleClient.getSelectedRestaurant());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void loadhours(restaurantDTO event) {
        System.out.println("EventBus triggered loadHours()!");

        if (event == null) {
            System.out.println("No restaurant available.");
            return;
        }

        System.out.println("Received " + event.getName() + " restaurants from EventBus.");

        Platform.runLater(() -> {
            restaurant = event;
            // If you need to update any UI components based on the restaurant data, do it here
        });
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void backAction(ActionEvent event) {
        onClose();
        SimpleClient.getClient().navigateTo("customerHomeView");
    }
}