package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;
import java.util.Arrays;

public class DayWithHours  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String x;
    private String y;

    public DayWithHours(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
    @Override
    public String toString() {
        return "point{" +
                "X='" + x + '\'' +
                ", Y=" + y +
                '}';
    }
}