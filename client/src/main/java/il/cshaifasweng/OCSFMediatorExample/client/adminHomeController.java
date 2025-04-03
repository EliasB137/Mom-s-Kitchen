package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.LoginResultEvent;
//import il.cshaifasweng.OCSFMediatorExample.client.events.LogoutResponseEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.LogoutResultEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class adminHomeController {

    private String role;
    @FXML
    private Label accesLabel;

    @FXML
    private Button aproveChangesButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Button reserveTableButton;

    @FXML
    private Button reviewFeedbackButton;

    @FXML
    private Button updateMenuButton;

    @FXML
    private Button viewMapButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    void logOutAction(ActionEvent event) {
        try {
            responseDTO message = new responseDTO("logOutRequest", new Object[]{SimpleClient.getUserID()});
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            Platform.runLater(() -> {
                accesLabel.setText("Error connecting to server.");
            });
            e.printStackTrace();
        }
    }

    @FXML
    void approveChangesAction(ActionEvent event) {
//        if(!role.equals("manager")){
//            accesLabel.setText("You dont have access for this action.");
//            return;
//        }
        SimpleClient.getClient().navigateTo("ApproveChangesView");
    }

    @FXML
    void reserveTableAction(ActionEvent event) {
//        if(!role.equals("worker")){
//            accesLabel.setText("You dont have access for this action.");
//            return;
//        }
        SimpleClient.getClient().navigateTo("ReserveTableAdminView");
    }

    @FXML
    void reviewFeedbackAction(ActionEvent event) {
//        if(((!role.equals("customer care")) && (!role.equals("manager")))){
//            accesLabel.setText("You dont have access for this action.");
//            return;
//        }
        SimpleClient.getClient().navigateTo("ReviewFeedbackView");
    }

    @FXML
    void updateMenuAction(ActionEvent event) {
//        if(!role.equals("dietitian ")){
//            accesLabel.setText("You dont have access for this action.");
//            return;
//        }
        SimpleClient.getClient().navigateTo("UpdateMenuView");
    }

    @FXML
    void viewMapAction(ActionEvent event) {
//        if(!role.equals("worker")){
//            accesLabel.setText("You dont have access for this action.");
//            return;
//        }
        SimpleClient.getClient().navigateTo("ViewMapView");
    }

    @FXML
    void viewReportsAction(ActionEvent event) {
//        if(((!role.equals("worker")) && (!role.equals("manager")))){
//            accesLabel.setText("You dont have access for this action.");
//            return;
//        }
        SimpleClient.getClient().navigateTo("ViewReportsView");
    }

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        accesLabel.setText("");
        // Get the user role from SimpleClient
        role = SimpleClient.getUserRole();
        switch(role) {
            case "worker":
                aproveChangesButton.setVisible(false);
                reviewFeedbackButton.setVisible(false);
                updateMenuButton.setVisible(false);
                reserveTableButton.setVisible(true);
                viewMapButton.setVisible(true);
                viewReportsButton.setVisible(true);
                break;
            case "manager":
                aproveChangesButton.setVisible(true);
                reviewFeedbackButton.setVisible(true);
                updateMenuButton.setVisible(false);
                reserveTableButton.setVisible(false);
                viewMapButton.setVisible(false);
                viewReportsButton.setVisible(true);
                break;
            case "dietitian":
                aproveChangesButton.setVisible(false);
                reviewFeedbackButton.setVisible(false);
                updateMenuButton.setVisible(true);
                reserveTableButton.setVisible(false);
                viewMapButton.setVisible(false);
                viewReportsButton.setVisible(false);
                break;
            case "customer care":
                aproveChangesButton.setVisible(false);
                reviewFeedbackButton.setVisible(true);
                updateMenuButton.setVisible(false);
                reserveTableButton.setVisible(false);
                viewMapButton.setVisible(false);
                viewReportsButton.setVisible(false);
                break;
            default:
                System.out.println("[ERROR] Role doesnt exist: " + role);
                aproveChangesButton.setVisible(false);
                reviewFeedbackButton.setVisible(false);
                updateMenuButton.setVisible(false);
                reserveTableButton.setVisible(false);
                viewMapButton.setVisible(false);
                viewReportsButton.setVisible(false);
        }
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void handleLogoutResponse(LogoutResultEvent event) {
        String message = event.getMessage();
        Platform.runLater(() -> {
            if (message.equals("logoutSuccessful")) {
                SimpleClient.setUserID(0); // Reset user ID
                SimpleClient.setUserRole(null); // Reset user role
                SimpleClient.getClient().navigateTo("startPageView");
            } else {
                accesLabel.setText("Logout failed: " + event.getMessage());
            }
        });
    }

}