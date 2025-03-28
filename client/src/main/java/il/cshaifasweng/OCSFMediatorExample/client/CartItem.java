package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;

import java.util.List;

public class CartItem {
    private dishDTO dish;
    private List<String> preferences;
    private int quantity;

    public CartItem(dishDTO dish, List<String> preferences, int quantity) {
        this.dish = dish;
        this.preferences = preferences;
        this.quantity = quantity;
    }

    public dishDTO getDish() {
        return dish;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        double price = Double.parseDouble(dish.getPrice());
        return price * quantity;
    }

    @Override
    public String toString() {
        return quantity + "x " + dish.getName() + " [" + String.join(", ", preferences) + "]";
    }
}
