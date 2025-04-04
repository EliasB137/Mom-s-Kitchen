package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class menuController {

    @FXML private TableView<dishDTO> menuTableView;
    @FXML private TableColumn<dishDTO, String> nameColumn;
    @FXML private TableColumn<dishDTO, String> ingredientsColumn;
    @FXML private TableColumn<dishDTO, String> priceColumn;
    @FXML private TableColumn<dishDTO, Boolean> deliveryColumn;
    @FXML private TableColumn<dishDTO, Void> actionsColumn;

    @FXML private ImageView dishImageView;

    @FXML private ChoiceBox<String> searchCategoryChoiceBox;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private Button backButton;
    @FXML private Button addDishButton;

    private ObservableList<dishDTO> dishList = FXCollections.observableArrayList();

    public menuController() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        ingredientsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIngredients()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));
        deliveryColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDeliveryAvailable()));

        menuTableView.setItems(dishList);
        menuTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> showDishImage(newSelection)
        );

        if (searchCategoryChoiceBox.getItems().isEmpty()) {
            searchCategoryChoiceBox.getItems().addAll("All", "Ingredient","Restaurant");
            searchCategoryChoiceBox.setValue("All");
        }

        searchButton.setOnAction(event -> searchDishes());
        backButton.setOnAction(event -> {
            String role = SimpleClient.getUserRole();
            if ("dietitian".equals(role)) {
                SimpleClient.getClient().navigateTo("adminHomeView");
            } else {
                SimpleClient.getClient().navigateTo("customerHomeView");
            }
        });

        // Set up actions for dietitian role
        if ("dietitian".equals(SimpleClient.getUserRole())) {
            System.out.println(SimpleClient.getUserRole());

            // Add actions column for dietitian
            setupActionsColumn();

            // Show and configure Add Dish button for dietitian
            if (addDishButton != null) {
                addDishButton.setVisible(true);
                addDishButton.setOnAction(event -> {
                    SimpleClient.getClient().navigateTo("addDishView");
                });
            }

            // Keep the double-click functionality
            menuTableView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    dishDTO selectedItem = menuTableView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        SimpleClient.setSelectedDish(selectedItem);
                        SimpleClient.getClient().navigateTo("dishView");
                    }
                }
            });
        }

        requestMenuData();
    }

    private void setupActionsColumn() {
        // Only show actions column for dietitian role
        if (actionsColumn == null) {
            actionsColumn = new TableColumn<>("Actions");
        }

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button editButton = new Button("Edit");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                buttonBox.setAlignment(Pos.CENTER);

                deleteButton.setOnAction(event -> {
                    dishDTO dish = getTableView().getItems().get(getIndex());
                    handleDeleteDish(dish);
                });

                editButton.setOnAction(event -> {
                    dishDTO dish = getTableView().getItems().get(getIndex());
                    SimpleClient.setSelectedDish(dish);
                    SimpleClient.getClient().navigateTo("dishView");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });

        // Add the actions column if it's not already there
        if (!menuTableView.getColumns().contains(actionsColumn)) {
            menuTableView.getColumns().add(actionsColumn);
        }
    }

    private void handleDeleteDish(dishDTO dish) {
        if (dish == null) return;

        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Dish");
        confirmation.setHeaderText("Delete " + dish.getName());
        confirmation.setContentText("Are you sure you want to delete this dish? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Send delete request to server
                SimpleClient.getClient().sendToServer("deleteDish " + dish.getId());

                // Optimistically remove from the local list
                dishList.remove(dish);
                menuTableView.refresh();

                // Show success message
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Dish Deleted");
                success.setHeaderText(null);
                success.setContentText("The dish '" + dish.getName() + "' has been successfully deleted.");
                success.showAndWait();
            } catch (Exception e) {
                System.err.println("Error deleting dish: " + e.getMessage());

                // Show error message
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Failed to Delete Dish");
                error.setContentText("An error occurred while trying to delete the dish. Please try again later.");
                error.showAndWait();
            }
        }
    }

    private void requestMenuData() {
        try {
            SimpleClient.getClient().sendToServer("getMenu");
        } catch (Exception e) {
            System.err.println("Error requesting menu data: " + e.getMessage());
        }
    }

    @Subscribe
    public void loadDishes(List<dishDTO> event) {
        if (event == null || event.isEmpty()) {
            System.out.println("No dishes available.");
            return;
        }

        System.out.println("Received " + event.size() + " dishes from server.");
        dishList.setAll(event);
        menuTableView.refresh();
    }

    @Subscribe
    public void handleDishDeleted(String message) {
        if (message.startsWith("dishDeleted:")) {
            // Refresh the menu after deletion
            requestMenuData();
        }
    }

    @Subscribe
    public void handleDishAdded(String message) {
        if (message.startsWith("dishAdded:") || message.startsWith("dishUpdated:")) {
            // Refresh the menu after a dish is added or updated
            requestMenuData();
        }
    }

    private void showDishImage(dishDTO dish) {
        if (dish != null && dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
            dishImageView.setImage(new Image(dish.getImageUrl()));
        }
    }

    private void searchDishes() {
        String category = searchCategoryChoiceBox.getValue();
        String searchQuery = searchTextField.getText().toLowerCase();

        if (!searchQuery.isEmpty()) {
            List<dishDTO> filtered = dishList.stream()
                    .filter(dish -> {
                        if (category.equals("All")) {
                            return dish.getName().toLowerCase().contains(searchQuery) ||
                                    dish.getIngredients().toLowerCase().contains(searchQuery);
                        } else if (category.equals("Ingredient")) {
                            return dish.getIngredients().toLowerCase().contains(searchQuery);
                        }
                        else if (category.equals("Restaurant")) {
                            return dish.getRestaurantNames().stream()
                                    .anyMatch(name -> name.toLowerCase().contains(searchQuery));
                        }

                        return false;
                    })
                    .collect(Collectors.toList());

            menuTableView.setItems(FXCollections.observableArrayList(filtered));
        } else {
            menuTableView.setItems(dishList); // restore full list
        }
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }
}