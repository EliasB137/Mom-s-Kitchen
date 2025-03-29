package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.MenuItemDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.Restaurant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SimpleClient extends AbstractClient {

	public static SimpleClient client = null;
	public static int PORT = 3000;
	public static String IP = "localhost";

	//keep track of the selected restaurant
	private static String selectedRestaurant;
	private static List<CartItem> cart = new ArrayList<>();

	//keep track of the selected dish
	private static dishDTO selectedDish;

//	public static void addToCart(dishDTO dish, List<String> preferences, int quantity) {
//		CartItem item = new CartItem(dish, preferences, quantity);
//		cart.add(item);
//		System.out.println("[DEBUG] Added to cart: " + item.toString());
//	}
//
//	public static List<CartItem> getCart() {
//		return cart;
//	}
//
//	public static void removeFromCart(CartItem item) {
//		cart.remove(item);
//		System.out.println("[DEBUG] Removed from cart: " + item.toString());
//	}
//
//	public static void clearCart() {
//		cart.clear();
//		System.out.println("[DEBUG] Cart cleared.");
//	}


	private SimpleClient(String host, int port) {
		super(host, port);
	}
	public static void setSelectedDish(dishDTO dish) {
		selectedDish = dish;
	}
	public static dishDTO getSelectedDish() {
		return selectedDish;
	}
	public static void setSelectedRestaurant(String restaurant) {
		selectedRestaurant = restaurant;;
	}

	public static String getSelectedRestaurant() {
		return selectedRestaurant;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("the message was recieved");
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
		else if (msg instanceof responseDTO) {
			System.out.println("the message is responeDTO type");
			responseDTO response = (responseDTO) msg;
			String message = response.getMessage();

			if(message.equals("MenuResponse"))
			{
				System.out.println("Client received `MenuResponse` from server.");
				List<dishDTO> dishes = (List<dishDTO>)response.getPayload()[0];
				EventBus.getDefault().post(dishes);  // Post to EventBus
			} else if (message.equals("MenuForRestaurant")) {
				System.out.println("Client received `menu` from server.");
				List<MenuItemDTO> dishes = (List<MenuItemDTO>)response.getPayload()[0];
				EventBus.getDefault().post(dishes);  // Post to EventBus
			}
		}
		else
		{
			System.out.println("ERROR: Unknown message type received: " + msg.getClass().getName());
			System.out.println("Client received raw message: " + msg.toString());
		}

	}
	private static List<MenuItemDTO> menuDishes = new ArrayList<>();

	public static void setMenuDishes(List<MenuItemDTO> dishes) {
		menuDishes = dishes;
	}

	public static List<MenuItemDTO> getMenuDishes() {
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
