package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;
import java.util.Arrays;

public class responseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;   // e.g., "login", "logout", "getMenu", "UpdatePrice"...
    private Object[] payload; // flexible data array

    public responseDTO(String message, Object[] payload) {
        this.message = message;
        this.payload = payload;
    }
    public responseDTO() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object[] getPayload() { return payload; }
    public void setPayload(Object[] payload) { this.payload = payload; }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", payload=" + Arrays.toString(payload) +
                    '}';
    }
}