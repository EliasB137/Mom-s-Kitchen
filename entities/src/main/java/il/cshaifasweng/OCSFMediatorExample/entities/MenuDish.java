package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "menu_dish", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"restaurant_id", "dish_id"})
})
public class MenuDish implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_dish_id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // ✅ Prevents LazyInitializationException
    @JoinColumn(name = "dish_id", referencedColumnName = "dish_id", nullable = false)
    private Dish dish;

    // Default constructor
    public MenuDish() {}

    public MenuDish(Restaurant restaurant, Dish dish) {
        this.restaurant = restaurant;
        this.dish = dish;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuDish menuDish = (MenuDish) o;
        return Objects.equals(restaurant, menuDish.restaurant) &&
                Objects.equals(dish, menuDish.dish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurant, dish);
    }

    @Override
    public String toString() {
        return "MenuDish{" +
                "id=" + id +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
                ", dish=" + (dish != null ? dish.getName() : "null") +
                '}';
    }
}
