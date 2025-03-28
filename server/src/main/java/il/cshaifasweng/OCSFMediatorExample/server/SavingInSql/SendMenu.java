package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import java.io.Serializable;
import java.util.List;

public class SendMenu implements Serializable {   // ✅ Ensures it can be sent over the network
    private static final long serialVersionUID = 1L;  // Ensures version compatibility

    private String message;
    List<Dish> array;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Dish> getArray() {
        return array;
    }

    public void setArray(List<Dish> array) {
        this.array = array;
    }

    public SendMenu(String message, List<Dish> array) {
        this.message = message;
        this.array = array;
    }


}
