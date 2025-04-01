package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;
import java.util.List;

public class reservationSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private List<Integer> table;
    private String date;
    private String time;
    private String restaurant;
    private String customerName;
    private String customerNumber;
    private String customerId;
    private String email;
    private String creditCard;
    private int numberOfGuests;

    // Default constructor
    public reservationSummaryDTO() {
    }

    // Parameterized constructor
    public reservationSummaryDTO(int id,List<Integer> table, String date, String time, String restaurant,
                          String customerName, String email, String creditCard, String customerNumber, int numberOfGuests, String customerId) {
        this.id = id;
        this.table = table;
        this.date = date;
        this.time = time;
        this.restaurant = restaurant;
        this.customerName = customerName;
        this.email = email;
        this.creditCard = creditCard;
        this.customerNumber = customerNumber;
        this.numberOfGuests = numberOfGuests;
        this.customerId = customerId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "id=" + id +
                ", tables=" + table +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", restaurant='" + restaurant + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}