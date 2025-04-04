package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class ReportsPickController {

    @FXML
    private Button backButton;

    @FXML
    private Button feedbackForTheChainButton;

    @FXML
    private Button feedbackReportButton;

    @FXML
    private Button ordersReportButton;

    @FXML
    private Label pickRestaurantLabel;

    @FXML
    private ComboBox<String> restaurantComboBox;

    @FXML
    private Button seatingsReportButton;

    @FXML
    private Label messageLabel;

    private ObservableList<String> restaurantList = FXCollections.observableArrayList();

    @FXML
    void FeedbackReportAction(ActionEvent event) {
        String selectedRestaurant = restaurantComboBox.getValue();
        if (selectedRestaurant == null) {
            messageLabel.setText("Please select a restaurant");
            return;
        }

        //Save selected restaurant in SimpleClient
        SimpleClient.setSelectedRestaurant(selectedRestaurant);
        onClose();
        SimpleClient.getClient().navigateTo("ReportFeedbackView");
    }

    @FXML
    public void backAction() {
        onClose();
        SimpleClient.getClient().navigateTo("adminHomeView");
    }

    @FXML
    void feedbackForTheChainAction(ActionEvent event) {
        SimpleClient.setSelectedRestaurant("not needed");
        onClose();
        SimpleClient.getClient().navigateTo("ReportFeedbackView");
    }

    @FXML
    void ordersReportAction(ActionEvent event) {
        String selectedRestaurant = restaurantComboBox.getValue();
        if (selectedRestaurant == null) {
            messageLabel.setText("Please select a restaurant");
            return;

        }

        //Save selected restaurant in SimpleClient
        SimpleClient.setSelectedRestaurant(selectedRestaurant);
        onClose();
        SimpleClient.getClient().navigateTo("ReportOrderView");
    }

    @FXML
    void seatingReportAction(ActionEvent event) {
        String selectedRestaurant = restaurantComboBox.getValue();
        if (selectedRestaurant == null) {
            messageLabel.setText("Please select a restaurant");
            return;

        }

        //Save selected restaurant in SimpleClient
        SimpleClient.setSelectedRestaurant(selectedRestaurant);
        onClose();
        SimpleClient.getClient().navigateTo("ReportSeatingsView");
    }

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);  // Register for EventBus updates

        messageLabel.setText("");
        restaurantComboBox.setItems(restaurantList);
        requestRestaurantList();
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


    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

}
