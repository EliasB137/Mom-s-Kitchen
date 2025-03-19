package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dishes")
public class Dish implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dish_id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "personalPreference", columnDefinition = "TEXT")
    private String personalPreference;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "delivery_available")
    private boolean deliveryAvailable;

    // Default constructor
    public Dish() {}

    // Constructor
    public Dish(String name, String ingredients, String personalPreference, String price, String imageUrl, boolean deliveryAvailable) {
        this.name = name;
        this.ingredients = ingredients;
        this.personalPreference = personalPreference;
        this.price = price;
        this.imageUrl = imageUrl;
        this.deliveryAvailable = deliveryAvailable;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getPersonalPreference() { return personalPreference; }
    public void setPersonalPreference(String personalPreference) { this.personalPreference = personalPreference; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isDeliveryAvailable() { return deliveryAvailable; }
    public void setDeliveryAvailable(boolean deliveryAvailable) { this.deliveryAvailable = deliveryAvailable; }

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
