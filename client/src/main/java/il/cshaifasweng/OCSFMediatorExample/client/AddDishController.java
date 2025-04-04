package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddDishController {

    @FXML private TextField nameField;
    @FXML private TextArea ingredientsArea;
    @FXML private TextField priceField;
    @FXML private CheckBox deliveryCheckbox;
    @FXML private ListView<String> restaurantListView;
    @FXML private TextField restaurantField;
    @FXML private Button addRestaurantButton;
    @FXML private TextField imageUrlField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // For preferences
    @FXML private ListView<String> preferencesListView;
    @FXML private TextField preferenceField;
    @FXML private Button addPreferenceButton;

    private ObservableList<String> restaurantList = FXCollections.observableArrayList();
    private ObservableList<String> preferencesList = FXCollections.observableArrayList();

    public AddDishController() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void initialize() {
        // Setup restaurant list
        restaurantListView.setItems(restaurantList);

        // Setup preferences list
        preferencesListView.setItems(preferencesList);

        // Configure Add Restaurant button
        addRestaurantButton.setOnAction(event -> {
            String restaurant = restaurantField.getText().trim();
            if (!restaurant.isEmpty() && !restaurantList.contains(restaurant)) {
                restaurantList.add(restaurant);
                restaurantField.clear();
            }
        });

        // Configure Add Preference button
        addPreferenceButton.setOnAction(event -> {
            String preference = preferenceField.getText().trim();
            if (!preference.isEmpty() && !preferencesList.contains(preference)) {
                preferencesList.add(preference);
                preferenceField.clear();
            }
        });

        // Configure Delete restaurant functionality (right-click menu)
        ContextMenu restaurantContextMenu = new ContextMenu();
        MenuItem deleteRestaurantItem = new MenuItem("Remove");
        deleteRestaurantItem.setOnAction(event -> {
            int selectedIndex = restaurantListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                restaurantList.remove(selectedIndex);
            }
        });
        restaurantContextMenu.getItems().add(deleteRestaurantItem);
        restaurantListView.setContextMenu(restaurantContextMenu);

        // Configure Delete preference functionality (right-click menu)
        ContextMenu preferenceContextMenu = new ContextMenu();
        MenuItem deletePreferenceItem = new MenuItem("Remove");
        deletePreferenceItem.setOnAction(event -> {
            int selectedIndex = preferencesListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                preferencesList.remove(selectedIndex);
            }
        });
        preferenceContextMenu.getItems().add(deletePreferenceItem);
        preferencesListView.setContextMenu(preferenceContextMenu);

        // Configure Cancel button
        cancelButton.setOnAction(event -> {
            SimpleClient.getClient().navigateTo("menuView");
        });

        // Configure Save button
        saveButton.setOnAction(event -> {
            if (validateForm()) {
                saveDish();
            }
        });
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String ingredients = ingredientsArea.getText().trim();
        String price = priceField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Validation Error", "Please enter a dish name.");
            return false;
        }

        if (ingredients.isEmpty()) {
            showAlert("Validation Error", "Please enter ingredients.");
            return false;
        }

        if (price.isEmpty()) {
            showAlert("Validation Error", "Please enter a price.");
            return false;
        }

        try {
            double priceValue = Double.parseDouble(price);
            if (priceValue <= 0) {
                showAlert("Validation Error", "Price must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price must be a valid number.");
            return false;
        }

        if (restaurantList.isEmpty()) {
            showAlert("Validation Error", "Please add at least one restaurant.");
            return false;
        }

        return true;
    }

    private void saveDish() {
        try {
            // Create a new dish DTO
            dishDTO newDish = new dishDTO();

            // We don't set the ID as it will be generated by the server

            // Set the basic properties
            newDish.setName(nameField.getText().trim());
            newDish.setIngredients(ingredientsArea.getText().trim());
            newDish.setPrice(priceField.getText().trim());
            newDish.setDeliveryAvailable(deliveryCheckbox.isSelected());
            newDish.setImageUrl(imageUrlField.getText().trim());

            // Set preferences and restaurants
            newDish.setAvailablePreferences(new ArrayList<>(preferencesList));
            newDish.setRestaurantNames(new ArrayList<>(restaurantList));

            // Send to server
            responseDTO request = new responseDTO("addDish", new Object[]{newDish});
            SimpleClient.getClient().sendToServer(request);



        } catch (Exception e) {
            System.err.println("Error adding dish: " + e.getMessage());
            showAlert("Error", "Failed to add dish. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Subscribe
    public void handleDishAdded(String message) {
        if (message.startsWith("dishAdded:")) {
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Dish has been added successfully!");
                alert.showAndWait();

                // Navigate after the alert
                SimpleClient.getClient().navigateTo("menuView");
            });
        }
    }



    @Subscribe
    public void handleDishAddError(String message) {
        if (message.startsWith("dishAddError:")) {
            String errorMsg = message.substring("dishAddError:".length());
            showAlert("Error", "Failed to add dish: " + errorMsg);
        }
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }
}