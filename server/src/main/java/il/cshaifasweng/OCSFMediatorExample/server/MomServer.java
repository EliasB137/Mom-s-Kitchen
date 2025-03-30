package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.*;
import il.cshaifasweng.OCSFMediatorExample.server.SavingInSql.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
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
    }






    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {

        String msgString = msg.toString();
        if (msgString.equals("getMenu")) {
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
        }
        else if (msgString.startsWith("getOrdersByCustomerId:")) {
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
        }


        else if (msg instanceof OrderCancellationDTO) {
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
            configuration.addAnnotatedClass(FeedBack.class);
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





}
