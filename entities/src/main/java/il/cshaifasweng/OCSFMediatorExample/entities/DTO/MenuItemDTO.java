
package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;
import java.util.List;

public class MenuItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private dishDTO dish;
    private String restaurantName;

    public MenuItemDTO()
    {}
    public MenuItemDTO(dishDTO dish, String restaurantName) {
        this.dish = dish;
        this.restaurantName = restaurantName;
    }

    public dishDTO getDish() {
        return dish;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
}
