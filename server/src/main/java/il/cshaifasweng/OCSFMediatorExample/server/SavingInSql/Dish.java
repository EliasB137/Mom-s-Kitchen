package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dishes")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dish_id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;

    // List of predefined preferences for this dish (e.g. "No Tomato", "Extra Cheese")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dish_preferences", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "preference")
    private List<String> availablePreferences = new ArrayList<>();


    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "delivery_available")
    private boolean deliveryAvailable;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dish_restaurant_names", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "restaurant_name")
    private List<String> restaurantNames = new ArrayList<>();

    // Default constructor
    public Dish() {}

    public Dish(String name, String ingredients, List<String> availablePreferences, String price, String imageUrl, boolean deliveryAvailable, List<String> restaurantNames) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
        this.deliveryAvailable = deliveryAvailable;
        this.availablePreferences = availablePreferences;
        this.imageUrl = imageUrl;
        this.restaurantNames = restaurantNames;
    }



    // Getters and setters
    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public List<String> getAvailablePreferences() { return availablePreferences; }
    public void setAvailablePreferences(List<String> availablePreferences) { this.availablePreferences = availablePreferences; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isDeliveryAvailable() { return deliveryAvailable; }
    public void setDeliveryAvailable(boolean deliveryAvailable) { this.deliveryAvailable = deliveryAvailable; }

    public List<String> getRestaurantNames() {
        return restaurantNames;
    }

    public void setRestaurantNames(List<String> restaurantNames) {
        this.restaurantNames = restaurantNames;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", deliveryAvailable=" + deliveryAvailable +
                '}';
    }
}
