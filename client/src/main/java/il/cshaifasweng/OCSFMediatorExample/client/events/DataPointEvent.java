package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.DataPoint;

import java.util.List;

public class DataPointEvent {
    private List<DataPoint> payload;

    public DataPointEvent(List<DataPoint> payload) {
        this.payload = payload;
    }
    public DataPointEvent() {}

    public List<DataPoint> getPayload() { return payload; }
    public void setPayload(List<DataPoint> payload) { this.payload = payload; }
}
