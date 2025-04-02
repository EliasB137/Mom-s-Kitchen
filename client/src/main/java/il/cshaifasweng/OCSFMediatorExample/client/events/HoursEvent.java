package il.cshaifasweng.OCSFMediatorExample.client.events;

import java.util.List;

public class HoursEvent {
    private List<String> payload;

    public HoursEvent(List<String> payload) {
        this.payload = payload;
    }
    public HoursEvent() {}

    public List<String> getPayload() { return payload; }
    public void setPayload(List<String> payload) { this.payload = payload; }
}
