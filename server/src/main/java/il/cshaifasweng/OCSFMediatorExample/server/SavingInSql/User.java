package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "isloggedin")
    private Boolean isLoggedin;

    @Column(name = "restaurantname")
    private String restaurantName;

    @Column(name = "role")
    private String role;  // Could be "CUSTOMER", "MANAGER", "ADMIN", etc.
//
//    Do we need this???
//    @ManyToOne
//    @JoinColumn(name = "restaurant_id")
//    private Restaurant restaurant;  // Many users can belong to one restaurant

    // Constructors
    public User() {}

    public User(String username, String password, String role, String restaurantName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isLoggedin = false;
        this.restaurantName = restaurantName;
    }

//    // Additional constructor with restaurant
//    public User(String username, String password, String email, String firstName, String lastName, String role, Restaurant restaurant) {
//        this(username, password, role);
//        this.restaurant = restaurant;
//    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getLoggedin() { return isLoggedin; }
    public void setLoggedin(Boolean loggedin) { this.isLoggedin = loggedin; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

//
//    public Restaurant getRestaurant() { return restaurant; }
//    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
//                ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
                '}';
    }
}