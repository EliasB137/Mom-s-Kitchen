package il.cshaifasweng.OCSFMediatorExample.entities.DTO.Events;

public class CancellationResultEvent {
    private final String message;

    public CancellationResultEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
