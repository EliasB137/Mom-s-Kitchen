package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class dishViewController {

    @FXML private Label dishNameLabel;
    @FXML private ImageView dishImageView;
    @FXML private Label dishIngredientsLabel;
    @FXML private Label dishPriceLabel;
    @FXML private Label dishDeliveryLabel;
    @FXML private VBox preferencesVBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addToCartButton;
    @FXML private Button backButton;

    private String selectedRestaurant;
    private dishDTO selectedDish;

    @FXML
    public void initialize() {
        selectedDish = SimpleClient.getSelectedDish();
        selectedRestaurant = SimpleClient.getSelectedRestaurant();

        if (selectedDish != null) {
            dishNameLabel.setText(selectedDish.getName());
            dishIngredientsLabel.setText("Ingredients: " + selectedDish.getIngredients());
            dishPriceLabel.setText("Price: $" + selectedDish.getPrice());
            dishDeliveryLabel.setText("Delivery: " + (selectedDish.isDeliveryAvailable() ? "Available" : "Not Available"));

            if (selectedDish.getImageUrl() != null && !selectedDish.getImageUrl().isEmpty()) {
                dishImageView.setImage(new Image(selectedDish.getImageUrl()));
            }

            // Setup preferences
            preferencesVBox.getChildren().clear();
            for (String pref : selectedDish.getAvailablePreferences()) {
                CheckBox checkBox = new CheckBox(pref);
                preferencesVBox.getChildren().add(checkBox);
            }

            // Setup quantity spinner
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
            quantitySpinner.setValueFactory(valueFactory);

        } else {
            System.err.println("[ERROR] No dish selected!");
        }

        addToCartButton.setOnAction(event -> {
            System.out.println("[DEBUG] Add to Cart button pressed");
            handleAddToCart();
        });
        backButton.setOnAction(event -> handleOrderFood());
    }

    private void handleAddToCart() {
        List<String> selectedPreferences = new ArrayList<>();
        for (var node : preferencesVBox.getChildren()) {
            if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                selectedPreferences.add(((CheckBox) node).getText());
            }
        }

        int quantity = quantitySpinner.getValue();
        System.out.println("Adding to cart: " + selectedDish.getName());
        System.out.println("Selected preferences: " + selectedPreferences);
        System.out.println("Quantity: " + quantity);

        // extend SimpleClient.addToCart to handle preferences and quantity
        CartStore.addItem(new CartItem(selectedDish, selectedPreferences, quantity));

    }

    private void handleOrderFood() {
        if (selectedRestaurant == null) {
            System.out.println("ERROR: No restaurant selected!");
            return;
        }

        System.out.println("Navigating to orderFoodView with restaurant: " + selectedRestaurant);
        orderFoodController controller = (orderFoodController) SimpleClient.getClient().navigateTo("orderFoodView");
        if (controller != null) {
            controller.setSelectedRestaurant(selectedRestaurant);
        }
    }
}
