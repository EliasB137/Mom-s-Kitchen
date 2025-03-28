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

public class menuController {

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
            searchCategoryChoiceBox.getItems().addAll("All", "Ingredient");
            searchCategoryChoiceBox.setValue("All");
        }

        searchButton.setOnAction(event -> searchDishes());
        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("customerHomeView"));

        requestMenuData();
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
            System.out.println("⚠️ No dishes available.");
            return;
        }

        System.out.println("✅ Received " + event.size() + " dishes from server.");
        dishList.setAll(event);
        menuTableView.refresh();
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
                        return false;
                    })
                    .collect(Collectors.toList());

            menuTableView.setItems(FXCollections.observableArrayList(filtered));
        } else {
            menuTableView.setItems(dishList); // restore full list
        }
    }
}
