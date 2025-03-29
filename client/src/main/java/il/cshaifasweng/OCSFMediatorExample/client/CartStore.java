package il.cshaifasweng.OCSFMediatorExample.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A singleton utility class to manage the shopping cart globally.
 */
public class CartStore {

    // Singleton cart list
    private static final List<CartItem> cart = new ArrayList<>();

    /**
     * Add a cart item.
     */
    public static void addItem(CartItem item) {
        if (item != null) {
            cart.add(item);
            System.out.println("[DEBUG] Added to CartStore: " + item);
        }
    }

    /**
     * Remove a cart item.
     */
    public static void removeItem(CartItem item) {
        if (cart.remove(item)) {
            System.out.println("[DEBUG] Removed from CartStore: " + item);
        }
    }

    /**
     * Clears all items from the cart.
     */
    public static void clearCart() {
        cart.clear();
        System.out.println("[DEBUG] CartStore cleared.");
    }

    /**
     * Get all cart items (read-only).
     */
    public static List<CartItem> getItems() {
        return Collections.unmodifiableList(cart);
    }

    /**
     * Get the total price of all items in the cart.
     */
    public static double getTotalPrice() {
        return cart.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    /**
     * Get total quantity of items in the cart.
     */
    public static int getTotalQuantity() {
        return cart.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * Debug helper to print all cart contents.
     */
    public static void debugPrintCart() {
        System.out.println("=== Cart Contents ===");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
        } else {
            cart.forEach(item -> System.out.println(item));
        }
        System.out.println("=====================");
    }
}
