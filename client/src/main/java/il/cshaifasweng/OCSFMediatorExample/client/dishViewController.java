package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class dishViewController {

    @FXML private Label dishNameLabel;
    @FXML private ImageView dishImageView;
    @FXML private Label dishIngredientsLabel;
    @FXML private Label dishPreferencesLabel;
    @FXML private Label dishPriceLabel;
    @FXML private Label dishDeliveryLabel;
    @FXML private Button addToCartButton;
    @FXML private Button backButton;
    private String selectedRestaurant; // Stores the selected restaurant
    private Dish selectedDish;

    @FXML
    public void initialize() {
        selectedDish = SimpleClient.getSelectedDish();
        selectedRestaurant = SimpleClient.getSelectedRestaurant();
        if (selectedDish != null) {
            dishNameLabel.setText(selectedDish.getName());
            dishIngredientsLabel.setText("Ingredients: " + selectedDish.getIngredients());
            dishPreferencesLabel.setText("Preferences: " + selectedDish.getPersonalPreference());
            dishPriceLabel.setText("Price: $" + selectedDish.getPrice());
            dishDeliveryLabel.setText("Delivery: " + (selectedDish.isDeliveryAvailable() ? "Available" : "Not Available"));

            if (selectedDish.getImageUrl() != null && !selectedDish.getImageUrl().isEmpty()) {
                dishImageView.setImage(new Image(selectedDish.getImageUrl()));
            }
        } else {
            System.err.println("[ERROR] No dish selected!");
        }

        addToCartButton.setOnAction(event -> handleAddToCart());
        backButton.setOnAction(event -> handleOrderFood());

    }

    private void handleOrderFood() {
        if (selectedRestaurant == null) {
            System.out.println("ERROR: No restaurant selected!");
            return;
        }

        System.out.println("Navigating to orderFoodView with restaurant: " + selectedRestaurant);

        // Pass selected restaurant to order food controller
        orderFoodController controller = (orderFoodController) SimpleClient.getClient().navigateTo("orderFoodView");
        if (controller != null) {
            controller.setSelectedRestaurant(selectedRestaurant);
        }
    }
    private void handleAddToCart() {
        System.out.println("Added to cart: " + selectedDish.getName());
        // Implement cart logic here
        // either implement another sql table or save the cart loccally for the client
    }
}
