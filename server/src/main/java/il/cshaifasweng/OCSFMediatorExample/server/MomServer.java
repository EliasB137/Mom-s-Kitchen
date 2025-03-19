package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.client.MenuResponse;
import il.cshaifasweng.OCSFMediatorExample.client.RestaurantListResponse;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
        try {
            if(getAllDishes().isEmpty())
            {
                session = sessionFactory.openSession();
                session.beginTransaction();

                Dish dish = new Dish(
                        "Spaghetti Carbonara",
                        "Spaghetti, Eggs, Pecorino Romano cheese, pancetta, and black pepper",
                        "Extra Cheese",
                        "72.00",
                        "https://example.com/spaghetti_carbonara.jpg",  // Image URL
                        true // Available for delivery
                );
                session.save(dish);
                session.flush();

                dish = new Dish(
                        "Caesar Salad",
                        "Romaine lettuce, croutons, and Caesar dressing",
                        "No Croutons",
                        "48.00",
                        "https://example.com/caesar_salad.jpg",
                        false
                );
                session.save(dish);
                session.flush();

                dish = new Dish(
                        "Fettuccine Alfredo",
                        "Fettuccine pasta, butter, heavy cream, and Parmesan cheese",
                        "Extra Creamy Sauce",
                        "64.00",
                        "https://example.com/fettuccine_alfredo.jpg",
                        true
                );
                session.save(dish);
                session.flush();

                dish = new Dish(
                        "Lasagna",
                        "Pasta sheets, meat or vegetable sauce, ricotta, and mozzarella cheese, topped with marinara sauce",
                        "Vegetarian Option",
                        "78.00",
                        "https://example.com/lasagna.jpg",
                        true
                );
                session.save(dish);
                session.flush();

                dish = new Dish(
                        "Ravioli",
                        "Pasta ravioli stuffed with fillings like ricotta cheese, spinach, or meat, served with marinara or Alfredo sauce",
                        "Marinara Sauce Only",
                        "65.00",
                        "https://example.com/ravioli.jpg",
                        false
                );
                session.save(dish);
                session.flush();

                session.getTransaction().commit();
                session.close();

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            session.close();
        }

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

                Restaurant restaurant = new Restaurant("Golden Gate Bites", "Downtown");
                session.save(restaurant);
                session.flush();

                System.out.println("Test restaurant added: " + restaurant.getName());

                // Check if any dish exists
                List<Dish> dishList = session.createQuery("FROM Dish", Dish.class).getResultList();
                if (dishList.isEmpty()) {
                    System.out.println("No dishes found! Adding test dish...");
                    Dish dish = new Dish(
                            "pizza",
                            "Spaghetti, Eggs, Pecorino Romano cheese, pancetta, and black pepper",
                            "Extra Cheese",
                            "72.00",
                            "https://i.imgur.com/vIZKDQW.jpeg",
                            true
                    );

                    session.save(dish);
                    session.flush();
                     dish = new Dish(
                            "Spaghetti Carbonara",
                            "Spaghetti, Eggs, Pecorino Romano cheese, pancetta, and black pepper",
                            "Extra Cheese",
                            "72.00",
                            "https://i.imgur.com/oBoLh3W.jpeg",
                            true
                    );

                    session.save(dish);
                    session.flush();

                    System.out.println("Test dish added: " + dish.getName());

                    // Link dish to the restaurant in menu_dish
                    MenuDish menuDish = new MenuDish(restaurant, dish);
                    session.save(menuDish);
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
                List<MenuDish> menuDishes = session.createQuery("FROM MenuDish md JOIN FETCH md.restaurant", MenuDish.class).getResultList();


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

                if (menuDishes.isEmpty()) {
                    System.out.println("[ERROR] No menu links found in database.");
                } else {
                    System.out.println("[DEBUG] Total Menu Links in DB: " + menuDishes.size());
                    for (MenuDish md : menuDishes) {
                        System.out.println("Dish: " + md.getDish().getName() + " -> Restaurant: " + md.getRestaurant().getName());
                    }
                }

                MenuResponse response = new MenuResponse(dishes, menuDishes);
                client.sendToClient(response);

            } catch (Exception e) {
                System.err.println("ERROR: Failed to fetch dishes!");
                e.printStackTrace();
            }
        }



        else if (msgString.equals("getRestaurants")) {
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

            // Send response to client
            RestaurantListResponse response = new RestaurantListResponse(restaurantList);
            if (client != null && client.isAlive()) {
                try {
                    client.sendToClient(response);
                }
                catch (IOException e) {}
            }
        }


        else if (msgString.startsWith("getMenuForRestaurant:")) {
            String restaurantName = msgString.replace("getMenuForRestaurant:", "").trim();
            System.out.println("Server received 'getMenuForRestaurant' request for: " + restaurantName);

            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                List<MenuDish> menuDishes = session.createQuery(
                                "SELECT md FROM MenuDish md JOIN FETCH md.restaurant WHERE md.restaurant.name = :restaurantName",
                                MenuDish.class)
                        .setParameter("restaurantName", restaurantName)
                        .getResultList();

                List<Dish> dishes = menuDishes.stream()
                        .map(MenuDish::getDish)
                        .distinct()
                        .collect(Collectors.toList());

                session.getTransaction().commit();
                session.close();

                System.out.println("[DEBUG] Preparing to send `MenuResponse` to client.");
                MenuResponse response = new MenuResponse(dishes, menuDishes);

                System.out.println("[DEBUG] Sending `MenuResponse`...");
                client.sendToClient(response);
                System.out.println("{DEBUG] `MenuResponse` sent successfully!");

            } catch (Exception e) {
                System.err.println("ERROR: Failed to fetch menu for restaurant!");
                e.printStackTrace();
            }
        }








        else if(msgString.startsWith("updatePrice")) {
            String[] parts = msgString.split("\\|");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[1]);
                try {
                    updateDish(id,parts[2]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else if(msgString.startsWith("addDish")) {
            String[] parts = msgString.split("\\|");
            if (parts.length == 5) {
                int id = Integer.parseInt(parts[1]);

            }
        }
    }
    private static List<Dish> getAllDishes() throws Exception {
        List<Dish> data = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();


            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Dish> query = builder.createQuery(Dish.class);
            query.from(Dish.class);
            data = session.createQuery(query).getResultList();

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
        return data;
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
            configuration.addAnnotatedClass(MenuDish.class);
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
