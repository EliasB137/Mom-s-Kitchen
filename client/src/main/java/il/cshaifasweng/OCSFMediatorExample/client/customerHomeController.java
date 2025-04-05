package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class customerHomeController {

    @FXML
    private Button viewMenuButton;

    @FXML
    private Button orderFoodButton;

    @FXML
    private Button reserveTableButton;

    @FXML
    private Button feedbackButton;

    @FXML
    private Button backButton;

    @FXML
    private Button viewCartButton;


    private String selectedRestaurant;

    @FXML
    public void initialize() {
        selectedRestaurant = SimpleClient.getSelectedRestaurant();
        viewMenuButton.setOnAction(event -> SimpleClient.getClient().navigateTo("MenuView"));
        orderFoodButton.setOnAction(event -> handleOrderFood());
        reserveTableButton.setOnAction(_ -> SimpleClient.getClient().navigateTo("reservetableView"));
        feedbackButton.setOnAction(event -> SimpleClient.getClient().navigateTo("feedbackView"));
        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("startPageView"));
        viewCartButton.setOnAction(event -> SimpleClient.getClient().navigateTo("cartView"));
    }

    public void setSelectedRestaurant(String restaurant) {
        this.selectedRestaurant = restaurant;
        System.out.println("Customer Home Controller set restaurant to: " + restaurant);
    }

    private void handleOrderFood() {
        if (selectedRestaurant == null) {
            System.out.println("ERROR: No restaurant selected!");
            return;
        }

        System.out.println("Navigating to orderFoodView with restaurant: " + selectedRestaurant);

         //Pass selected restaurant to order food controller
        orderFoodController controller = (orderFoodController) SimpleClient.getClient().navigateTo("orderFoodView");
        if (controller != null) {
            controller.setSelectedRestaurant(selectedRestaurant);
        }
    }
}
