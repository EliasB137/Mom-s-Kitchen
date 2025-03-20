package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class cartController {

    @FXML private TableView<Dish> cartTable;
    @FXML private TableColumn<Dish, String> nameColumn;
    @FXML private TableColumn<Dish, String> priceColumn;
    @FXML private TableColumn<Dish, Void> removeColumn; //Column for remove buttons
    @FXML private Button checkoutButton;
    @FXML private Button clearCartButton;
    @FXML private Button backButton;
    @FXML private Label totalPriceLabel;
    private ObservableList<Dish> cartList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrice()));

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
                    Dish dish = getTableView().getItems().get(getIndex());
                    handleRemoveDish(dish);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });
    }

    private void handleRemoveDish(Dish dish) {
        SimpleClient.removeFromCart(dish);
        updateTotalPrice();
        cartList.setAll(SimpleClient.getCart());
        System.out.println("[DEBUG] Removed from cart: " + dish.getName());
    }

    private void handleClearCart() {
        SimpleClient.clearCart();
        cartList.clear();
        updateTotalPrice();
        System.out.println("[DEBUG] Cart cleared.");
    }
    private void updateTotalPrice() {
        double total = SimpleClient.getCart().stream()
                .mapToDouble(dish -> Double.parseDouble(dish.getPrice()))
                .sum();
        totalPriceLabel.setText("Total: $" + String.format("%.2f", total)); // ✅ Display total price
    }
    private void handleCheckout() {

    }
}
