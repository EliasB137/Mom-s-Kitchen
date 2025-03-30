package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class OrderSubmissionDTO implements Serializable {
    private String name, id, address, creditCard;
    private LocalDateTime deliveryTime;
    private List<CartItem> cart;

    public OrderSubmissionDTO(String name, String id, String address, LocalDateTime deliveryTime, String creditCard, List<CartItem> cart) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.creditCard = creditCard;
        this.cart = cart;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public void setCart(List<CartItem> cart) {
        this.cart = cart;
    }
}
