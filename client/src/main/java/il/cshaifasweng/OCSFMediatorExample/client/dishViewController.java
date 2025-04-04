package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.CartItem;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.RequestedChangesDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
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
    @FXML private VBox dietitianEditBox;
    @FXML private TextField priceField;
    @FXML private TextArea ingredientsArea;
    @FXML private Button saveChangesButton;
    @FXML private Button requestChainButton;
    @FXML private Label selectPreferencesLabel;
    @FXML private HBox spinnerbox;

    private String selectedRestaurant;
    private dishDTO selectedDish;
    private String userRole;

    @FXML
    public void initialize() {
        // First, get the selected dish and restaurant
        selectedDish = SimpleClient.getSelectedDish();
        selectedRestaurant = SimpleClient.getSelectedRestaurant();
        userRole = SimpleClient.getUserRole();

        // Check if we have a valid dish
        if (selectedDish == null) {
            System.err.println("[ERROR] No dish selected!");
            showErrorAlert("No dish data found", "Please try again");
            navigateBack(); // Return to previous screen
            return;
        }

        // Populate the UI with dish details
        dishNameLabel.setText(selectedDish.getName());
        dishIngredientsLabel.setText("Ingredients: " + selectedDish.getIngredients());
        dishPriceLabel.setText("Price: $" + selectedDish.getPrice());
        dishDeliveryLabel.setText("Delivery: " + (selectedDish.isDeliveryAvailable() ? "Available" : "Not Available"));

        if (selectedDish.getImageUrl() != null && !selectedDish.getImageUrl().isEmpty()) {
            try {
                dishImageView.setImage(new Image(selectedDish.getImageUrl()));
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }

        // Setup preferences
        setupPreferences();

        if (selectedDish.getAvailablePreferences() == null || selectedDish.getAvailablePreferences().isEmpty()) {
            preferencesVBox.setVisible(false);
            preferencesVBox.setManaged(false);
            selectPreferencesLabel.setVisible(false);
            selectPreferencesLabel.setManaged(false);
        } else
        {
            selectPreferencesLabel.setVisible(true);
            selectPreferencesLabel.setManaged(true);
        }

        // Setup quantity spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        quantitySpinner.setValueFactory(valueFactory);

        // Setup buttons
        addToCartButton.setOnAction(event -> {
            System.out.println("[DEBUG] Add to Cart button pressed");
            handleAddToCart();
        });

        // Set up back button with different behavior based on user role
        backButton.setOnAction(event -> navigateBack());

        // Handle dietitian-specific UI
        setupDietitianUI();
    }

    private void setupPreferences() {
        preferencesVBox.getChildren().clear();
        if (selectedDish.getAvailablePreferences() != null) {
            for (String pref : selectedDish.getAvailablePreferences()) {
                CheckBox checkBox = new CheckBox(pref);
                preferencesVBox.getChildren().add(checkBox);
            }
        }
    }

    private void setupDietitianUI() {
        // By default, hide dietitian UI
        dietitianEditBox.setVisible(false);
        dietitianEditBox.setManaged(false);
        preferencesVBox.setManaged(true);
        preferencesVBox.setVisible(true);
        spinnerbox.setManaged(true);
        spinnerbox.setVisible(true);
        selectPreferencesLabel.setVisible(true);

        if ("dietitian".equals(userRole)) {
            dietitianEditBox.setVisible(true);
            dietitianEditBox.setManaged(true);

            selectPreferencesLabel.setVisible(false);
            spinnerbox.setManaged(false);
            spinnerbox.setVisible(false);
            preferencesVBox.setManaged(false);
            preferencesVBox.setVisible(false);
            // Add to cart button not needed for dietitians
            addToCartButton.setVisible(false);

            // Set initial values
            priceField.setText(selectedDish.getPrice());
            ingredientsArea.setText(selectedDish.getIngredients());

            saveChangesButton.setOnAction(event -> {
                String newPrice = priceField.getText();
                String newIngredients = ingredientsArea.getText();

                // Create and send RequestedChangesDTO
                try {
                    RequestedChangesDTO changeRequest = new RequestedChangesDTO(
                            Integer.parseInt(newPrice),
                            newIngredients,
                            selectedDish.getName(),
                            String.join(",", selectedDish.getAvailablePreferences()),
                            selectedDish.getId()
                    );

                    responseDTO request = new responseDTO("submitPriceChangeRequest", new Object[]{changeRequest});
                    SimpleClient.getClient().sendToServer(request);

                    showSuccessAlert("Changes submitted", "Your change request has been sent for approval.");
                } catch (NumberFormatException e) {
                    showErrorAlert("Invalid Price", "Please enter a valid number for price");
                } catch (IOException e) {
                    System.err.println("Error sending change request: " + e.getMessage());
                    showErrorAlert("Network Error", "Could not submit changes. Please try again.");
                }
            });

            requestChainButton.setOnAction(event -> {
                try {
                    responseDTO request = new responseDTO("defineAsChainDish", new Object[]{selectedDish.getId()});
                    SimpleClient.getClient().sendToServer(request);
                    showSuccessAlert("Request Submitted", "Request to define as chain dish has been submitted.");
                } catch (IOException e) {
                    System.err.println("Error sending chain dish request: " + e.getMessage());
                    showErrorAlert("Network Error", "Could not submit request. Please try again.");
                }
            });
        }
    }

    /**
     * Navigate back based on user role
     * - Dietitians go back to menuView
     * - Customers go back to orderFoodView
     */
    private void navigateBack() {
        System.out.println("Navigating back based on role: " + userRole);

        if ("dietitian".equals(userRole)) {
            // Dietitians go back to menu view
            System.out.println("Dietitian role detected, navigating to menuView");
            SimpleClient.getClient().navigateTo("menuView");
        } else {
            // Regular customers go back to order food view
            System.out.println("Customer role detected, navigating to orderFoodView");
            try {
                orderFoodController controller = (orderFoodController) SimpleClient.getClient().navigateTo("orderFoodView");
                if (controller != null && selectedRestaurant != null) {
                    controller.setSelectedRestaurant(selectedRestaurant);
                }
            } catch (Exception e) {
                System.err.println("Error navigating to orderFoodView: " + e.getMessage());
                // Fallback navigation if orderFoodView fails
                SimpleClient.getClient().navigateTo("menuView");
            }
        }
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

        // Add to cart
        CartStore.addItem(new CartItem(selectedDish, selectedPreferences, quantity));

        // Show confirmation
        showSuccessAlert("Added to Cart", quantity + " x " + selectedDish.getName() + " added to your cart.");
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}