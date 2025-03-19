package il.cshaifasweng.OCSFMediatorExample.entities;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "requested_changes")
public class RequestedChanges implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requested_change_id")
    private int id;

    @Column(name = "price")
    private int price;

    @Column(name = "ingredients")
    private String ingredients;

    @Column(name = "name")
    private String name;

    @Column(name = "personal_pref")
    private String personalPref;

    @Column(name = "category")
    private String category;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private Dish dish;

    // Constructors
    public RequestedChanges() {}

    public RequestedChanges(int price, String ingredients, String name, String personalPref, String category, Dish dish) {
        this.price = price;
        this.ingredients = ingredients;
        this.name = name;
        this.personalPref = personalPref;
        this.category = category;
        this.dish = dish;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPersonalPref() { return personalPref; }
    public void setPersonalPref(String personalPref) { this.personalPref = personalPref; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    @Override
    public String toString() {
        return "RequestedChanges{" +
                "id=" + id +
                ", price=" + price +
                ", ingredients='" + ingredients + '\'' +
                ", name='" + name + '\'' +
                ", personalPref='" + personalPref + '\'' +
                ", category='" + category + '\'' +
                ", dish=" + (dish != null ? dish.getName() : "null") +
                '}';
    }
}