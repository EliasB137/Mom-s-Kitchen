package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
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
    private String selectedRestaurant; // Stores the selected restaurant

    public orderFoodController() {
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
        if(searchCategoryChoiceBox.getItems().isEmpty()) {
            searchCategoryChoiceBox.getItems().addAll("All", "Restaurant", "Ingredient", "Branch");
            searchCategoryChoiceBox.setValue("All");
        }
        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));
        searchButton.setOnAction(event -> searchDishes());


        // Detect double-click to open dishView
        menuTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
                if (selectedDish != null) {
                    System.out.println("Navigating to dishView with dish: " + selectedDish.getName());
                    SimpleClient.setSelectedDish(selectedDish);
                    SimpleClient.getClient().navigateTo("dishView");
                }
            }
        });

        requestMenuData();
    }

    public void setSelectedRestaurant(String restaurant) {
        this.selectedRestaurant = restaurant; // Store the selected restaurant name
        requestMenuData();
    }

    private void requestMenuData() {
        String selectedRestaurant = SimpleClient.getSelectedRestaurant();

        if (selectedRestaurant == null) {
            System.err.println(" [ERROR] No restaurant selected! Cannot fetch menu.");
            return;
        }

        try {
            System.out.println(" Requesting menu for restaurant: " + selectedRestaurant);
            SimpleClient.getClient().sendToServer("getMenuForRestaurant:" + selectedRestaurant);
        } catch (Exception e) {
            System.err.println(" ERROR: Could not request menu for restaurant.");
            e.printStackTrace();
        }
    }


    @Subscribe
    public void loadDishes(MenuResponse event) {
        System.out.println("[DEBUG] Received `MenuResponse` from server.");

        if (event.getDishes() == null || event.getDishes().isEmpty()) {
            System.out.println("[ERROR] No dishes available in `MenuResponse`.");
            return;
        }

        System.out.println("[DEBUG] Total Dishes Received: " + event.getDishes().size());
        for (Dish dish : event.getDishes()) {
            System.out.println("Dish: " + dish.getName());
        }

        dishList.setAll(event.getDishes());
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

        List<Dish> filteredDishes = dishList.stream()
                .filter(dish -> {
                    if (category.equals("All")) {
                        return dish.getName().toLowerCase().contains(searchQuery) ||
                                dish.getIngredients().toLowerCase().contains(searchQuery);
                    } else if (category.equals("Ingredient")) {
                        return dish.getIngredients().toLowerCase().contains(searchQuery);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        menuTableView.setItems(FXCollections.observableArrayList(filteredDishes));
    }
}
