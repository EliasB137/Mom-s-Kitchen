package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import net.bytebuddy.asm.Advice;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
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

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "address")
    private String address;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "credit_card")
    private String creditCard;

    @Column(name = "preferred_delivery_time")
    private LocalDateTime preferredDeliveryTime;

    @Column(name = "order_date")
    private java.util.Date orderDate;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    public Order() {}

    public Order(double totalPrice, String address, String customerName, String customerId,
                 String creditCard, LocalDateTime preferredDeliveryTime, java.util.Date orderDate) {
        this.totalPrice = totalPrice;
        this.address = address;
        this.customerName = customerName;
        this.customerId = customerId;
        this.creditCard = creditCard;
        this.preferredDeliveryTime = preferredDeliveryTime;
        this.orderDate = orderDate;
    }

    public int getId() {
        return id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public LocalDateTime getPreferredDeliveryTime() {
        return preferredDeliveryTime;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void addOrderItem(OrderItem item) {
        item.setOrder(this);
        orderItems.add(item);
    }

    public Set<Dish> getDishes() {
        return orderItems.stream().map(OrderItem::getDish).collect(Collectors.toSet());
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public void setPreferredDeliveryTime(LocalDateTime preferredDeliveryTime) {
        this.preferredDeliveryTime = preferredDeliveryTime;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", totalPrice=" + totalPrice +
                ", customer='" + customerName + '\'' +
                ", address='" + address + '\'' +
                ", deliveryTime='" + preferredDeliveryTime + '\'' +
                '}';
    }
}
