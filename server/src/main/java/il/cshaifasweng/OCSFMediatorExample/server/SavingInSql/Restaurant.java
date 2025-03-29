package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.io.Serializable;
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

    // Default constructor
    public Restaurant() {}

    public Restaurant(String name, String location, List<String> openingHours) {
        this.name = name;
        this.location = location;
        this.openingHours = openingHours;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getOpeningHours() { return openingHours; }
    public void setOpeningHours(List<String> openingHours) {this.openingHours = openingHours; }

    @Override
    public String toString() {
        return "Restaurant{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}
