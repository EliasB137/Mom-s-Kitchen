package il.cshaifasweng.OCSFMediatorExample.client.events;

import java.util.List;

public class LogoutResultEvent {
    private String message;

    public LogoutResultEvent(String message) {
        this.message = message;
    }
    public LogoutResultEvent() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}
