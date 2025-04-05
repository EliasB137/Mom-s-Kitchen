package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;

public class DataPoint  implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x; // Month number (1-12)
    private int y; // Count of feedback in that month

    public DataPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}