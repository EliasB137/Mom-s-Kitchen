package il.cshaifasweng.OCSFMediatorExample.client.events;

import java.util.List;

public class reservationCancellationResultEvent {
    private int message;

    public reservationCancellationResultEvent(int message) {
        this.message = message;
    }
    public reservationCancellationResultEvent() {}

    public int getMessage() { return message; }
    public void setMessage(int message) { this.message = message; }

}
