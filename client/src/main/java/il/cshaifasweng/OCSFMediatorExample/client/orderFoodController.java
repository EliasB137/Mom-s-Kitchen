
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.stream.Collectors;

public class orderFoodController {

    @FXML private TableView<dishDTO> menuTableView;
    @FXML private TableColumn<dishDTO, String> nameColumn;
    @FXML private TableColumn<dishDTO, String> ingredientsColumn;
    @FXML private TableColumn<dishDTO, String> priceColumn;
    @FXML private TableColumn<dishDTO, Boolean> deliveryColumn;
    @FXML private ImageView dishImageView;
    @FXML private ChoiceBox<String> searchCategoryChoiceBox;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private Button backButton;
    @FXML private Button viewCartButton;

    private ObservableList<dishDTO> dishList = FXCollections.observableArrayList();

    public orderFoodController() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        ingredientsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIngredients()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));
        deliveryColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDeliveryAvailable()));

        menuTableView.setItems(dishList);

        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));
        viewCartButton.setOnAction(event -> {
            Object controller = SimpleClient.getClient().navigateTo("cartView");
            if (controller instanceof cartController) {
                ((cartController) controller).refreshCart();
            }
        });
        if (searchCategoryChoiceBox.getItems().isEmpty()) {
            searchCategoryChoiceBox.getItems().addAll("All", "Ingredient");
            searchCategoryChoiceBox.setValue("All");
        }

        searchButton.setOnAction(event -> searchDishes());

        menuTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                dishDTO selectedItem = menuTableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    SimpleClient.setSelectedDish(selectedItem); // You need to add this method
                    SimpleClient.getClient().navigateTo("dishView");
                }
            }
        });
    }

    public void setSelectedRestaurant(String restaurant) {
        SimpleClient.setSelectedRestaurant(restaurant);
        requestMenuData();
    }

    private void requestMenuData() {
        String selectedRestaurant = SimpleClient.getSelectedRestaurant();
        if (selectedRestaurant == null) {
            System.err.println("[ERROR] No restaurant selected!");
            return;
        }

        try {
            System.out.println("Requesting menu for restaurant: " + selectedRestaurant);
            SimpleClient.getClient().sendToServer("getMenuForRestaurant:" + selectedRestaurant);
        } catch (Exception e) {
            System.err.println("Failed to request menu.");
            e.printStackTrace();
        }
    }

    @Subscribe
    public void loadMenu(List<dishDTO> menuItems) {
        System.out.println("[DEBUG] Received dishDTO list from EventBus: " + menuItems.size());

        if (menuItems == null || menuItems.isEmpty()) {
            System.out.println("[ERROR] No menu items received!");
            return;
        }

        String selectedRestaurant = SimpleClient.getSelectedRestaurant();
        List<dishDTO> filteredDishes = menuItems.stream()
                .filter(dish ->
                        dish.isDeliveryAvailable() &&
                                dish.getRestaurantNames().stream()
                                        .anyMatch(name -> name.equalsIgnoreCase("all") || name.equalsIgnoreCase(selectedRestaurant))
                )
                .collect(Collectors.toList());

        dishList.setAll(filteredDishes);
        menuTableView.setItems(dishList);
        menuTableView.refresh();
    }





    private void showDishImage(dishDTO dish) {
        if (dish != null && dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
            dishImageView.setImage(new Image(dish.getImageUrl()));
        } else {
            dishImageView.setImage(null);
        }
    }

    private void searchDishes() {
        String category = searchCategoryChoiceBox.getValue();
        String query = searchTextField.getText().toLowerCase();

        if (query.isEmpty()) {
            menuTableView.setItems(dishList);
            return;
        }

        List<dishDTO> filtered = dishList.stream().filter(dish -> {
            if (category.equals("All")) {
                return dish.getName().toLowerCase().contains(query) ||
                        dish.getIngredients().toLowerCase().contains(query);
            } else if (category.equals("Ingredient")) {
                return dish.getIngredients().toLowerCase().contains(query);
            }
            return false;
        }).collect(Collectors.toList());

        menuTableView.setItems(FXCollections.observableArrayList(filtered));
    }
}
