package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.DataPointEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewReservation;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.DataPoint;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.Report;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class ViewMapController {

    private String selectedRestaurant;
    private boolean searching;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<String> restaurantComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<DataPoint> Table;
    @FXML
    private TableColumn<DataPoint, String> reservedTableColumn;
    @FXML
    private TableColumn<DataPoint, String> tableIDColumn;

    private ObservableList<String> restaurantList = FXCollections.observableArrayList();
    private ObservableList<DataPoint> mapTablesList = FXCollections.observableArrayList();

    @FXML
    public void backAction() {
        onClose();
        SimpleClient.getClient().navigateTo("adminHomeView");
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void searchAction(ActionEvent event) {
        selectedRestaurant = restaurantComboBox.getValue();
        if (selectedRestaurant == null) {
            messageLabel.setText("Please select a restaurant");
            return;
        }
        searching = true;
        responseDTO response = new responseDTO("getTableMap",new Object[]{selectedRestaurant});
        try {
            SimpleClient.getClient().sendToServer(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        searching = false;
        EventBus.getDefault().register(this);  // Register for EventBus updates

        messageLabel.setText("");
        tableIDColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getX())));
        reservedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(numberToString(data.getValue().getY())));
        Table.setItems(mapTablesList);

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

    @Subscribe
    public void loadTables(DataPointEvent eventList) {
        System.out.println("EventBus triggered loadTables()!");
        List<DataPoint> event = eventList.getPayload();
        // Handle UI updates on JavaFX Application Thread
        Platform.runLater(() -> {
            if (event == null || event.isEmpty()) {
                System.out.println("[ERROR] Should not be here!");
                return;
            }
            mapTablesList.setAll(event);
        });
    }
    private String numberToString(int number) {
        if (number == 1) {
            return "Reserved";
        }
        return "Not Reserved";
    }

    @Subscribe
    public void handleNewReservations(NewReservation event) {
        if (searching) {
            responseDTO response = new responseDTO("getTableMap", new Object[]{selectedRestaurant});
            Platform.runLater(() -> messageLabel.setText(LocalTime.now().toString() + "times available may change."));
        }
    }
}
