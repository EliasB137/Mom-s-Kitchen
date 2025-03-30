package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.OrderCancellationDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.OrderSummaryDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class viewOrdersController {

    @FXML private TextField idField;
    @FXML private Button searchButton;
    @FXML private TableView<OrderSummaryDTO> ordersTable;
    @FXML private TableColumn<OrderSummaryDTO, String> orderIdColumn;
    @FXML private TableColumn<OrderSummaryDTO, String> totalPriceColumn;
    @FXML private TableColumn<OrderSummaryDTO, String> deliveryTimeColumn;
    @FXML private TableColumn<OrderSummaryDTO, Void> actionColumn;
    @FXML private Label messageLabel;

    private ObservableList<OrderSummaryDTO> orderList = FXCollections.observableArrayList();

    public viewOrdersController() {
        EventBus.getDefault().register(this);
    }
    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getOrderId())));
        totalPriceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getTotalPrice())));
        deliveryTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeliveryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        ordersTable.setItems(orderList);
        addCancelButtonToTable();

        searchButton.setOnAction(event -> fetchOrdersById());
    }

    private void fetchOrdersById() {
        String id = idField.getText();
        if (id.isEmpty()) {
            messageLabel.setText("Please enter your ID.");
            return;
        }

        try {
            SimpleClient.getClient().sendToServer("getOrdersByCustomerId:" + id);
            messageLabel.setText("Loading orders...");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to request orders.");
        }
    }

    private void addCancelButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelButton = new Button("Cancel");

            {
                cancelButton.setOnAction(event -> {
                    OrderSummaryDTO order = getTableView().getItems().get(getIndex());
                    try {
                        OrderCancellationDTO cancelDTO = new OrderCancellationDTO(order.getOrderId(), idField.getText());
                        SimpleClient.getClient().sendToServer(cancelDTO);
                        messageLabel.setText("Processing cancellation...");
                    } catch (IOException e) {
                        messageLabel.setText("Cancellation failed.");
                        e.printStackTrace();
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cancelButton);
            }
        });
    }
    @Subscribe
    public void handleOrders(List<OrderSummaryDTO> event) {
        if (event == null || event.isEmpty()) {
            System.out.println("No dishes available.");
            return;
        }

        System.out.println("Received " + event.size() + " dishes from server.");
        orderList.setAll(event);
    }

    // Call this when receiving orders from the server
    public void displayOrders(List<OrderSummaryDTO> orders) {
        orderList.setAll(orders);
        messageLabel.setText("Found " + orders.size() + " orders.");
    }
    @FXML
    public void handleBack() {
        SimpleClient.getClient().navigateTo("customerHomeView");
    }
    public void displayMessage(String msg) {
        messageLabel.setText(msg);
    }
}
