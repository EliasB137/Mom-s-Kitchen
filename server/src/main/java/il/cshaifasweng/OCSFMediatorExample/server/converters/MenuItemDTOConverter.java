package il.cshaifasweng.OCSFMediatorExample.server.converters;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.MenuItemDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import il.cshaifasweng.OCSFMediatorExample.server.SavingInSql.MenuDish;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuItemDTOConverter {

    public static MenuItemDTO convert(MenuDish menuDish) {
        return new MenuItemDTO(
                new dishDTO(
                        menuDish.getDish().getId(),
                        menuDish.getDish().getName(),
                        menuDish.getDish().getIngredients(),
                        new ArrayList(menuDish.getDish().getAvailablePreferences()),
                        menuDish.getDish().getPrice(),
                        menuDish.getDish().getImageUrl(),
                        menuDish.getDish().isDeliveryAvailable()
                ),
                menuDish.getRestaurant().getName()
        );
    }

    public static List<MenuItemDTO> convertList(List<MenuDish> menuDishes) {
        return menuDishes.stream()
                .map(MenuItemDTOConverter::convert)
                .collect(Collectors.toList());
    }
}
