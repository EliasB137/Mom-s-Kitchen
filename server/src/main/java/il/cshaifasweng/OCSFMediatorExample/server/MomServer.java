package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.*;
import il.cshaifasweng.OCSFMediatorExample.server.SavingInSql.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
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
                            Arrays.asList("No Pepper", "Extra Cheese", "No tomato"), "72.00",
                            "https://cdn.loveandlemons.com/wp-content/uploads/2024/12/caesar-salad.jpg", true, Arrays.asList("Golden Gate Bites"));
                    session.save(dish);
                    session.flush();

                    dish = new Dish("Caesar Salad", "Romaine lettuce, croutons, and Caesar dressing",
                            Arrays.asList("No Pepper", "Extra Cheese", "No tomato"), "48.00",
                            "https://cdn.loveandlemons.com/wp-content/uploads/2024/12/caesar-salad.jpg", true, Arrays.asList("All"));


                    session.save(dish);
                    session.flush();
                    dish = new Dish("test1", "Romaine lettuce, croutons, and Caesar dressing",
                            Arrays.asList("No Pepper", "Extra Cheese", "No tomato"), "48.00",
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


                User user = new User("manager","manager","manager");
                session.save(user);
                session.flush();
                user = new User("worker","worker","worker");
                session.save(user);
                session.flush();
                user = new User("dietitian","dietitian","dietitian");
                session.save(user);
                session.flush();
                user = new User("customer care","customer care","customer care");
                session.save(user);
                session.flush();

                System.out.println("Linked " + table.getId() + " to " + restaurantAddTableTo.getName());
            }
            session.getTransaction().commit();
        }catch (Exception e) {
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

                Reservation reservation = new Reservation(Arrays.asList(2),"2025-04-04","10:00","Golden Gate Bites","tony","tonysabbah@gmail.com","123412341234,123","1234567890", 3,"213991516");
                session.save(reservation);
                session.flush();

                tableAddReservationTo.addReservation(reservation.getId());
                session.update(tableAddReservationTo);

                System.out.println("Linked " + reservation.getId() + " to " + tableAddReservationTo.getId());
            }
            session.getTransaction().commit();
        }catch (Exception e) {
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
        if(msg instanceof responseDTO){
            responseDTO message = (responseDTO) msg;
            String command = message.getMessage();
            Object[] payload = message.getPayload();
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
            else if (message.getMessage().equals("defineAsChainDish")) {

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
                        }
                        session.remove(change); // Delete whether approved or rejected
                    }

                    session.getTransaction().commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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
                            feedbackDTO.getFeedback() // the actual feedback message
                    );

                    session.save(feedbackEntity);
                    session.getTransaction().commit();

                    System.out.println(" Feedback saved successfully to database.");
                } catch (Exception e) {
                    System.err.println(" Error saving feedback: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if(command.equals("getHours"))
            {
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

                System.out.println("[DEBUG] Got  " + tables.size() + " tables");


                List<String> hours = getAvailableHours(reservations,tables,restaurantName,numberOfGuest,date);

                System.out.println("[DEBUG] Got " + hours.size() + " hours");

                responseDTO response = new responseDTO("availableHours",new Object[]{hours});
                try {
                    client.sendToClient(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if(command.equals("confirmReservation")) {
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

                List<Integer> result = confirmReservations(name,number,creditCard,email,time,date,numberOfGuest,restaurantName,inOrOut,customerId);

                if (!result.isEmpty()) {
                    responseDTO response = new responseDTO("reservationResult",new Object[]{result});
                    try {
                        client.sendToClient(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    responseDTO response = new responseDTO("reservationResult",new Object[]{});
                    try {
                        client.sendToClient(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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

                int userID =0;
                String userRole = "0";

                String reply ;
                if(user == null) {
                    reply = "notFound";
                }else if(user.getLoggedin()){
                    reply = "alreadyLoggedin";
                }else{
                    reply = "loginSuccessful";
                    userID = user.getId();
                    userRole = user.getRole();
                    user.setLoggedin(true);
                    session.update(user);
                }
                responseDTO response = new responseDTO("loginResult",new Object[]{reply,userID,userRole});
                session.getTransaction().commit();
                session.close();
                try {
                    client.sendToClient(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if(command.equals("logOutRequest")) {
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

                responseDTO response = new responseDTO("logoutResult",new Object[]{reply});

                session.getTransaction().commit();
                session.close();

                try {
                    client.sendToClient(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        } else if(msg instanceof String) {
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
            }

            else if (msgString.equals("getMenu")) {
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
            }else if (msgString.startsWith("getOrdersByCustomerId:")) {
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
        }

        else if (msg instanceof reservationCancellationDTO) {
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

                    if(hoursDifference  >= 1)
                        fine = 0;
                    else
                        fine = reservation.getNumberOfGuests()*10;

                    // Delete the order — cascading should handle orderItems (if mapped correctly)
                    session.delete(reservation);
                    session.getTransaction().commit();

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

}


