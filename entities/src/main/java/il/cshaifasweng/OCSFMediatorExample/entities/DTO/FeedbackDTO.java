package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;

public class FeedbackDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fullName;
    private String email;
    private String cardId;
    private boolean isDelivery;           // true = delivery, false = dine in
    private String tableNumber;           // relevant only if dine-in
    private String restaurantName;        // relevant only if delivery
    private String feedback;

    public FeedbackDTO() {}

    public FeedbackDTO(String fullName, String email, String cardId, boolean isDelivery,
                       String tableNumber, String restaurantName, String feedback) {
        this.fullName = fullName;
        this.email = email;
        this.cardId = cardId;
        this.isDelivery = isDelivery;
        this.tableNumber = tableNumber;
        this.restaurantName = restaurantName;
        this.feedback = feedback;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean isDelivery) {
        this.isDelivery = isDelivery;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

