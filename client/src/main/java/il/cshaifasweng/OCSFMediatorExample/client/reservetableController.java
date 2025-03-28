//package il.cshaifasweng.OCSFMediatorExample.client;
//
//import il.cshaifasweng.OCSFMediatorExample.entities.SavingInSql.Restaurant;
//import javafx.collections.FXCollections;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.ChoiceBox;
//import javafx.scene.control.DatePicker;
//import javafx.scene.control.TextField;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class reservetableController {
//
//    EntityManagerFactory emf;
//    @FXML
//    private Button countinuebtn;
//
//    @FXML
//    private DatePicker datePicker;
//
//    @FXML
//    private ChoiceBox<String> insideOrOutside;
//
//    @FXML
//    private TextField numberOfGuests;
//
//    @FXML
//    private ChoiceBox<String> timechoicebox;
//
//    @FXML
//    void countinuebtnpressed(ActionEvent event) {
//
//    }
//
//    @FXML
//    void daypicked(ActionEvent event) {
//        LocalDate selectedDate = datePicker.getValue();
//        if (selectedDate != null) {
//            EntityManager em = emf.createEntityManager();
//            Restaurant restaurant = em.find(Restaurant.class, SimpleClient.getSelectedRestaurant());
//            String availableHours = restaurant.getOpeningHourForDay(selectedDate.getDayOfWeek().toString());
//            String[] parts = availableHours.split(" - ");
//            LocalTime startTime = LocalTime.parse(parts[0] + ":00"); // Convert "8:00" to "08:00:00"
//            LocalTime endTime = LocalTime.parse(parts[1] + ":00");   // Convert "22:00" to "22:00:00"
//
//            List<String> timeSlots = new ArrayList<>();
//            LocalTime currentTime = startTime;
//            while (currentTime.isBefore(endTime)) {
//                timeSlots.add(currentTime.toString()); // Converts to "08:00", "08:15", etc.
//                currentTime = currentTime.plusMinutes(15);
//            }
//            //timechoicebox.setItems(FXCollections.observableArrayList(timeSlots));
//            timechoicebox.getItems().setAll(timeSlots);
//        }
//    }
//
//    @FXML
//    public void initialize() {
//        datePicker.setOnAction(this::daypicked);
//    }
//
//}
