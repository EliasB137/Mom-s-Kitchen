package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.SavingInSql.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.restaurantDTO;
import il.cshaifasweng.OCSFMediatorExample.server.SavingInSql.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DTOConverter implements Serializable {
    private static final long serialVersionUID = 1L;
/// ////////////////////Dish converter ///////////////////////////
public static dishDTO convertToDishDTO(Dish dish) {
    if (dish == null) return null;

    List<String> restaurantNames = dish.getRestaurantNames() != null
            ? new ArrayList<>(dish.getRestaurantNames())
            : new ArrayList<>();

    return new dishDTO(
            dish.getId(),
            dish.getName(),
            dish.getIngredients(),
            new ArrayList<>(dish.getAvailablePreferences()),
            dish.getPrice(),
            dish.getImageUrl(),
            dish.isDeliveryAvailable(),
            restaurantNames
    );
}


    public static List<dishDTO> convertToDishDTOList(List<Dish> dishes) {
        return dishes.stream()
                .map(DTOConverter::convertToDishDTO)
                .collect(Collectors.toList());
    }

    /// //////////////////////////////////////////////////////////
/// ///////////////////Restaurant converter///////////////////////
    public static restaurantDTO convertToRestaurantDTO(Restaurant restaurant) {
        if (restaurant == null) return null;
        return new restaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getLocation(),
                new ArrayList<>(restaurant.getOpeningHours()) // Create a new list
        );
    }

    public static List<restaurantDTO> convertToRestaurantDTOList(List<Restaurant> restaurants) {
        if (restaurants == null) {
            System.out.println("Restaurant list is null");
            return new ArrayList<>();
        }

        try {
            return restaurants.stream()
                    .map(restaurant -> {
                        try {
                            List<String> hours = restaurant.getOpeningHours();
                            List<String> hoursCopy = hours != null ? new ArrayList<>(hours) : new ArrayList<>();
                            return new restaurantDTO(
                                    restaurant.getId(),
                                    restaurant.getName(),
                                    restaurant.getLocation(),
                                    hoursCopy
                            );
                        } catch (Exception e) {
                            System.err.println("Error converting restaurant: " + restaurant.getId() + " - " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in stream: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    /// //////////////////////////////////////


}
