package il.cshaifasweng.OCSFMediatorExample.entities;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
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

    @Column(name = "tb_bool")
    private boolean available;

    @Column(name = "tb_int")
    private int tbInt;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private Set<Reservation> reservations = new HashSet<>();

    // Constructors
    public Tables() {}

    public Tables(int seats, boolean available, int tbInt, Restaurant restaurant) {
        this.seats = seats;
        this.available = available;
        this.tbInt = tbInt;
        this.restaurant = restaurant;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getTbInt() { return tbInt; }
    public void setTbInt(int tbInt) { this.tbInt = tbInt; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public Set<Reservation> getReservations() { return reservations; }
    public void setReservations(Set<Reservation> reservations) { this.reservations = reservations; }

    // Helper method to add a reservation
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setTable(this);
    }

    // Helper method to remove a reservation
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", seats=" + seats +
                ", available=" + available +
                ", tbInt=" + tbInt +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
                ", reservationCount=" + reservations.size() +
                '}';
    }
}