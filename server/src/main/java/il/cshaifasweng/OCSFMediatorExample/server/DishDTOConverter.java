package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.SavingInSql.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DishDTOConverter implements Serializable {
    private static final long serialVersionUID = 1L;

    public static dishDTO convertToDTO(Dish dish) {
        if (dish == null) return null;
        return new dishDTO(
                dish.getId(),
                dish.getName(),
                dish.getIngredients(),
                new ArrayList<>(dish.getAvailablePreferences()), // Create a new list
                dish.getPrice(),
                dish.getImageUrl(),
                dish.isDeliveryAvailable()
        );
    }

    public static List<dishDTO> convertToDTOList(List<Dish> dishes) {
        return dishes.stream()
                .map(dish -> new dishDTO(
                        dish.getId(),
                        dish.getName(),
                        dish.getIngredients(),
                        new ArrayList<>(dish.getAvailablePreferences()), // Create a new list
                        dish.getPrice(),
                        dish.getImageUrl(),
                        dish.isDeliveryAvailable()
                ))
                .collect(Collectors.toList());
    }

}
