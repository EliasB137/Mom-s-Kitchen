package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.*;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.FeedbackDTO;
import il.cshaifasweng.OCSFMediatorExample.server.SavingInSql.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import net.bytebuddy.asm.Advice;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


public class MomServer extends AbstractServer {
    private static Session session;
    private static String newPassword;
    private static SessionFactory sessionFactory = null;

    /**
     * Constructs a new server.
     *
     * @param port the port number on which to listen.
     */
    public MomServer(int port) {
        super(port);
        System.out.println("Mom Server Started, Type the SQL password :");
        Scanner scanner = new Scanner(System.in);
        newPassword = scanner.nextLine();
        scanner.close();
        ensureTestDataExists();


    }

    private void ensureTestDataExists() {
        Session session = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            // Check if any restaurant exists
            List<Restaurant> restaurantList = session.createQuery("FROM Restaurant", Restaurant.class).getResultList();

            if (restaurantList.isEmpty()) {
                System.out.println("No restaurants found! Adding test restaurant...");

                Restaurant restaurant = new Restaurant("Golden Gate Bites", "Downtown", Arrays.asList(
                        "monday: 08:00-22:00",
                        "tuesday: 08:00-22:00",
                        "wednesday: 08:00-22:00",
                        "thursday: 08:00-22:00",
                        "friday: 08:00-22:00",
                        "saturday: 09:00-23:00",
                        "sunday: closed"
                ));
                session.save(restaurant);
                session.flush();

                System.out.println("Test restaurant added: " + restaurant.getName());

                // Check if any dish exists
                List<Dish> dishList = session.createQuery("FROM Dish", Dish.class).getResultList();
                if (dishList.isEmpty()) {
                    System.out.println("No dishes found! Adding test dish...");
                    Dish dish = new Dish("Spaghetti Carbonara",
                            "Spaghetti, Eggs, Pecorino Romano cheese, pancetta, and black pepper",
                            Arrays.asList("No Pepper", "Extra Cheese", "No tomato"), "72",
                            "https://cdn.loveandlemons.com/wp-content/uploads/2024/12/caesar-salad.jpg", true, Arrays.asList("Golden Gate Bites"));
                    session.save(dish);
                    session.flush();

                    dish = new Dish("Caesar Salad", "Romaine lettuce, croutons, and Caesar dressing",
                            Arrays.asList("No Pepper", "Extra Cheese", "No tomato"), "48",
                            "https://cdn.loveandlemons.com/wp-content/uploads/2024/12/caesar-salad.jpg", true, Arrays.asList("All"));


                    session.save(dish);
                    session.flush();
                    dish = new Dish("test1", "Romaine lettuce, croutons, and Caesar dressing",
                            Arrays.asList("No Pepper", "Extra Cheese", "No tomato"), "48",
                            "https://cdn.loveandlemons.com/wp-content/uploads/2024/12/caesar-salad.jpg", false, Arrays.asList("All"));
                    session.save(dish);
                    session.flush();

                    System.out.println("Test dish added: " + dish.getName());

                    session.flush();

                    System.out.println("Linked " + dish.getName() + " to " + restaurant.getName());
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("ERROR: Failed to add test restaurant and dish!");
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        session = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            List<Tables> tablesList = session.createQuery("FROM Tables", Tables.class).getResultList();
            if (tablesList.isEmpty()) {
                Query<Restaurant> query = session.createQuery("FROM Restaurant WHERE name = :name", Restaurant.class);
                query.setParameter("name", "Golden Gate Bites");
                Restaurant restaurantAddTableTo = query.uniqueResult();

                System.out.println("No tables found! Adding test tables...");
                Tables table = new Tables(2, true, "Golden Gate Bites");
                session.save(table);
                session.flush();

                restaurantAddTableTo.addTable(table.getId());
                session.update(restaurantAddTableTo);

                table = new Tables(3, true, "Golden Gate Bites");
                session.save(table);
                session.flush();

                restaurantAddTableTo.addTable(table.getId());
                session.update(restaurantAddTableTo);

                table = new Tables(4, true, "Golden Gate Bites");
                session.save(table);
                session.flush();

                restaurantAddTableTo.addTable(table.getId());
                session.update(restaurantAddTableTo);


                User user = new User("restaurant manager", "restaurant manager", "restaurant manager", "Golden Gate Bites");
                session.save(user);
                session.flush();
                user = new User("chain manager", "chain manager", "chain manager", "");
                session.save(user);
                session.flush();
                user = new User("worker", "worker", "worker", "");
                session.save(user);
                session.flush();
                user = new User("dietitian", "dietitian", "dietitian", "");
                session.save(user);
                session.flush();
                user = new User("customer care", "customer care", "customer care", "");
                session.save(user);
                session.flush();

                System.out.println("Linked " + table.getId() + " to " + restaurantAddTableTo.getName());
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("ERROR: Failed to add test Tables!!");
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        session = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            List<Reservation> tablesList = session.createQuery("FROM Reservation ", Reservation.class).getResultList();
            if (tablesList.isEmpty()) {
                Query<Tables> query = session.createQuery("FROM Tables WHERE id = :id", Tables.class);
                query.setParameter("id", 2);
                Tables tableAddReservationTo = query.uniqueResult();

                Reservation reservation = new Reservation(Arrays.asList(2), "2025-04-04", "10:00", "Golden Gate Bites", "tony", "tonysabbah@gmail.com", "123412341234,123", "1234567890", 3, "213991516");
                session.save(reservation);
                session.flush();

                tableAddReservationTo.addReservation(reservation.getId());
                session.update(tableAddReservationTo);

                System.out.println("Linked " + reservation.getId() + " to " + tableAddReservationTo.getId());
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("ERROR: Failed to add test Tables!!");
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }


    }







    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof responseDTO) {
            responseDTO message = (responseDTO) msg;
            String command = message.getMessage();
            Object[] payload = message.getPayload();
            System.out.println("Received message from client: " + msg);

            if (message.getMessage().equals("submitPriceChangeRequest")) {
                RequestedChangesDTO change = (RequestedChangesDTO) message.getPayload()[0];

                try (Session session = getSessionFactory().openSession()) {
                    session.beginTransaction();

                    Dish dish = session.get(Dish.class, change.getDishId());

                    RequestedChanges req = new RequestedChanges(
                            change.getPrice(),
                            change.getIngredients(),
                            change.getName(),
                            change.getPersonalPref(),
                            dish  // only pass the dish object now
                    );

                    session.save(req);
                    session.getTransaction().commit();
                    System.out.println("[DEBUG] RequestedChanges saved successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("[ERROR] Failed to save RequestedChanges.");
                }
            }

            else if (command.equals("defineAsChainDish")) {
                int dishId = (int) message.getPayload()[0];

                try (Session session = getSessionFactory().openSession()) {
                    session.beginTransaction();

                    Dish dish = session.get(Dish.class, dishId);
                    if (dish != null) {
                        List<String> restaurants = dish.getRestaurantNames();
                        if (!restaurants.contains("All")) {
                            restaurants.add("All");
                            dish.setRestaurantNames(restaurants);
                            session.update(dish);
                            session.getTransaction().commit();
                            System.out.println("[DEBUG] Dish marked as chain-wide.");

                            // Notify all other clients about the menu update
                            responseDTO updateNotification = new responseDTO("menuUpdated", new Object[]{"Dish updated to chain-wide"});
                            sendToAllExcept(updateNotification, client);
                        } else {
                            System.out.println("[INFO] Dish is already chain-wide.");
                        }
                    } else {
                        System.err.println("[ERROR] Dish not found with ID: " + dishId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("[ERROR] Failed to define dish as chain-wide.");
                }
            }


            //Feedback thing
            //Review feedback, show all feedbacks
            else if ("getAllFeedbacks".equals(command)) {
                System.out.println("Server received 'getAllFeedbacks' request.");

                try {
                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    List<Feedback> feedbacks = session.createQuery("FROM Feedback", Feedback.class).getResultList();

                    session.getTransaction().commit();
                    session.close();

                    if (feedbacks.isEmpty()) {
                        System.out.println("[DEBUG] No feedbacks found.");
                    } else {
                        System.out.println("[DEBUG] Total feedbacks in DB: " + feedbacks.size());
                    }

                    List<FeedbackDTO> feedbackDTOs = DTOConverter.convertToFeedbackDTOList(feedbacks);


                    responseDTO response = new responseDTO("getAllFeedbacks", new Object[]{feedbackDTOs});
                    client.sendToClient(response);

                } catch (Exception e) {
                    System.err.println("ERROR: Failed to fetch feedbacks!");
                    e.printStackTrace();
                }
            }

            //respond to feedback (when the Customer care Review the feedback and send the response)

            else if ("respondToFeedback".equals(command)) {
                System.out.println("Server received 'respondToFeedback' request.");

                try {
                    String fullName = (String) payload[0];
                    String email = (String) payload[1];
                    String feedbackMessage = (String) payload[2];
                    String responseText = (String) payload[3];
                    boolean isCompensated = (boolean) payload[4];


                    System.out.println("Searching for feedback: " + fullName + " | " + email + " | " + feedbackMessage);

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    String hql = "FROM Feedback f WHERE f.fullName = :name AND f.email = :email AND f.feedback = :message";
                    Feedback feedback = session.createQuery(hql, Feedback.class)
                            .setParameter("name", fullName)
                            .setParameter("email", email)
                            .setParameter("message", feedbackMessage)
                            .uniqueResult();

                    if (feedback != null) {
                        feedback.setResponded(true);
                        feedback.setCompensated(isCompensated);

                        session.update(feedback);
                        session.getTransaction().commit();

                        System.out.println("Successfully updated feedback: " + fullName);

                        // Send updated feedback list to all clients
                        List<Feedback> updatedFeedbacks = session.createQuery("FROM Feedback", Feedback.class).getResultList();
                        List<FeedbackDTO> updatedDTOs = DTOConverter.convertToFeedbackDTOList(updatedFeedbacks);

                        responseDTO response = new responseDTO("getAllFeedbacks", new Object[]{updatedDTOs});
                        sendToAllClients(response);

                    } else {
                        System.err.println("[ERROR] Feedback not found!");
                        session.getTransaction().rollback();
                    }

                    session.close();

                } catch (Exception e) {
                    System.err.println("ERROR processing feedback response:");
                    e.printStackTrace();
                }
            }


            //Submit feedback
            else if ("submitFeedback".equals(command)) {
                    System.out.println("Server received 'submitFeedback' request.");

                    try (Session session = sessionFactory.openSession()) {
                        session.beginTransaction();

                        FeedbackDTO feedbackDTO = (FeedbackDTO) payload[0];

                        Feedback feedbackEntity = new Feedback(
                                feedbackDTO.getFullName(),
                                feedbackDTO.getEmail(),
                                feedbackDTO.getCardId(),
                                feedbackDTO.isDelivery(),
                                feedbackDTO.getTableNumber(),
                                feedbackDTO.getRestaurantName(),
                                feedbackDTO.getFeedback(), // the actual feedback message
                                feedbackDTO.getCompensated(),
                                feedbackDTO.getResponded()
                        );

                        session.save(feedbackEntity);
                        session.getTransaction().commit();

                        System.out.println(" Feedback saved successfully to database.");
                    } catch (Exception e) {
                        System.err.println(" Error saving feedback: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

            else if (command.equals("processChangeRequest")) {
                int dishId = (int) payload[0];
                boolean approved = (boolean) payload[1];

                try (Session session = getSessionFactory().openSession()) {
                    session.beginTransaction();
                    RequestedChanges change = session.createQuery(
                                    "FROM RequestedChanges WHERE dish.id = :dishId", RequestedChanges.class)
                            .setParameter("dishId", dishId)
                            .uniqueResult();

                    if (change != null) {
                        if (approved) {
                            Dish dish = change.getDish();
                            dish.setPrice(String.valueOf(change.getPrice()));
                            dish.setIngredients(change.getIngredients());
                            dish.setAvailablePreferences(Arrays.asList(change.getPersonalPref().split(",")));
                            session.update(dish);

                            // Notify all other clients about the menu update
                            responseDTO updateNotification = new responseDTO("menuUpdated", new Object[]{"Dish updated"});
                            sendToAllExcept(updateNotification, client);
                        }
                        session.remove(change); // Delete whether approved or rejected
                    }

                    session.getTransaction().commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if (command.equals("getHours")) {
                    System.out.println("[DEBUG] Command getHours ");

                    LocalDate date = (LocalDate) payload[0];
                    String time = payload[1].toString();
                    String inOrOut = payload[2].toString();
                    String numberOfGuest = payload[3].toString();
                    String restaurantName = payload[4].toString();

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    String hql1 = "FROM Reservation r WHERE r.restaurant = :givenRestaurantName AND r.date = :givenDate";

                    List<Reservation> reservations = null;
                    try {
                        Query<Reservation> query1 = session.createQuery(hql1, Reservation.class);
                        query1.setParameter("givenRestaurantName", restaurantName);
                        query1.setParameter("givenDate", date.toString());
                        reservations = query1.list();
                        System.out.println("[DEBUG] Got " + reservations.size() + " reservations " + date.toString() + " " + restaurantName);
                    } catch (Exception e) {
                        System.out.println("[DEBUG] Query error: " + e.getMessage());
                        e.printStackTrace();
                    }

                    for (Reservation reservation : reservations) {
                        Hibernate.initialize(reservation.getTable());
                    }

                    String hql2 = "FROM Tables r WHERE r.restaurant = :givenRestaurantName AND r.tableIn = :isInTable";

                    Query query2 = session.createQuery(hql2);
                    query2.setParameter("givenRestaurantName", restaurantName);
                    if (inOrOut.equals("Inside"))
                        query2.setParameter("isInTable", true);
                    else
                        query2.setParameter("isInTable", false);
                    List<Tables> tables = query2.list();
                    for (Tables table : tables) {
                        Hibernate.initialize(table.getReservations());
                    }
                    session.getTransaction().commit();
                    session.close();

                    System.out.println("[DEBUG] Got  " + tables.size() + " tables");


                    List<String> hours = getAvailableHours(reservations, tables, restaurantName, numberOfGuest, date);

                    System.out.println("[DEBUG] Got " + hours.size() + " hours");

                    responseDTO response = new responseDTO("availableHours", new Object[]{hours});
                    try {
                        client.sendToClient(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (command.equals("confirmReservation")) {
                    System.out.println("[DEBUG] Command confirmReservation ");

                    String name = payload[0].toString();
                    String number = payload[1].toString();
                    String creditCard = payload[2].toString();
                    String email = payload[3].toString();
                    String time = payload[4].toString();
                    LocalDate date = (LocalDate) payload[5];
                    String numberOfGuest = payload[6].toString();
                    String restaurantName = payload[7].toString();
                    String inOrOut = payload[8].toString();
                    String customerId = payload[9].toString();

                    Session session = sessionFactory.openSession();

                    List<Integer> result = confirmReservations(name, number, creditCard, email, time, date, numberOfGuest, restaurantName, inOrOut, customerId);

                    if (!result.isEmpty()) {
                        responseDTO response = new responseDTO("reservationResult", new Object[]{result});
                        responseDTO responseForAll = new responseDTO("updateReservations", new Object[]{});
                        try {
                            client.sendToClient(response);
                            sendToAllExcept(responseForAll, client);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        responseDTO response = new responseDTO("reservationResult", new Object[]{});
                        try {
                            client.sendToClient(response);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                else if (command.equals("addDish")) {
                    dishDTO dto = (dishDTO) payload[0];

                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();

                        Dish newDish = DTOConverter.convertToDishEntity(dto);

                        session.save(newDish);
                        session.getTransaction().commit();

                        responseDTO response = new responseDTO("dishAdded", new Object[]{"Dish added: " + newDish.getName()});
                        client.sendToClient(response);

                        // Notify all other clients about the menu update
                        responseDTO updateNotification = new responseDTO("menuUpdated", new Object[]{"Dish added"});
                        sendToAllExcept(updateNotification, client);

                        System.out.println("[DEBUG] Dish added successfully: " + newDish.getName());

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            client.sendToClient("dishAddError:" + e.getMessage());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (command.equals("logInRequest")) {
                    String username = payload[0].toString();
                    String password = payload[1].toString();

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    String hql = "FROM User r WHERE r.username = :givenUsername AND r.password = :givenPassword";

                    Query query = session.createQuery(hql);
                    query.setParameter("givenUsername", username);
                    query.setParameter("givenPassword", password);
                    User user = (User) query.uniqueResult();

                    int userID = 0;
                    String userRole = "0";

                    String reply;
                    if (user == null) {
                        reply = "notFound";
                    } else if (user.getLoggedin()) {
                        reply = "alreadyLoggedin";
                    } else {
                        reply = "loginSuccessful";
                        userID = user.getId();
                        userRole = user.getRole();
                        user.setLoggedin(true);
                        session.update(user);
                    }
                    responseDTO response = new responseDTO("loginResult", new Object[]{reply, userID, userRole});
                    session.getTransaction().commit();
                    session.close();
                    try {
                        client.sendToClient(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (command.equals("logOutRequest")) {
                    int userID = (int) payload[0];
                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    String hql = "FROM User r WHERE r.id = :givenUserID";

                    Query query = session.createQuery(hql);
                    query.setParameter("givenUserID", userID);
                    User user = (User) query.uniqueResult();

                    user.setLoggedin(false);
                    session.update(user);

                    String reply = "logoutSuccessful";

                    responseDTO response = new responseDTO("logoutResult", new Object[]{reply});

                    session.getTransaction().commit();
                    session.close();

                    try {
                        client.sendToClient(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else if (command.equals("getSeatingReport")) {
                    String month = payload[0].toString();
                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();
                        String monthNumber = monthNameToNumber(month);

                        // Query to get reservations from the same month
                        String hql = "FROM Reservation r WHERE SUBSTRING(r.date, 6, 2) = :month";
                        List<Reservation> reservations = session.createQuery(hql, Reservation.class)
                                .setParameter("month", monthNumber)
                                .list();
                        session.getTransaction().commit();
                        // Assuming you already have the filtered reservations for the specific month
                        Map<String, String> guestsPerDay = new HashMap<>();

                        // Iterate through the reservations and sum up guests for each day
                        // First create a temporary map to do the calculations
                        Map<String, Integer> tempGuestsPerDay = new HashMap<>();

                        for (Reservation reservation : reservations) {
                            String fullDate = reservation.getDate(); // Format: "2025-04-04"

                            // Extract just the day part (last 2 characters)
                            String day = fullDate.substring(8, 10);

                            int guests = reservation.getNumberOfGuests();

                            // If this day already exists in the map, add to the count
                            // Otherwise, initialize it with the current guest count
                            tempGuestsPerDay.merge(day, guests, Integer::sum);
                        }
                        List<Report> seatingsReportList = new ArrayList<>();
                        int year = LocalDate.now().getYear(); // Or extract from one of your reservations


                        // Now convert the Integer values to String
                        for (Map.Entry<String, Integer> entry : tempGuestsPerDay.entrySet()) {
                            guestsPerDay.put(entry.getKey(), entry.getValue().toString());
                        }

                        // Determine the number of days in the month
                        int daysInMonth;
                        if (month.equals("February")) {
                            // Check for leap year
                            daysInMonth = Year.isLeap(year) ? 29 : 28;
                        } else if (month.equals("April") || month.equals("June") ||
                                month.equals("September") || month.equals("November")) {
                            daysInMonth = 30;
                        } else {
                            daysInMonth = 31;
                        }

                        for (int i = 1; i <= daysInMonth; i++) {
                            // Format day as two digits (01, 02, etc.)
                            String dayStr = String.format("%02d", i);

                            // Get the number of guests for this day, or "0" if no reservations
                            String guestCount = guestsPerDay.getOrDefault(dayStr, "0");

                            // Create a new seatingsReport object and add to the list
                            seatingsReportList.add(new Report(dayStr, guestCount));
                        }


                        responseDTO response = new responseDTO("seatingReport", new Object[]{seatingsReportList});

                        client.sendToClient(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (command.equals("getOrdersReport")) {
                    String month = payload[0].toString();
                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();
                        String monthNumber = monthNameToNumber(month);

                        // Query to get reservations from the same month
                        String hql = "FROM Order o WHERE FUNCTION('MONTH', o.orderDate) = :monthNum";
                        List<Order> orders = session.createQuery(hql, Order.class)
                                .setParameter("monthNum", Integer.parseInt(monthNumber))
                                .list();
                        session.getTransaction().commit();

                        // Assuming you already have the filtered reservations for the specific month
                        Map<String, String> ordersPerDay = new HashMap<>();

                        // Iterate through the reservations and sum up guests for each day
                        // First create a temporary map to do the calculations
                        Map<String, Integer> tempOrdersPerDay = new HashMap<>();

                        for (Order order : orders) {
                            String fullDate = order.getOrderDate().toString(); // Format: "2025-04-04"

                            // Extract just the day part (last 2 characters)
                            SimpleDateFormat sdf = new SimpleDateFormat("dd"); // "dd" gives day of month with leading zero
                            String day = sdf.format(fullDate);

                            // If this day already exists in the map, add to the count
                            // Otherwise, initialize it with the current guest count
                            tempOrdersPerDay.merge(day, 1, Integer::sum);
                        }
                        List<Report> ordersReportList = new ArrayList<>();
                        int year = LocalDate.now().getYear(); // Or extract from one of your reservations


                        // Now convert the Integer values to String
                        for (Map.Entry<String, Integer> entry : tempOrdersPerDay.entrySet()) {
                            ordersPerDay.put(entry.getKey(), entry.getValue().toString());
                        }
                        int daysInMonth;
                        if (month.equals("February")) {
                            // Check for leap year
                            daysInMonth = Year.isLeap(year) ? 29 : 28;
                        } else if (month.equals("April") || month.equals("June") ||
                                month.equals("September") || month.equals("November")) {
                            daysInMonth = 30;
                        } else {
                            daysInMonth = 31;
                        }

                        for (int i = 1; i <= daysInMonth; i++) {
                            // Format day as two digits (01, 02, etc.)
                            String dayStr = String.format("%02d", i);

                            // Get the number of guests for this day, or "0" if no reservations
                            String guestCount = ordersPerDay.getOrDefault(dayStr, "0");

                            // Create a new seatingsReport object and add to the list
                            ordersReportList.add(new Report(dayStr, guestCount));
                        }


                        responseDTO response = new responseDTO("ordersReport", new Object[]{ordersReportList});

                        client.sendToClient(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (command.equals("getFeedBackReport")) {
                    String userRole = payload[0].toString();
                    String selectedRestaurant = payload[1].toString();
                    List<Feedback> feedbackList = new ArrayList<>();
                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();
                        String hql;
                        if (userRole.equals("restaurant manager")) {
                            hql = "FROM Feedback r WHERE r.restaurantName = :givenRestaurant";
                            Query<Feedback> query = session.createQuery(hql, Feedback.class);
                            query.setParameter("givenRestaurant", selectedRestaurant);
                            feedbackList = query.list();
                        } else {
                            hql = "FROM Feedback";
                            Query<Feedback> query = session.createQuery(hql, Feedback.class);
                            feedbackList = query.list();
                        }
                        session.getTransaction().commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    List<DataPoint> dataPoints = convertFeedbackToDataPoints(feedbackList);

                    responseDTO response = new responseDTO("feedbacksReport", new Object[]{dataPoints});

                    try {
                        client.sendToClient(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (command.equals("getTableMap")) {
                    List<Reservation> reservations = getCurrentReservationsWithRoundedTime();
                    System.out.println("Check1");

                    if (reservations == null) {
                        System.out.println("reservations is null");
                        return;
                    }

                    List<Integer> reservedTables = new ArrayList<>();

                    for (int i = 0; i < reservations.size(); i++) {
                        Reservation reservation = reservations.get(i);
                        if (reservation == null) {
                            System.out.println("Null reservation at index " + i);
                            continue;
                        }
                        List<Integer> tableIds = reservation.getTable();
                        if (tableIds != null) {
                            try {
                                reservedTables.addAll(tableIds);
                            } catch (Exception e) {
                                System.out.println("Exception at index " + i + ": " + e);
                            }
                        } else {
                            System.out.println("reservation.getTable() returned null at index " + i);
                        }
                    }
                    System.out.println("Check2");

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    // Query to get all tables
                    String hql = "FROM Tables";
                    List<Tables> allTables = session.createQuery(hql, Tables.class).list();

                    session.getTransaction().commit();
                    session.close();
                    System.out.println("Check3");

                    List<DataPoint> dataPoints = new ArrayList<>();

                    // For each table, create a data point
                    for (Tables table : allTables) {
                        // Set y to 1 if the table is in the reserved list, otherwise 0
                        int reservationStatus = reservedTables.contains(table.getId()) ? 1 : 0;

                        // Create and add the data point
                        dataPoints.add(new DataPoint(table.getId(), reservationStatus));
                    }
                    System.out.println("Check5");

                    responseDTO response = new responseDTO("tableMap", new Object[]{dataPoints});
                    try {
                        client.sendToClient(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            } else if (msg instanceof String) {
                String msgString = msg.toString();
                if (msgString.equals("getPendingChanges")) {
                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();
                        List<RequestedChanges> changes = session.createQuery("FROM RequestedChanges", RequestedChanges.class).list();

                        List<RequestedChangesDTO> dtoList = changes.stream().map(change -> new RequestedChangesDTO(
                                change.getPrice(),
                                change.getIngredients(),
                                change.getName(),
                                change.getPersonalPref(),
                                change.getDish().getId()
                        )).collect(Collectors.toList());

                        responseDTO response = new responseDTO("PendingChangesResponse", new Object[]{dtoList});
                        client.sendToClient(response);

                        session.getTransaction().commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msgString.equals("getMenu")) {
                    System.out.println("Server received 'getMenu' request.");

                    try {
                        Session session = sessionFactory.openSession();
                        session.beginTransaction();

                        List<Dish> dishes = session.createQuery("FROM Dish", Dish.class).getResultList();
                        for (Dish dish : dishes) {
                            Hibernate.initialize(dish.getAvailablePreferences());
                            Hibernate.initialize(dish.getRestaurantNames()); // <- This solves the LazyInitializationException
                        }

                        session.getTransaction().commit();
                        session.close();

                        if (dishes.isEmpty()) {
                            System.out.println("[ERROR] No dishes found in database.");
                        } else {
                            System.out.println("[DEBUG] Total Dishes in DB: " + dishes.size());
                            for (Dish dish : dishes) {
                                System.out.println("Dish: " + dish.getName());
                            }
                        }
                        List<dishDTO> dishesDTO = DTOConverter.convertToDishDTOList(dishes);

                        if (dishesDTO == null) {
                            System.err.println("[ERROR] dishesDTO is null!");
                        } else {
                            System.out.println("[DEBUG] dishesDTO: " + dishesDTO);
                        }

                        responseDTO response = new responseDTO("MenuResponse", new Object[]{dishesDTO});
                        client.sendToClient(response);


                    } catch (Exception e) {
                        System.err.println("ERROR: Failed to fetch dishes!");
                        e.printStackTrace();
                    }
                } else if (msgString.equals("getRestaurants")) {
                    System.out.println("Server received 'getRestaurants' request!");

                    List<Restaurant> restaurantList = null;

                    try {
                        SessionFactory sessionFactory = getSessionFactory();
                        session = sessionFactory.openSession();
                        session.beginTransaction();

                        // Using Criteria API to fetch restaurants
                        CriteriaBuilder builder = session.getCriteriaBuilder();
                        CriteriaQuery<Restaurant> query = builder.createQuery(Restaurant.class);
                        query.from(Restaurant.class);
                        restaurantList = session.createQuery(query).getResultList();

                        // THIS IS NEEDED FOR THE ARRAY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        for (Restaurant restaurant : restaurantList) {
                            Hibernate.initialize(restaurant.getOpeningHours());
                        }

                        session.getTransaction().commit();
                    } catch (Exception e) {
                        if (session != null) {
                            session.getTransaction().rollback();
                        }
                        System.err.println("ERROR: Failed to fetch restaurants from the database!");
                        e.printStackTrace();
                    } finally {
                        if (session != null) {
                            session.close();
                        }
                    }
                    // Ensure data exists before sending
                    if (restaurantList == null || restaurantList.isEmpty()) {
                        System.out.println("No restaurants found.");
                        return;
                    }
                    List<restaurantDTO> restaurantsDTO = DTOConverter.convertToRestaurantDTOList(restaurantList);

                    if (restaurantsDTO == null) {
                        System.err.println("[ERROR] dishesDTO is null!");
                    } else {
                        System.out.println("[DEBUG] dishesDTO: " + restaurantsDTO);
                    }
                    responseDTO response = new responseDTO("restaurants", new Object[]{restaurantsDTO});


                    if (client != null && client.isAlive()) {
                        try {
                            client.sendToClient(response);
                        } catch (IOException e) {
                        }
                    }
                } else if (msgString.startsWith("removeDish:")) {
                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();

                        int dishId = Integer.parseInt(msgString.replace("removeDish:", "").trim());
                        Dish dish = session.get(Dish.class, dishId);

                        if (dish != null) {
                            session.remove(dish);
                            session.getTransaction().commit();
                            System.out.println("[DEBUG] Removed dish: " + dish.getName());

                            // Notify client of success
                            responseDTO updateNotification = new responseDTO("menuUpdated", new Object[]{"Dish Removed"});
                            sendToAllExcept(updateNotification, client);
                        } else {
                            System.err.println("[ERROR] Dish with ID " + dishId + " not found.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msgString.startsWith("getMenuForRestaurant:")) {
                    String restaurantName = msgString.replace("getMenuForRestaurant:", "").trim();
                    System.out.println("Server received 'getMenuForRestaurant' request for: " + restaurantName);

                    try {
                        Session session = sessionFactory.openSession();
                        session.beginTransaction();

                        List<Dish> filteredDishes = session.createQuery("FROM Dish", Dish.class)
                                .getResultList()
                                .stream()
                                .filter(d -> d.getRestaurantNames().contains(restaurantName) || d.getRestaurantNames().contains("All"))
                                .collect(Collectors.toList());

                        List<dishDTO> dishDTOs = DTOConverter.convertToDishDTOList(filteredDishes);
                        responseDTO response = new responseDTO("MenuForRestaurant", new Object[]{dishDTOs});
                        client.sendToClient(response);


                        session.getTransaction().commit();
                        session.close();


                    } catch (Exception e) {
                        System.err.println("ERROR: Failed to fetch menu for restaurant!");
                        e.printStackTrace();
                    }
                } else if (msgString.startsWith("getRestaurantByName:")) {
                    String restaurantName = msgString.replace("getRestaurantByName:", "").trim();
                    System.out.println("Server received 'getRestaurantByName' request for: " + restaurantName);

                    Session session = sessionFactory.openSession();
                    Restaurant restaurant = null;

                    try {
                        String hql = "FROM Restaurant r WHERE r.name = :restaurantName";
                        Query<Restaurant> query = session.createQuery(hql, Restaurant.class);
                        query.setParameter("restaurantName", restaurantName);

                        restaurant = query.uniqueResult();  // Can be null if no match found

                        Hibernate.initialize(restaurant.getOpeningHours());


                    } catch (Exception e) {
                        e.printStackTrace();  // Log any errors
                    } finally {
                        session.close();  // Ensure session is closed
                    }

                    if (restaurant != null) {
                        System.out.println("[DEBUG] Got Restaurant from Database: " + restaurant);
                    } else {
                        System.out.println("[DEBUG] No restaurant found with name: " + restaurantName);
                    }

                    restaurantDTO RestaurantDTO = DTOConverter.convertToRestaurantDTO(restaurant);

                    System.out.println("[DEBUG] converted restaurant and now sending it " + restaurant);

                    if (client != null && client.isAlive()) {
                        try {
                            client.sendToClient(RestaurantDTO);
                        } catch (IOException e) {
                        }
                    }
                } else if (msgString.startsWith("getOrdersByCustomerId:")) {
                    String customerId = msgString.replace("getOrdersByCustomerId:", "").trim();

                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();

                        List<Order> orders = session.createQuery("FROM Order WHERE customerId = :id", Order.class)
                                .setParameter("id", customerId)
                                .getResultList();

                        List<OrderSummaryDTO> summaries = orders.stream().map(order -> new OrderSummaryDTO(
                                order.getId(),
                                order.getCustomerName(),
                                order.getCustomerId(),
                                order.getPreferredDeliveryTime(),
                                order.getAddress(),
                                order.getTotalPrice(),
                                order.getOrderDate()
                        )).collect(Collectors.toList());

                        responseDTO response = new responseDTO("CustomerOrdersResponse", new Object[]{summaries});
                        client.sendToClient(response);

                        session.getTransaction().commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msgString.startsWith("getReservationsByCustomerId:")) {
                    String customerId = msgString.replace("getReservationsByCustomerId:", "").trim();

                    try (Session session = getSessionFactory().openSession()) {
                        session.beginTransaction();

                        List<Reservation> reservations = session.createQuery("FROM Reservation WHERE customerId = :id", Reservation.class)
                                .setParameter("id", customerId)
                                .getResultList();
                        List<reservationSummaryDTO> summaries = reservations.stream().map(reservation -> new reservationSummaryDTO(
                                reservation.getId(),
                                new ArrayList<>(reservation.getTable()),
                                reservation.getDate(),
                                reservation.getTime(),
                                reservation.getRestaurant(),
                                reservation.getCustomerName(),
                                reservation.getEmail(),
                                reservation.getCreditCard(),
                                reservation.getCustomerNumber(),
                                reservation.getNumberOfGuests(),
                                reservation.getCustomerId()
                        )).collect(Collectors.toList());
                        responseDTO response = new responseDTO("CustomerReservationsResponse", new Object[]{summaries});
                        client.sendToClient(response);

                        session.getTransaction().commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (msg instanceof OrderSubmissionDTO) {
                OrderSubmissionDTO orderDTO = (OrderSubmissionDTO) msg;
                try {
                    Session session = getSessionFactory().openSession();
                    session.beginTransaction();

                    // 2. Calculate total price
                    double total = orderDTO.getCart().stream()
                            .mapToDouble(item -> {
                                try {
                                    return Double.parseDouble(item.getDish().getPrice()) * item.getQuantity();
                                } catch (Exception e) {
                                    return 0.0;
                                }
                            }).sum();

                    // 3. Create and save the Order
                    Order order = new Order(
                            total,
                            orderDTO.getAddress(),
                            orderDTO.getName(),
                            orderDTO.getId(),
                            orderDTO.getCreditCard(),
                            orderDTO.getDeliveryTime(),
                            new Date()
                    );
                    session.save(order);

                    // 4. Add OrderItems
                    for (CartItem item : orderDTO.getCart()) {
                        // Fetch the actual Dish from DB by ID
                        Dish dish = session.get(Dish.class, item.getDish().getId());

                        if (dish != null) {
                            String prefs = String.join(", ", item.getSelectedPreferences());
                            OrderItem orderItem = new OrderItem(order, dish, item.getQuantity(), prefs);
                            session.save(orderItem);
                        } else {
                            System.err.println("[WARNING] Dish not found for ID: " + item.getDish().getId());
                        }
                    }

                    session.getTransaction().commit();
                    session.close();

                    client.sendToClient(new responseDTO("OrderSuccess", new Object[]{}));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        client.sendToClient(new responseDTO("OrderFailure", new Object[]{}));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } else if (msg instanceof OrderCancellationDTO) {
                OrderCancellationDTO cancelRequest = (OrderCancellationDTO) msg;

                try (Session session = getSessionFactory().openSession()) {
                    session.beginTransaction();

                    Order order = session.get(Order.class, cancelRequest.getOrderId());

                    if (order == null || !order.getCustomerId().equals(cancelRequest.getCustomerId())) {
                        client.sendToClient(new responseDTO("OrderCancellationFailure", new Object[]{"Order not found or customer ID mismatch."}));
                        return;
                    }

                    // Calculate refund based on delivery time
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime deliveryTime = order.getPreferredDeliveryTime();

                    long minutesUntilDelivery = ChronoUnit.MINUTES.between(now, deliveryTime);
                    double refund;
                    if (minutesUntilDelivery >= 180) {
                        refund = order.getTotalPrice(); // Full refund
                    } else if (minutesUntilDelivery >= 60) {
                        refund = order.getTotalPrice() * 0.5; // 50%
                    } else {
                        refund = 0.0; // No refund
                    }

                    // Delete the order — cascading should handle orderItems (if mapped correctly)
                    session.delete(order);
                    session.getTransaction().commit();

                    client.sendToClient(new responseDTO("OrderCancellationSuccess", new Object[]{refund}));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        client.sendToClient(new responseDTO("OrderCancellationFailure", new Object[]{"Server error during deletion."}));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } else if (msg instanceof reservationCancellationDTO) {
                reservationCancellationDTO cancelRequest = (reservationCancellationDTO) msg;

                try (Session session = getSessionFactory().openSession()) {
                    session.beginTransaction();

                    Reservation reservation = session.get(Reservation.class, cancelRequest.getOrderId());

                    if (reservation == null || !reservation.getCustomerId().equals(cancelRequest.getCustomerId())) {
                        client.sendToClient(new responseDTO("OrderCancellationFailure", new Object[]{"Order not found or customer ID mismatch."}));
                        return;
                    }

                    // Calculate refund based on delivery time
                    LocalDateTime now = LocalDateTime.now();
                    String date = reservation.getDate();
                    String time = reservation.getTime();

                    LocalDateTime reservationDateTime = LocalDateTime.parse(
                            date + "T" + time + ":00"  // Format: "2025-04-04T10:00:00"
                    );
                    long hoursDifference = ChronoUnit.HOURS.between(now, reservationDateTime);
                    int fine;

                    if (hoursDifference >= 1)
                        fine = 0;
                    else
                        fine = reservation.getNumberOfGuests() * 10;

                    // Delete the order — cascading should handle orderItems (if mapped correctly)
                    session.delete(reservation);
                    session.getTransaction().commit();

                    responseDTO responseForAll = new responseDTO("updateReservations", new Object[]{});
                    sendToAllExcept(responseForAll, client);
                    client.sendToClient(new responseDTO("ReservationCancellationSuccess", new Object[]{fine}));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        client.sendToClient(new responseDTO("ReservationCancellationFailure", new Object[]{"Server error during deletion."}));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

        }

    private String monthNameToNumber(String month) {
        Map<String, String> monthNameToNumber = new HashMap<>();
        monthNameToNumber.put("january", "01");
        monthNameToNumber.put("february", "02");
        monthNameToNumber.put("march", "03");
        monthNameToNumber.put("april", "04");
        monthNameToNumber.put("may", "05");
        monthNameToNumber.put("june", "06");
        monthNameToNumber.put("july", "07");
        monthNameToNumber.put("august", "08");
        monthNameToNumber.put("september", "09");
        monthNameToNumber.put("october", "10");
        monthNameToNumber.put("november", "11");
        monthNameToNumber.put("december", "12");

        String monthNumber = monthNameToNumber.get(month);
        return monthNumber;
    }

    public static void initializeSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();

            // Load hibernate.cfg.xml or hibernate.properties // i think this is useless
            // configuration.configure();

            // If you need to override password at runtime
            configuration.setProperty("hibernate.connection.password", newPassword);

            // Register entity classes
            configuration.addAnnotatedClass(Dish.class);
            configuration.addAnnotatedClass(Restaurant.class);
            configuration.addAnnotatedClass(Feedback.class);
            configuration.addAnnotatedClass(Order.class);
            configuration.addAnnotatedClass(OrderItem.class);
            configuration.addAnnotatedClass(Reports.class);
            configuration.addAnnotatedClass(RequestedChanges.class);
            configuration.addAnnotatedClass(Reservation.class);
            configuration.addAnnotatedClass(Tables.class);
            configuration.addAnnotatedClass(User.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
    }

    // Access the singleton
    public static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            initializeSessionFactory();
        }
        return sessionFactory;
    }

    private static void updateDish(int id, String price) throws Exception {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            Dish dishToUpdate = session.get(Dish.class, id);

            dishToUpdate.setPrice(price);

            session.update(dishToUpdate);


            session.getTransaction().commit(); // Save everything.

        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            session.close();
        }
    }

    private static void remove(int id) throws Exception {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            Dish dishToRemove = session.get(Dish.class, id);
            if (dishToRemove != null) {
                session.delete(dishToRemove);
            }
            session.getTransaction().commit(); // Save everything.

        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static List<String> getAvailableHours(List<Reservation> reservations,List<Tables> tables,String  restaurantName,String numberOfGuest,LocalDate date) {
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();

        Query<Restaurant> query = session.createQuery("FROM Restaurant WHERE name = :name", Restaurant.class);
        query.setParameter("name", "Golden Gate Bites");
        Restaurant restaurant = query.uniqueResult(); // Returns null if not found, or the restaurant if found

        Hibernate.initialize(restaurant.getOpeningHours());
        session.getTransaction().commit();
        session.close();

        String hours = restaurant.getOpeningHourForDay(date.getDayOfWeek().toString().toLowerCase());

        String[] parts = hours.split("-");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime start = LocalTime.parse(parts[0], formatter);
        LocalTime end = LocalTime.parse(parts[1], formatter).minusHours(1);

        List<String> availableHours = new ArrayList<>();
        int requestedGuests = Integer.parseInt(numberOfGuest);

        // Iterate from start to end in 15-minute steps
        while (!start.isAfter(end)) {
            LocalTime currentTime = start;

            // Create lists for available tables based on seating capacity
            List<Tables> availableTables2 = new ArrayList<>();
            List<Tables> availableTables3 = new ArrayList<>();
            List<Tables> availableTables4 = new ArrayList<>();

            for (Tables table : tables) {
                boolean isAvailable = true;

                // Check if this table is already reserved at this time or an hour before
                for (Reservation reservation : reservations) {
                    LocalTime reservationTime = LocalTime.parse(reservation.getTime(), formatter);
                    List<Integer> reservedTableIds = reservation.getTable();
                    if (reservedTableIds.contains(table.getId())) {
                        // Check if the reservation overlaps with current time
                        // (reservation time is within 1 hour before or after current time)
                        if (reservationTime.equals(currentTime) ||
                                (reservationTime.isBefore(currentTime) &&
                                        reservationTime.plusHours(1).isAfter(currentTime)) ||
                                (reservationTime.isAfter(currentTime) &&
                                        reservationTime.isBefore(currentTime.plusHours(1)))) {
                            isAvailable = false;
                            break;
                        }
                    }
                }
                // If the table is available, add it to the appropriate list
                if (isAvailable) {
                    if (table.getSeats() == 2) {
                        availableTables2.add(table);
                    } else if (table.getSeats() == 3) {
                        availableTables3.add(table);
                    } else if (table.getSeats() == 4) {
                        availableTables4.add(table);
                    }
                }
            }
            int totalCapacity = 0;
            List<Tables> allAvailableTables = new ArrayList<>();
            allAvailableTables.addAll(availableTables2);
            allAvailableTables.addAll(availableTables3);
            allAvailableTables.addAll(availableTables4);

            // Sort tables by capacity (largest first) for optimal allocation
            Collections.sort(allAvailableTables, (t1, t2) -> Integer.compare(t2.getSeats(), t1.getSeats()));

            // Check if we have enough capacity for the requested guests
            boolean hasEnoughCapacity = false;

            if (requestedGuests <= 2 && !availableTables2.isEmpty()) {
                // Small party - can use any available table
                hasEnoughCapacity = true;
            } else if (requestedGuests <= 3 && (!availableTables3.isEmpty() || !availableTables4.isEmpty())) {
                // Medium party - need table for 3+ people
                hasEnoughCapacity = true;
            } else if (requestedGuests <= 4 && !availableTables4.isEmpty()) {
                // Larger party - need table for 4 people
                hasEnoughCapacity = true;
            } else if (requestedGuests > 4) {
                // Very large party - need to combine tables
                int cumulativeCapacity = 0;
                for (Tables table : allAvailableTables) {
                    cumulativeCapacity += table.getSeats();
                    if (cumulativeCapacity >= requestedGuests) {
                        hasEnoughCapacity = true;
                        break;
                    }
                }
            }

            if (hasEnoughCapacity) {
                availableHours.add(currentTime.format(formatter));
            }

            // Move to the next time slot
            start = start.plusMinutes(15);
        }


        return availableHours;
    }

    public static List<Integer> confirmReservations(String name,String number,String creditCard,String email,String time,
                                                    LocalDate date, String numberOfGuest,String restaurantName,String inOrOut, String customerId){
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        String hql1 = "FROM Reservation r WHERE r.restaurant = :givenRestaurantName AND r.date = :givenDate";

        List<Reservation> reservations = null;
        try {
            Query<Reservation> query1 = session.createQuery(hql1, Reservation.class);
            query1.setParameter("givenRestaurantName", restaurantName);
            query1.setParameter("givenDate", date.toString());
            reservations = query1.list();
            System.out.println("[DEBUG] Got " + reservations.size() + " reservations " + date.toString()+ " " + restaurantName);
        } catch (Exception e) {
            System.out.println("[DEBUG] Query error: " + e.getMessage());
            e.printStackTrace();
        }

        for (Reservation reservation : reservations) {
            Hibernate.initialize(reservation.getTable());
        }

        String hql2 = "FROM Tables r WHERE r.restaurant = :givenRestaurantName AND r.tableIn = :isInTable";

        Query query2 = session.createQuery(hql2);
        query2.setParameter("givenRestaurantName", restaurantName);
        if(inOrOut.equals("Inside"))
            query2.setParameter("isInTable", true);
        else
            query2.setParameter("isInTable", false);
        List<Tables> tables = query2.list();
        for (Tables table : tables) {
            Hibernate.initialize(table.getReservations());
        }
        session.getTransaction().commit();
        session.close();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.parse(time, formatter);

        int requestedGuests = Integer.parseInt(numberOfGuest);
        List<Tables> availableTables2 = new ArrayList<>();
        List<Tables> availableTables3 = new ArrayList<>();
        List<Tables> availableTables4 = new ArrayList<>();

        for (Tables table : tables) {
            boolean isAvailable = true;

            // Check if this table is already reserved at this time or an hour before
            for (Reservation reservation : reservations) {
                LocalTime reservationTime = LocalTime.parse(reservation.getTime(), formatter);
                List<Integer> reservedTableIds = reservation.getTable();
                if (reservedTableIds.contains(table.getId())) {
                    // Check if the reservation overlaps with current time
                    // (reservation time is within 1 hour before or after current time)
                    if (reservationTime.equals(currentTime) ||
                            (reservationTime.isBefore(currentTime) &&
                                    reservationTime.plusHours(1).isAfter(currentTime)) ||
                            (reservationTime.isAfter(currentTime) &&
                                    reservationTime.isBefore(currentTime.plusHours(1)))) {
                        isAvailable = false;
                        break;
                    }
                }
            }
            // If the table is available, add it to the appropriate list
            if (isAvailable) {
                if (table.getSeats() == 2) {
                    availableTables2.add(table);
                } else if (table.getSeats() == 3) {
                    availableTables3.add(table);
                } else if (table.getSeats() == 4) {
                    availableTables4.add(table);
                }
            }
        }

        List<Integer> selectedTableIds = new ArrayList<>();
        int remainingGuests = requestedGuests;

        // First, allocate 4-seat tables as much as possible
        // But stop if remaining guests are 2 or 3 (to give them appropriate sized tables)
        for (Tables table : availableTables4) {
            if (    (remainingGuests <= 0)||
                    (remainingGuests == 3 && !availableTables3.isEmpty()) ||
                    (remainingGuests <= 2 && !availableTables3.isEmpty() && !availableTables2.isEmpty())) break;
            selectedTableIds.add(table.getId());
            remainingGuests -= 4;
        }
        if (remainingGuests > 0) {
            for (Tables table : availableTables3) {
                if ((remainingGuests <= 0 ) || (remainingGuests <= 2 && !availableTables2.isEmpty())) break;
                selectedTableIds.add(table.getId());
                remainingGuests -= 3;
            }
        }
        if (remainingGuests > 0) {
            for (Tables table : availableTables2) {
                if (remainingGuests <= 0) break;
                selectedTableIds.add(table.getId());
                remainingGuests -= 2;
            }
        }
        if (remainingGuests > 0) {
            // Not enough tables available
            return new ArrayList<>(); // Return empty list to indicate failure
        }
        sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();

        Reservation newReservation = new Reservation(selectedTableIds, date.toString(), time, restaurantName, name, email,
                creditCard,number,Integer.parseInt(numberOfGuest),customerId);

        for (int table_id : selectedTableIds) {
            Tables table = session.get(Tables.class, table_id);
            table.addReservation(newReservation.getId());
            session.update(table);
        }

        session.save(newReservation);
        session.getTransaction().commit();
        session.close();

        return selectedTableIds;
    }

    private void sendToAllExcept(Object msg, ConnectionToClient excludeClient) {
        // Get a list of all client connections
        Thread[] clientThreadList = getClientConnections();

        for (int i = 0; i < clientThreadList.length; i++) {
            if (clientThreadList[i] instanceof ConnectionToClient) {
                ConnectionToClient client = (ConnectionToClient) clientThreadList[i];

                // Skip the excluded client
                if (client.equals(excludeClient)) {
                    continue;
                }

                // Send to this client
                try {
                    client.sendToClient(msg);
                } catch (IOException e) {
                    System.out.println("Error sending to client: " + e.getMessage());
                }
            }
        }
    }
    private List<DataPoint> convertFeedbackToDataPoints(List<Feedback> feedbackList) {
        // Count feedback submissions by month
        Map<Integer, Integer> feedbackCountByMonth = new HashMap<>();

        // Initialize all months with zero count
        for (int i = 1; i <= 12; i++) {
            feedbackCountByMonth.put(i, 0);
        }

        // Count feedback for each month
        for (Feedback feedback : feedbackList) {
            if (feedback.getSubmittedAt() != null) {
                int month = feedback.getSubmittedAt().getMonthValue();
                feedbackCountByMonth.put(month, feedbackCountByMonth.get(month) + 1);
            }
        }

        // Convert the map to a list of DataPoints
        List<DataPoint> dataPoints = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            dataPoints.add(new DataPoint(month, feedbackCountByMonth.get(month)));
        }

        return dataPoints;
    }
    public List<Reservation> getCurrentReservationsWithRoundedTime() {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Extract current date
        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Extract and round up the current time
        LocalTime currentTime = now.toLocalTime();
        int minute = currentTime.getMinute();
        int roundedMinute;
        int hour = currentTime.getHour();

        // Round up to nearest 15-minute interval
        if (minute == 0) {
            roundedMinute = 0;
        } else if (minute <= 15) {
            roundedMinute = 15;
        } else if (minute <= 30) {
            roundedMinute = 30;
        } else if (minute <= 45) {
            roundedMinute = 45;
        } else {
            roundedMinute = 0;
            hour = (hour + 1) % 24;  // Handle hour rollover
        }

        // Format the rounded time as string (HH:MM)
        String roundedTimeString = String.format("%02d:%02d", hour, roundedMinute);

        // Begin transaction
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        // Query to get reservations for current date and rounded time
        String hql = "FROM Reservation r WHERE r.date = :currentDate AND r.time = :roundedTime";
        List<Reservation> reservations = session.createQuery(hql, Reservation.class)
                .setParameter("currentDate", currentDate)
                .setParameter("roundedTime", roundedTimeString)
                .list();
        for (Reservation reservation : reservations) {
            Hibernate.initialize(reservation.getTable());
        }
        session.getTransaction().commit();
        session.close();

        return reservations;
    }

}


