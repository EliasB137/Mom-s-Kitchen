package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

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
		System.out.println("Message received from server.");

		if (msg instanceof responseDTO) {
			responseDTO response = (responseDTO) msg;
			String message = response.getMessage();

			switch (message) {
				case "MenuResponse":
				case "MenuForRestaurant":
					List<dishDTO> dishes = (List<dishDTO>) response.getPayload()[0];
					EventBus.getDefault().post(dishes);
					break;

				case "restaurants":
					List<restaurantDTO> restaurants = (List<restaurantDTO>) response.getPayload()[0];
					EventBus.getDefault().post(restaurants);
					break;

				case "CustomerOrdersResponse":
					List<OrderSummaryDTO> orderSummaries = (List<OrderSummaryDTO>) response.getPayload()[0];
					EventBus.getDefault().post(orderSummaries);
					break;

				case "OrderCancelled":
//					String cancelMsg = (String) response.getPayload()[0];
//					EventBus.getDefault().post(new WarningEvent(cancelMsg));
					break;

				default:
					System.out.println("[WARN] Unknown responseDTO message: " + message);
			}
		} else if (msg instanceof restaurantDTO) {
			EventBus.getDefault().post((restaurantDTO) msg);
		} else {
			System.err.println("Unknown message type: " + msg.getClass().getName());
			System.err.println("Raw message: " + msg.toString());
		}
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
