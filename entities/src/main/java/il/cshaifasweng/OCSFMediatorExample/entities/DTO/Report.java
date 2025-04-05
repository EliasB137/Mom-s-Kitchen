package il.cshaifasweng.OCSFMediatorExample.entities.DTO;

import java.io.Serializable;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private String day;
    private String number;

    public Report(String day, String numberOfSeats) {
        this.day = day;
        this.number = numberOfSeats;
    }
    public Report() {}

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
}
