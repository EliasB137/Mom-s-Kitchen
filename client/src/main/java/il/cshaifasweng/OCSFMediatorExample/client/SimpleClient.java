package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuDish;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SimpleClient extends AbstractClient {

	public static SimpleClient client = null;
	public static int PORT = 3000;
	public static String IP = "localhost";

	//keep track of the selected restaurant
	private static String selectedRestaurant;
	private static List<Dish> cart = new ArrayList<>(); //Store cart locally
	//keep track of the selected dish
	private static Dish selectedDish;

	public static void addToCart(Dish dish) {
		if (dish != null) {
			cart.add(dish);
			System.out.println("[DEBUG] Added to cart: " + dish.getName());
		}
	}

	public static void removeFromCart(Dish dish) {
		cart.remove(dish);
		System.out.println("[DEBUG] Removed from cart: " + dish.getName());
	}

	public static void clearCart() {
		cart.clear();
		System.out.println("[DEBUG] Cart cleared.");
	}

	public static List<Dish> getCart() {
		return cart;
	}

	private SimpleClient(String host, int port) {
		super(host, port);
	}
	public static void setSelectedDish(Dish dish) {
		selectedDish = dish;
	}
	public static Dish getSelectedDish() {
		return selectedDish;
	}
	public static void setSelectedRestaurant(String restaurant) {
		selectedRestaurant = restaurant;
		clearCart();
	}

	public static String getSelectedRestaurant() {
		return selectedRestaurant;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {

		if (msg instanceof RestaurantListResponse) {
			RestaurantListResponse response = (RestaurantListResponse) msg;
			List<Restaurant> Restaurants = response.getRestaurants();

			System.out.println("Received " + Restaurants.size() + " restaurants from server.");
			for (Restaurant restaurant : Restaurants) {
				System.out.println("Restaurant: " + restaurant.getName() + " - Location: " + restaurant.getLocation());
			}

			// Post event to update UI
			EventBus.getDefault().post(new RestaurantListResponse(Restaurants));

		}
		else if (msg instanceof MenuResponse) {
			System.out.println("Client received `MenuResponse` from server.");
			EventBus.getDefault().post((MenuResponse) msg);  // Post to EventBus
		}
		else
		{
			System.out.println("ERROR: Unknown message type received: " + msg.getClass().getName());
			System.out.println("Client received raw message: " + msg.toString());
		}

	}
	private static List<MenuDish> menuDishes = new ArrayList<>();

	public static void setMenuDishes(List<MenuDish> dishes) {
		menuDishes = dishes;
	}

	public static List<MenuDish> getMenuDishes() {
		return menuDishes;
	}

	public Object navigateTo(String fxmlFileName) {
		try {
			System.out.println("Navigating to " + fxmlFileName);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/" + fxmlFileName + ".fxml"));

			Parent root = loader.load();
			Object controller = loader.getController();

			if (controller == null) {
				System.err.println("ERROR: FXML file loaded but controller is NULL: " + fxmlFileName);
				return null;
			}

			Stage stage = App.getPrimaryStage();
			if (stage == null) {
				System.err.println("ERROR: Primary stage is NULL!");
				return null;
			}

			stage.setScene(new Scene(root));
			stage.show();

			return controller;  // Return the controller instance

		} catch (IOException e) {
			System.err.println("ERROR: Could not load FXML file " + fxmlFileName);
			e.printStackTrace();
			return null;
		}
	}





	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(IP, PORT);
		}
		return client;
	}
}
