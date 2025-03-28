package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.Restaurant;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "feedback")
public class FeedBack implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private int id;

    @Column(name = "content")
    private String content;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "email")
    private String email;

    @Column(name = "credit_card")
    private String creditCard;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    // Constructors
    public FeedBack() {}

    public FeedBack(String content, String customerName, String email, String creditCard, Restaurant restaurant) {
        this.content = content;
        this.customerName = customerName;
        this.email = email;
        this.creditCard = creditCard;
        this.restaurant = restaurant;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreditCard() { return creditCard; }
    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
                '}';
    }
}