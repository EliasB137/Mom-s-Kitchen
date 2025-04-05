package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback implements Serializable {

   @Id
   private String cardId;
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
   // private int id;

    private String fullName;
    private String email;
    private boolean isDelivery;
    private String tableNumber;
    private String restaurantName;

    @Column(length = 1000)
    private String feedback;

    private boolean isCompensated;
    private boolean isResponded;

    private LocalDateTime submittedAt;

    // Required by Hibernate
    public Feedback() {
    }


    // Full constructor for manual creation
    public Feedback(String fullName, String email, String cardId, boolean isDelivery,
                    String tableNumber, String restaurantName, String feedback, boolean isCompensated, boolean isResponded) {
        this.fullName = fullName;
        this.email = email;
        this.cardId = cardId;
        this.isDelivery = isDelivery;
        this.tableNumber = tableNumber;
        this.restaurantName = restaurantName;
        this.feedback = feedback;
        this.submittedAt = LocalDateTime.now(); // Automatically sets current time (to know when feedback submitted cuz we have 24h to respond)
        this.isCompensated = isCompensated;
        this.isResponded = isResponded;


    }

    // Getters and Setters



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

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setCompensated(boolean compensated) {
        isCompensated = compensated;
    }

    public boolean getCompensated() {
        return isCompensated;
    }

    public void setResponded(boolean responded) {
        isResponded = responded;
    }

    public boolean getResponded() {
        return isResponded;
    }
}