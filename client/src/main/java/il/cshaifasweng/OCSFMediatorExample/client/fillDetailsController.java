package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.CartItem;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.OrderSubmissionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

public class fillDetailsController {
    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private TextField addressField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourCombo;
    @FXML private ComboBox<String> minuteCombo;
    @FXML private TextField creditCardField;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Setup time selection ComboBoxes
        setupTimeComboBoxes();

        // Set default values
        datePicker.setValue(LocalDate.now().plusDays(1)); // Tomorrow by default

        confirmButton.setOnAction(event -> submitOrder());
        cancelButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));
    }

    private void setupTimeComboBoxes() {
        // Populate hours (8 AM to 8 PM)
        for (int hour = 8; hour <= 20; hour++) {
            hourCombo.getItems().add(String.format("%02d", hour));
        }
        hourCombo.setValue("12"); // Default to noon

        // Populate minutes (0, 15, 30, 45)
        for (int minute = 0; minute < 60; minute += 15) {
            minuteCombo.getItems().add(String.format("%02d", minute));
        }
        minuteCombo.setValue("00"); // Default to 00 minutes
    }

    private void submitOrder() {
        String name = nameField.getText();
        String id = idField.getText();
        String address = addressField.getText();
        String creditCard = creditCardField.getText();

        // Validate basic inputs
        if (name.isEmpty() || id.isEmpty() || address.isEmpty() || creditCard.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // Validate date picker
        if (datePicker.getValue() == null) {
            errorLabel.setText("Please select a delivery date.");
            return;
        }

        // Get date and time values
        LocalDate selectedDate = datePicker.getValue();
        int hour = Integer.parseInt(hourCombo.getValue());
        int minute = Integer.parseInt(minuteCombo.getValue());

        // Ensure date is not in the past
        if (selectedDate.isBefore(LocalDate.now())) {
            errorLabel.setText("Please select a future date for delivery.");
            return;
        }

        // Create LocalDateTime from selected date and time
        LocalDateTime deliveryTime = LocalDateTime.of(selectedDate, LocalTime.of(hour, minute));

        // Create the order with cart items
        List<CartItem> cart = CartStore.getItems();

        if (cart.isEmpty()) {
            errorLabel.setText("Your cart is empty. Please add items before checkout.");
            return;
        }

        OrderSubmissionDTO orderData = new OrderSubmissionDTO(name, id, address, deliveryTime, creditCard, cart);
        try {
            SimpleClient.getClient().sendToServer(orderData);
            CartStore.clearCart();  // Clear the cart after submission
            SimpleClient.getClient().navigateTo("customerHomeView");  // Navigate back
            System.out.println("[DEBUG] Order submitted and cart cleared. Navigating home.");
        } catch (IOException e) {
            errorLabel.setText("Error submitting order. Please try again.");
            e.printStackTrace();
        }
    }
}