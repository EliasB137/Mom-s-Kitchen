package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private Tables table;

    @Column(name = "time_and_date")
    private LocalDateTime timeAndDate;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "email")
    private String email;

    @Column(name = "credit_card")
    private String creditCard;

    @Column(name = "status")
    private String status;

    // Constructors
    public Reservation() {}

    public Reservation(Tables table, LocalDateTime timeAndDate, Restaurant restaurant,
                       String customerName, String email, String creditCard, String status) {
        this.table = table;
        this.timeAndDate = timeAndDate;
        this.restaurant = restaurant;
        this.customerName = customerName;
        this.email = email;
        this.creditCard = creditCard;
        this.status = status;

        // Update the table's reservations collection
        if (table != null) {
            table.getReservations().add(this);
        }
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Tables getTable() { return table; }
    public void setTable(Tables table) {
        this.table = table;
    }

    public LocalDateTime getTimeAndDate() { return timeAndDate; }
    public void setTimeAndDate(LocalDateTime timeAndDate) { this.timeAndDate = timeAndDate; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreditCard() { return creditCard; }
    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", table=" + (table != null ? table.getId() : "null") +
                ", timeAndDate=" + timeAndDate +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
                ", customerName='" + customerName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}