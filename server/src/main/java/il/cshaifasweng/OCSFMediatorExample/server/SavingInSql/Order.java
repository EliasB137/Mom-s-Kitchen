package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int id;

    @Column(name = "price")
    private int price;

    @Column(name = "location")
    private String location;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "email")
    private String email;

    @Column(name = "credit_card")
    private String creditCard;

    @Column(name = "order_date")
    private java.util.Date orderDate;

    @Column(name = "status")
    private String status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    // Constructors
    public Order() {}

    public Order(int price, String location, String customerName, String email,
                 String creditCard, java.util.Date orderDate, String status, Restaurant restaurant) {
        this.price = price;
        this.location = location;
        this.customerName = customerName;
        this.email = email;
        this.creditCard = creditCard;
        this.orderDate = orderDate;
        this.status = status;
        this.restaurant = restaurant;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreditCard() { return creditCard; }
    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }

    public java.util.Date getOrderDate() { return orderDate; }
    public void setOrderDate(java.util.Date orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public Set<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(Set<OrderItem> orderItems) { this.orderItems = orderItems; }

    // Helper method to add dishes to the order
    public void addDish(Dish dish, int quantity, String specialInstructions) {
        OrderItem orderItem = new OrderItem(this, dish, quantity, specialInstructions);
        orderItems.add(orderItem);
        //dish.getOrderItems().add(orderItem);
    }

    // Convenience method to get all dishes in this order
    public Set<Dish> getDishes() {
        return orderItems.stream()
                .map(OrderItem::getDish)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
                '}';
    }
}