package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class OrderSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int orderId;
    private String customerName;
    private String customerId;
    private LocalDateTime deliveryTime;
    private String address;
    private double totalPrice;
    private Date orderDate;

    public OrderSummaryDTO() {}

    public OrderSummaryDTO(int orderId, String customerName, String customerId, LocalDateTime deliveryTime,
                           String address, double totalPrice, Date orderDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerId = customerId;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public String getAddress() {
        return address;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

}
