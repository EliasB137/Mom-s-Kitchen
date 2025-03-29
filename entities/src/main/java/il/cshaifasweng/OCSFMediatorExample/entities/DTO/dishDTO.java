package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;
import java.util.List;

public class dishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String ingredients;
    private List<String> availablePreferences;
    private String price;
    private String imageUrl;
    private boolean deliveryAvailable;
    private List<String> restaurantNames;

    public dishDTO()
    {}

    public dishDTO(int id, String name, String ingredients, List<String> availablePreferences,
                   String price, String imageUrl, boolean deliveryAvailable, List<String> restaurantNames) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
        this.deliveryAvailable = deliveryAvailable;
        this.availablePreferences = availablePreferences;
        this.imageUrl = imageUrl;
        this.restaurantNames = restaurantNames;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public List<String> getAvailablePreferences() {
        return availablePreferences;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isDeliveryAvailable() {
        return deliveryAvailable;
    }

    public List<String> getRestaurantNames() {
        return restaurantNames;
    }

    public void setRestaurantNames(List<String> restaurantNames) {
        this.restaurantNames = restaurantNames;
    }

    @Override
    public String toString() {
        return "dishDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", preferences=" + availablePreferences +
                ", price='" + price + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", deliveryAvailable=" + deliveryAvailable +
                ", restaurantNames=" + restaurantNames +
                '}';
    }

}
