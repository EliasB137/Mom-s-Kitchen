package il.cshaifasweng.OCSFMediatorExample.client.events;

import java.util.List;

public class reservationResultEvent {
    private String message;

    public reservationResultEvent(String message) {
        this.message = message;
    }
    public reservationResultEvent() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}
