package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;

import java.util.List;

public class CartItem {
    private dishDTO dish;
    private List<String> selectedPreferences;
    private int quantity;

    public CartItem(dishDTO dish, List<String> selectedPreferences, int quantity) {
        this.dish = dish;
        this.selectedPreferences = selectedPreferences;
        this.quantity = quantity;
    }

    public dishDTO getDish() {
        return dish;
    }

    public List<String> getSelectedPreferences() {
        return selectedPreferences;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        try {
            return Double.parseDouble(dish.getPrice()) * quantity;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

