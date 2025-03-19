package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuDish;

import java.io.Serializable;
import java.util.List;

public class MenuResponse implements Serializable {
    private static final long serialVersionUID = 1L;  //Ensures compatibility

    private List<Dish> dishes;
    private List<MenuDish> menuDishes;

    public MenuResponse(List<Dish> dishes, List<MenuDish> menuDishes) {
        this.dishes = dishes;
        this.menuDishes = menuDishes;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public List<MenuDish> getMenuDishes() {
        return menuDishes;
    }
}


