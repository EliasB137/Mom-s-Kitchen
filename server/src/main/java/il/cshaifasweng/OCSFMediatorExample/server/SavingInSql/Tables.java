package il.cshaifasweng.OCSFMediatorExample.server.SavingInSql;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tables")
public class Tables implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private int id;

    @Column(name = "seats")
    private int seats;

    @Column(name = "table_in")
    private boolean tableIn;

    @Column(name = "restaurant_id")
    private String restaurant;

    @ElementCollection
    @CollectionTable(
            name = "restaurant_reservations",
            joinColumns = @JoinColumn(name = "table_id")
    )
    @Column(name = "reservations")
    private List<Integer> reservations;

    // Constructors
    public Tables() {}

    public Tables(int seats, boolean tableIn, String restaurant) {
        this.seats = seats;
        this.tableIn = tableIn;
        this.restaurant = restaurant;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public boolean getTableIn() { return tableIn; }
    public void setTableIn(boolean tableIn) { this.tableIn = tableIn; }

    public String getRestaurant() { return restaurant; }
    public void setRestaurant(String restaurant) { this.restaurant = restaurant; }

    public List<Integer> getReservations() { return reservations; }
    public void setReservations(List<Integer> reservations) { this.reservations = reservations; }

    // Helper method to add a reservation
    public void addReservation(int reservation) {
        reservations.add(reservation);
    }

//    // Helper method to remove a reservation
//    public void removeReservation(Reservation reservation) {
//        reservations.remove(reservation);
//    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", seats=" + seats +
                ", tbInt=" + tableIn +
                ", restaurant=" + restaurant +
                ", reservationCount=" + reservations.size() +
                '}';
    }
}