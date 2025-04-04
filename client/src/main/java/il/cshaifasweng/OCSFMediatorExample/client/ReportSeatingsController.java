package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.Report;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ReportSeatingsController {

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<Report, String> dayColumn;

    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Report> table;

    @FXML
    private TableColumn<Report, String> numberOfSeatingsColumn;

    private ObservableList<String> monthList = FXCollections.observableArrayList();
    private ObservableList<Report> seatingReportList = FXCollections.observableArrayList();




    @FXML
    void searchAction(ActionEvent event) throws IOException {
        if(monthComboBox.getValue().isEmpty()) {
            messageLabel.setText("Please select a month");
            return;
        }
        responseDTO response = new responseDTO("getSeatingReport",new Object[]{monthComboBox.getValue().trim().toLowerCase()});
        SimpleClient.getClient().sendToServer(response);
    }

    @FXML
    public void initialize() {
        messageLabel.setText("");
        dayColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDay())));
        numberOfSeatingsColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getNumber())));
        table.setItems(seatingReportList);


        EventBus.getDefault().register(this);  // Register for EventBus updates

        LocalDate today = LocalDate.now();
        // Get current month (1-based, January is 1)
        int currentMonth = today.getMonthValue();

        // Add months from January to the month before current
        for (int i = 1; i < currentMonth; i++) {
            // Convert month number to Month enum and get its display name
            String monthName = Month.of(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            monthList.add(monthName);
        }
        monthComboBox.setItems(monthList);
    }

    @FXML
    public void backAction() {
        onClose();
        SimpleClient.getClient().navigateTo("adminHomeView");
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void loadReport(List<Report> event) {
        System.out.println("EventBus triggered loadReport()!");
        if (event == null || event.isEmpty()) {
            System.out.println("[ERROR] Should not be here!");
            seatingReportList.setAll(event);
            return;
        }

        seatingReportList.setAll(event);

    }
}
