//package il.cshaifasweng.OCSFMediatorExample.client;
//
//import il.cshaifasweng.OCSFMediatorExample.entities.SavingInSql.Dish;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class orderFoodController {
//
//    @FXML
//    private TableView<Dish> menuTableView;
//
//    @FXML
//    private TableColumn<Dish, String> nameColumn;
//
//    @FXML
//    private TableColumn<Dish, String> ingredientsColumn;
//
//    @FXML
//    private TableColumn<Dish, String> preferenceColumn;
//
//    @FXML
//    private TableColumn<Dish, String> priceColumn;
//
//    @FXML
//    private TableColumn<Dish, Boolean> deliveryColumn;
//
//    @FXML
//    private ImageView dishImageView;
//
//    @FXML
//    private ChoiceBox<String> searchCategoryChoiceBox;
//
//    @FXML
//    private TextField searchTextField;
//
//    @FXML
//    private Button searchButton;
//
//    @FXML
//    private Button backButton;
//
//    @FXML
//    private Button viewCartButton;
//
//    private ObservableList<Dish> dishList = FXCollections.observableArrayList();
//    private String selectedRestaurant; // Stores the selected restaurant
//
//    public orderFoodController() {
//        EventBus.getDefault().register(this); // Subscribe to EventBus
//    }
//
//    @FXML
//    public void initialize() {
//        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
//        ingredientsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIngredients()));
//        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));
//        deliveryColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDeliveryAvailable()));
//
//        menuTableView.setItems(dishList);
//        if(searchCategoryChoiceBox.getItems().isEmpty()) {
//            searchCategoryChoiceBox.getItems().addAll("All", "Restaurant", "Ingredient", "Branch");
//            searchCategoryChoiceBox.setValue("All");
//        }
//        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));
//        searchButton.setOnAction(event -> searchDishes());
//        viewCartButton.setOnAction(event -> SimpleClient.getClient().navigateTo("cartView"));
//
//        // Detect double-click to open dishView
//        menuTableView.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                Dish selectedDish = menuTableView.getSelectionModel().getSelectedItem();
//                if (selectedDish != null) {
//                    System.out.println("Navigating to dishView with dish: " + selectedDish.getName());
//                    SimpleClient.setSelectedDish(selectedDish);
//                    SimpleClient.getClient().navigateTo("dishView");
//                }
//            }
//        });
//
//        requestMenuData();
//    }
//
//    public void setSelectedRestaurant(String restaurant) {
//        this.selectedRestaurant = restaurant; // Store the selected restaurant name
//        requestMenuData();
//    }
//
//    private void requestMenuData() {
//        String selectedRestaurant = SimpleClient.getSelectedRestaurant();
//
//        if (selectedRestaurant == null) {
//            System.err.println(" [ERROR] No restaurant selected! Cannot fetch menu.");
//            return;
//        }
//
//        try {
//            System.out.println(" Requesting menu for restaurant: " + selectedRestaurant);
//            SimpleClient.getClient().sendToServer("getMenuForRestaurant:" + selectedRestaurant);
//        } catch (Exception e) {
//            System.err.println(" ERROR: Could not request menu for restaurant.");
//            e.printStackTrace();
//        }
//    }
//
//
//    @Subscribe
//    public void loadDishes(MenuResponse event) {
//        System.out.println("[DEBUG] Received `MenuResponse` from server.");
//
//        if (event.getDishes() == null || event.getDishes().isEmpty()) {
//            System.out.println("[ERROR] No dishes available in `MenuResponse`.");
//            return;
//        }
//
//        System.out.println("[DEBUG] Total Dishes Received: " + event.getDishes().size());
//        for (Dish dish : event.getDishes()) {
//            System.out.println("Dish: " + dish.getName());
//        }
//
//        dishList.setAll(event.getDishes());
//        menuTableView.refresh();
//    }
//
//
//
//    private void showDishImage(Dish dish) {
//        if (dish != null && dish.getImageUrl() != null) {
//            dishImageView.setImage(new Image(dish.getImageUrl()));
//        }
//    }
//
//    private void searchDishes() {
//        String category = searchCategoryChoiceBox.getValue();
//        String searchQuery = searchTextField.getText().toLowerCase();
//        if (!searchQuery.isEmpty()) {
//            List<Dish> filteredDishes = dishList.stream()
//                    .filter(dish -> {
//                        if (category.equals("All")) {
//                            return dish.getName().toLowerCase().contains(searchQuery) ||
//                                    dish.getIngredients().toLowerCase().contains(searchQuery);
//                        } else if (category.equals("Ingredient")) {
//                            return dish.getIngredients().toLowerCase().contains(searchQuery);
//                        }
//                        return false;
//                    })
//                    .collect(Collectors.toList());
//
//            menuTableView.setItems(FXCollections.observableArrayList(filteredDishes));
//        }
//        else {
//            initialize();
//        }
//    }
//}
//package il.cshaifasweng.OCSFMediatorExample.client;
//
//import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
//import il.cshaifasweng.OCSFMediatorExample.entities.DTO.MenuItemDTO;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class orderFoodController {
//
//    @FXML private TableView<dishDTO> menuTableView;
//    @FXML private TableColumn<dishDTO, String> nameColumn;
//    @FXML private TableColumn<dishDTO, String> ingredientsColumn;
//    @FXML private TableColumn<dishDTO, String> priceColumn;
//    @FXML private TableColumn<dishDTO, Boolean> deliveryColumn;
//    @FXML private ImageView dishImageView;
//    @FXML private ChoiceBox<String> searchCategoryChoiceBox;
//    @FXML private TextField searchTextField;
//    @FXML private Button searchButton;
//    @FXML private Button backButton;
//    @FXML private Button viewCartButton;
//
//    private ObservableList<dishDTO> dishList = FXCollections.observableArrayList();
//
//    public orderFoodController() {
//        EventBus.getDefault().register(this);
//    }
//
//    @FXML
//    public void initialize() {
//        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
//        ingredientsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIngredients()));
//        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrice()));
//        deliveryColumn.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isDeliveryAvailable()));
//
//        menuTableView.setItems(dishList);
//
//        menuTableView.getSelectionModel().selectedItemProperty().addListener(
//                (obs, oldSelection, newSelection) -> showDishImage(newSelection)
//        );
//
//        if (searchCategoryChoiceBox.getItems().isEmpty()) {
//            searchCategoryChoiceBox.getItems().addAll("All", "Ingredient");
//            searchCategoryChoiceBox.setValue("All");
//        }
//
//        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));
//        viewCartButton.setOnAction(event -> SimpleClient.getClient().navigateTo("cartView"));
//        searchButton.setOnAction(event -> searchDishes());
//        menuTableView.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                dishDTO selectedDish = menuTableView.getSelectionModel().getSelectedItem();
//                if (selectedDish != null) {
//                    System.out.println("Navigating to dishView with dish: " + selectedDish.getName());
//                    SimpleClient.setSelectedDish(selectedDish);
//                    SimpleClient.getClient().navigateTo("dishView");
//                }
//            }
//        });
//
//        requestMenuData();
//    }
//
//    public void setSelectedRestaurant(String restaurant) {
//        SimpleClient.setSelectedRestaurant(restaurant);
//        requestMenuData();
//    }
//
//    private void requestMenuData() {
//        String selectedRestaurant = SimpleClient.getSelectedRestaurant();
//        if (selectedRestaurant == null) {
//            System.err.println("[ERROR] No restaurant selected!");
//            return;
//        }
//
//        try {
//            System.out.println("Requesting menu for restaurant: " + selectedRestaurant);
//            SimpleClient.getClient().sendToServer("getMenuForRestaurant:" + selectedRestaurant);
//        } catch (Exception e) {
//            System.err.println("Failed to request menu.");
//            e.printStackTrace();
//        }
//    }
//
//    @Subscribe
//    public void loadDishes(MenuResponse event) {
//        if (event.getDishes() == null || event.getDishes().isEmpty()) {
//            System.out.println("[ERROR] No dishes in response.");
//            return;
//        }
//
//        System.out.println("Loaded " + event.getDishes().size() + " dishes.");
//        dishList.setAll(event.getDishes());
//        menuTableView.refresh();
//
//        if (event.getMenuDishes() != null) {
//            SimpleClient.setMenuDishes(event.getMenuDishes());
//        }
//    }
//
//    private void showDishImage(dishDTO dish) {
//        if (dish != null && dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
//            dishImageView.setImage(new Image(dish.getImageUrl()));
//        } else {
//            dishImageView.setImage(null);
//        }
//    }
//
//    private void searchDishes() {
//        String category = searchCategoryChoiceBox.getValue();
//        String query = searchTextField.getText().toLowerCase();
//
//        if (query.isEmpty()) {
//            menuTableView.setItems(dishList);
//            return;
//        }
//
//        List<dishDTO> filtered = dishList.stream().filter(dish -> {
//            if (category.equals("All")) {
//                return dish.getName().toLowerCase().contains(query) ||
//                        dish.getIngredients().toLowerCase().contains(query);
//            } else if (category.equals("Ingredient")) {
//                return dish.getIngredients().toLowerCase().contains(query);
//            }
//            return false;
//        }).collect(Collectors.toList());
//
//        menuTableView.setItems(FXCollections.observableArrayList(filtered));
//    }
//}
