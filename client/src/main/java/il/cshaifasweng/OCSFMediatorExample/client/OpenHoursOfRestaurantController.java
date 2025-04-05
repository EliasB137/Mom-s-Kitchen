package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class OpenHoursOfRestaurantController {

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<DayWithHours, String> dayColumn;

    @FXML
    private TableColumn<DayWithHours, String> hoursOpenColumn;

    @FXML
    private TableView<DayWithHours> tableHours;

    private ObservableList<DayWithHours> dayWithHours = FXCollections.observableArrayList();

    @FXML
    public void backAction() {
        onClose();
        SimpleClient.getClient().navigateTo("startPageView");
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }
    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        dayColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getX()));
        hoursOpenColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getY()));
        tableHours.setItems(dayWithHours);

        responseDTO response = new responseDTO("getOpeningHours",new Object[]{SimpleClient.getSelectedRestaurant()});
        try {
            SimpleClient.getClient().sendToServer(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Subscribe
    public void handleHours(List<DayWithHours> event) {
        dayWithHours.setAll(event);
    }

}
