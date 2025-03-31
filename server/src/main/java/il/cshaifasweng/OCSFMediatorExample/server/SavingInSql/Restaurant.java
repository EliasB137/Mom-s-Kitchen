package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
public class Restaurant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private int id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @ElementCollection
    @CollectionTable(
            name = "restaurant_opening_hours",
            joinColumns = @JoinColumn(name = "restaurant_id")
    )
    @Column(name = "opening_hours")
    private List<String> openingHours;

    @ElementCollection
    @CollectionTable(
            name = "restaurant_tables",
            joinColumns = @JoinColumn(name = "restaurant_id")
    )
    @Column(name = "tables")
    private List<Integer> tables;

    // Default constructor
    public Restaurant() {}

    public Restaurant(String name, String location, List<String> openingHours) {
        this.name = name;
        this.location = location;
        this.openingHours = openingHours;
        this.tables = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getOpeningHours() { return openingHours; }
    public void setOpeningHours(List<String> openingHours) {this.openingHours = openingHours; }
    public List<Integer> getTables() { return tables; }
    public void setTables(List<Integer> tables) { this.tables = tables; }

    public void addTable(int table) {
        tables.add(table);
    }

    public String getOpeningHourForDay(String day) {
        if (openingHours == null) {
            return "Opening hours not available!";
        }
        return openingHours.stream()
                .filter(hours -> hours.startsWith(day.trim() + ":"))
                .map(hours -> hours.substring(hours.indexOf(":") + 2))  // Extracts only the time
                .findFirst()
                .orElse("closed");
    }

    @Override
    public String toString() {
        return "Restaurant{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}
