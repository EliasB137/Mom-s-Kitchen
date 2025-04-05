package il.cshaifasweng.OCSFMediatorExample.client.events;

public class NewReservation {
    private String message;

    public NewReservation(String message) {
        this.message = message;
    }
    public NewReservation() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}
