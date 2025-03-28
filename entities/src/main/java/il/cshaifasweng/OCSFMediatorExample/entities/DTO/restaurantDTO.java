//package il.cshaifasweng.OCSFMediatorExample.entities.DTO;
//
//import il.cshaifasweng.OCSFMediatorExample.entities.SavingInSql.Restaurant;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//public class restaurantDTO implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    private int id;
//    private String name;
//    private String location;
//    private List<String> openingHours;
//
////    PUT THIS ONLY IF YOU WANT TO MAKE INDIVIDUAL DAYS CLOSED X1
////    @ElementCollection
////    @CollectionTable(
////            name = "branch_closed_dates",
////            joinColumns = @JoinColumn(name = "branch_id")
////    )
////    @Column(name = "closed_date", nullable = false)
////    private List<LocalDate> closedDates;
////
////    // Check if the restaurant is closed on a specific date
////    public boolean isClosedOn(LocalDate date) {
////        return closedDates != null && closedDates.contains(date);
////    }
////
////    NEED TO ADD THIS WHEN STARTING A RESTAURANT
////    Arrays.asList(
////                    LocalDate.of(2025, 12, 25), // Christmas
////                    LocalDate.of(2026, 1, 1)    // New Year's Day
////     )
//
//
//    // Default constructor
//    public restaurantDTO() {}
//
//    public restaurantDTO(Restaurant restaurant) {
//        this.id = restaurant.getId();
//        this.name = restaurant.getName();
//        this.location = restaurant.getLocation();
//        this.openingHours = restaurantf.getOpeningHours();
//
//    }
//
//    public int getId() { return id; }
//    public void setId(int id) { this.id = id; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public String getLocation() { return location; }
//    public void setLocation(String location) { this.location = location; }
//    public List<String> getOpeningHours() { return openingHours; }
//    public void setOpeningHours(List<String> openingHours) {this.openingHours = openingHours; }
//
//    public String getOpeningHourForDay(String day) {
//        if (openingHours == null) {
//            return "Opening hours not available!";
//        }
//
//        return openingHours.stream()
//                .filter(hours -> hours.startsWith(day + ":"))
//                .map(hours -> hours.substring(hours.indexOf(":") + 2))  // Extracts only the time
//                .findFirst()
//                .orElse("Closed");
//    }
//
//
//    @Override
//    public String toString() {
//        return "Restaurant{id=" + id + ", name='" + name + "', location='" + location + "'}";
//    }
//}
