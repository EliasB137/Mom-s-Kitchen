package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class startPageController {

    @FXML
    private ChoiceBox<String> restaurantChoiceBox;

    @FXML
    private Button viewOrderButton;

    @FXML
    private Button adminSignInButton;

    @FXML
    private Button viewReservationButton;

    @FXML
    private Button continueButton;

    @FXML
    private Button exitButton;

    @FXML
    private Label statusLabel;

    private ObservableList<String> restaurantList = FXCollections.observableArrayList();

    public startPageController() {
        EventBus.getDefault().register(this);  // Register for EventBus updates
    }

    @FXML
    public void initialize() {
        System.out.println("StartPageController Initialized!");

        restaurantChoiceBox.setItems(restaurantList);
        requestRestaurantList();

        viewOrderButton.setOnAction(event -> SimpleClient.getClient().navigateTo("viewOrderView"));
        viewReservationButton.setOnAction(event -> SimpleClient.getClient().navigateTo("viewReservationView"));
        adminSignInButton.setOnAction(event -> SimpleClient.getClient().navigateTo("adminSignInView"));
        continueButton.setOnAction(event -> handleContinue());
        exitButton.setOnAction(event -> exitApplication());
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

    private void handleContinue() {
        String selectedRestaurant = restaurantChoiceBox.getValue();
        if (selectedRestaurant == null) {
            showAlert("Please select a restaurant before continuing.");
            return;

        }

        //Save selected restaurant in SimpleClient
        SimpleClient.setSelectedRestaurant(selectedRestaurant);
        System.out.println("[DEBUG] Selected restaurant stored: " + SimpleClient.getSelectedRestaurant());
        System.out.println("Navigating to customerHomeView with restaurant: " + selectedRestaurant);
        onClose();
        SimpleClient.getClient().navigateTo("customerHomeView");
    }



    private void exitApplication() {
        System.out.println("🚪 Exiting application...");
        System.exit(0);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

}
