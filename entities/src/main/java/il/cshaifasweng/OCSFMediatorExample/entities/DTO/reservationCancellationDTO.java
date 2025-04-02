package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;

public class reservationCancellationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int orderId;
    private String customerId;

    public reservationCancellationDTO() {}

    public reservationCancellationDTO(int orderId, String customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
