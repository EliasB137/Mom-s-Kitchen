package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private int id;

    @ElementCollection
    @CollectionTable(
            name = "reservations_tables",
            joinColumns = @JoinColumn(name = "reservation_id")
    )
    @Column(name = "tables")
    private List<Integer> table;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "restaurant_name")
    private String restaurant;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "credit_card")
    private String creditCard;


    // Constructors
    public Reservation() {
    }

    public Reservation(List<Integer> table, String date, String time, String restaurant,
                       String customerName, String email, String creditCard,String customerNumber) {
        this.table = table;
        this.date = date;
        this.time = time;
        this.restaurant = restaurant;
        this.customerName = customerName;
        this.email = email;
        this.creditCard = creditCard;
        this.customerNumber = customerNumber;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }


    public List<Integer> getTable() {
        return table;
    }

    public void setTable(List<Integer> table) {
        this.table = table;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", tables=" + table +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", restaurant='" + restaurant + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}