package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import java.io.Serializable;
import java.util.List;

public class RestaurantListResponse implements Serializable {  // Ensure it's serializable
    private static final long serialVersionUID = 1L;
    private final List<Restaurant> Restaurants;

    public RestaurantListResponse(List<Restaurant> Restaurants) {
        this.Restaurants = Restaurants;
    }

    public List<Restaurant> getRestaurants() {
        return Restaurants;
    }
}
