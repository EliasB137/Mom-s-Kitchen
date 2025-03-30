package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class reservetableController {

    private restaurantDTO restaurant;
    private LocalDate selectedDate;
    @FXML
    private Button countinuebtn;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ChoiceBox<String> insideOrOutside;

    @FXML
    private TextField numberOfGuests;

    @FXML
    private ChoiceBox<String> timechoicebox;

    @FXML
    void countinuebtnpressed(ActionEvent event) {
    }


    @FXML
    void daypicked(ActionEvent event) {
        selectedDate = datePicker.getValue();
        System.out.println("day picked: " + selectedDate.getDayOfWeek().toString().toLowerCase());
        if (selectedDate != null) {

            String availableHours = restaurant.getOpeningHourForDay(selectedDate.getDayOfWeek().toString().toLowerCase());
            System.out.println(availableHours);
            if(availableHours.equals("closed")) {
                List<String> timeSlots = new ArrayList<>();
                timeSlots.add(availableHours);
                timechoicebox.getItems().setAll(timeSlots);
            }else{
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
                //timechoicebox.setItems(FXCollections.observableArrayList(timeSlots));
                timechoicebox.getItems().setAll(timeSlots);
            }
        }
    }

    @FXML
    public void initialize() {
        List<String> insideOutside = new ArrayList<>();
        insideOutside.add("Inside");
        insideOutside.add("outside");
        insideOrOutside.getItems().setAll(insideOutside);

        EventBus.getDefault().register(this);  // Register for EventBus updates

        datePicker.setOnAction(this::daypicked);
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
        restaurant = event;
    }

}
