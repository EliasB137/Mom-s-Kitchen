package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuDish;
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

public class menuController {

    @FXML
    private TableView<Dish> menuTableView;

    @FXML
    private TableColumn<Dish, String> nameColumn;

    @FXML
    private TableColumn<Dish, String> ingredientsColumn;

    @FXML
    private TableColumn<Dish, String> preferenceColumn;

    @FXML
    private TableColumn<Dish, String> priceColumn;

    @FXML
    private TableColumn<Dish, Boolean> deliveryColumn;

    @FXML
    private ImageView dishImageView;

    @FXML
    private ChoiceBox<String> searchCategoryChoiceBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Button backButton;

    private ObservableList<Dish> dishList = FXCollections.observableArrayList();

    public menuController() {
        EventBus.getDefault().register(this); // Subscribe to EventBus
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        ingredientsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIngredients()));
        preferenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPersonalPreference()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));
        deliveryColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDeliveryAvailable()));

        menuTableView.setItems(dishList);
        menuTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> showDishImage(newSelection)
        );

        searchCategoryChoiceBox.getItems().addAll("All", "Restaurant", "Ingredient", "Branch");
        searchCategoryChoiceBox.setValue("All");

        searchButton.setOnAction(event -> searchDishes());
        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));

        requestMenuData();
    }

    private void requestMenuData() {
        try {
            SimpleClient.getClient().sendToServer("getMenu");
        }
        catch (Exception e) {}
    }

    @Subscribe
    public void loadDishes(MenuResponse event) {
        if (event.getDishes() == null || event.getDishes().isEmpty()) {
            System.out.println("No dishes available.");
            return;
        }

        System.out.println("Received " + event.getDishes().size() + " dishes from server.");
        dishList.setAll(event.getDishes());
        SimpleClient.setMenuDishes(event.getMenuDishes()); // Store MenuDish for searching
        menuTableView.refresh();
    }


    private void showDishImage(Dish dish) {
        if (dish != null && dish.getImageUrl() != null) {
            dishImageView.setImage(new Image(dish.getImageUrl()));
        }
    }

    private void searchDishes() {
        String category = searchCategoryChoiceBox.getValue();
        String searchQuery = searchTextField.getText().toLowerCase();

        if (!searchQuery.isEmpty()) {
            List<Dish> filteredDishes = dishList.stream()
                    .filter(dish -> {
                        if (category.equals("All")) {
                            return dish.getName().toLowerCase().contains(searchQuery) ||
                                    dish.getIngredients().toLowerCase().contains(searchQuery);
                        } else if (category.equals("Restaurant")) {
                            System.out.println("[DEBUG] Checking dish: " + dish.getName());

                            // Fetch associated restaurants from MenuDish
                            List<MenuDish> menuDishes = SimpleClient.getClient().getMenuDishes();
                            for (MenuDish md : menuDishes) {
                                if (md.getDish().equals(dish) &&
                                        md.getRestaurant().getName().toLowerCase().contains(searchQuery)) {
                                    return true;
                                }
                            }
                        } else if (category.equals("Ingredient")) {
                            return dish.getIngredients().toLowerCase().contains(searchQuery);
                        } else if (category.equals("Branch")) {
                            System.out.println("[DEBUG] Searching Branch for " + dish.getName());

                            // Fetch branch data
                            List<MenuDish> menuDishes = SimpleClient.getClient().getMenuDishes();
                            for (MenuDish md : menuDishes) {
                                if (md.getDish().equals(dish) &&
                                        md.getRestaurant().getLocation().toLowerCase().contains(searchQuery)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            menuTableView.setItems(FXCollections.observableArrayList(filteredDishes));
        }
        else {
            initialize();
        }
    }


}
