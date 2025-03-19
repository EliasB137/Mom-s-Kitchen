package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.SendMenu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class PrimaryController {

	@FXML
	private TableView<Dish> TableView;

	@FXML
	private TableColumn<Dish, String> nameColumn;

	@FXML
	private TableColumn<Dish, String> ingredientsColumn;

	@FXML
	private TableColumn<Dish, String> personalPrefrenceColumn;

	@FXML
	private TableColumn<Dish, String> priceColumn;

	private ObservableList<Dish> dishList = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		System.out.println("✅ PrimaryController Initialized!");
		EventBus.getDefault().register(this);  // ✅ Ensures loadDishes() gets triggered

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
		personalPrefrenceColumn.setCellValueFactory(new PropertyValueFactory<>("personalPreference"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		TableView.setItems(dishList);
	}


	private void handleRowClick(MouseEvent event) {
		if (event.getClickCount() == 2) { // Double-click
			Dish selectedDish = TableView.getSelectionModel().getSelectedItem();
			if (selectedDish != null) {
				System.out.println("🍽 Selected Dish: " + selectedDish.getName());
				EventBus.getDefault().post(selectedDish);
			} else {
				System.out.println("⚠️ No dish selected.");
			}
		}
	}

	@Subscribe
	public void loadDishes(MenuEvent event) {
		System.out.println("📥 EventBus triggQered loadDishes()!");

		if (event == null || event.getMenu() == null || event.getMenu().getArray() == null) {
			System.out.println("❌ ERROR: Invalid MenuEvent received!");
			return;
		}

		List<Dish> dishesFromServer = event.getMenu().getArray();
		System.out.println("📥 Received " + dishesFromServer.size() + " dishes from EventBus.");

		if (dishesFromServer.isEmpty()) {
			System.out.println("⚠️ WARNING: Dishes list is EMPTY!");
		} else {
			System.out.println("✅ Updating TableView with " + dishesFromServer.size() + " dishes...");
			dishList.setAll(dishesFromServer);
			System.out.println("🛠 TableView updated successfully!");
		}
	}

}
