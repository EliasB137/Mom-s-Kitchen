package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class cartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> nameColumn;
    @FXML private TableColumn<CartItem, String> preferencesColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, String> totalPriceColumn;
    @FXML private TableColumn<CartItem, Void> removeColumn;

    @FXML private Button checkoutButton;
    @FXML private Button clearCartButton;
    @FXML private Button backButton;

    @FXML private Label totalPriceLabel;

    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set column cell value factories
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDish().getName()));

        preferencesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.join(", ", cellData.getValue().getPreferences())));

        quantityColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        totalPriceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f", cellData.getValue().getTotalPrice())));

        cartList.setAll(SimpleClient.getCart());
        cartTable.setItems(cartList);

        addRemoveButtonToTable();
        updateTotalPrice();

        clearCartButton.setOnAction(event -> handleClearCart());
        checkoutButton.setOnAction(event -> handleCheckout());
        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));
    }

    private void addRemoveButtonToTable() {
        removeColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    handleRemoveItem(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });
    }

    private void handleRemoveItem(CartItem item) {
        SimpleClient.removeFromCart(item);
        cartList.setAll(SimpleClient.getCart());
        updateTotalPrice();
        System.out.println("[DEBUG] Removed from cart: " + item.getDish().getName());
    }

    private void handleClearCart() {
        SimpleClient.clearCart();
        cartList.clear();
        updateTotalPrice();
        System.out.println("[DEBUG] Cart cleared.");
    }

    private void updateTotalPrice() {
        double total = SimpleClient.getCart().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        totalPriceLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void handleCheckout() {
        System.out.println("Checkout pressed - TODO: implement order submission.");
        // Navigate to details form or confirmation page (not implemented here)
    }
}
