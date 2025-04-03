package il.cshaifasweng.OCSFMediatorExample.client;

//import il.cshaifasweng.OCSFMediatorExample.entities.DTO.Events.ReservationCancellationResultEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.reservationCancellationResultEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.reservationCancellationDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.reservationSummaryDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class viewReservationsController {

    @FXML private TextField idField;
    @FXML private Button searchButton;
    @FXML private TableView<reservationSummaryDTO> reservationsTable;
    @FXML private TableColumn<reservationSummaryDTO, String> reservationIdColumn;
    @FXML private TableColumn<reservationSummaryDTO, String> dateColumn;
    @FXML private TableColumn<reservationSummaryDTO, String> numberOfGuestsColumn;
    @FXML private TableColumn<reservationSummaryDTO, Void> actionColumn;
    @FXML private Label messageLabel;

    private ObservableList<reservationSummaryDTO> reservationList = FXCollections.observableArrayList();

    public viewReservationsController() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void initialize() {
        reservationIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        numberOfGuestsColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getNumberOfGuests())));
        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDate() + " " + data.getValue().getTime()));

        reservationsTable.setItems(reservationList);
        addCancelButtonToTable();

        searchButton.setOnAction(event -> fetchReservationsById());
    }

    private void fetchReservationsById() {
        String id = idField.getText();
        if (id.isEmpty()) {
            messageLabel.setText("Please enter your ID.");
            return;
        }

        try {
            SimpleClient.getClient().sendToServer("getReservationsByCustomerId:" + id);
            messageLabel.setText("Loading reservations...");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to request reservations.");
        }
    }

    private void addCancelButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelButton = new Button("Cancel");

            {
                cancelButton.setOnAction(event -> {
                    reservationSummaryDTO reservation = getTableView().getItems().get(getIndex());
                    try {
                        reservationCancellationDTO cancelDTO = new reservationCancellationDTO(reservation.getId(), idField.getText());
                        SimpleClient.getClient().sendToServer(cancelDTO);
                        messageLabel.setText("Processing cancellation...");
                    } catch (IOException e) {
                        messageLabel.setText("Cancellation failed.");
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cancelButton);
            }
        });
    }

    @Subscribe
    public void handleCancellationResult(reservationCancellationResultEvent event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reservation Cancellation");
            alert.setHeaderText(null);
            if (event.getMessage() == 0 )
                alert.setContentText("The reservation was canceled, and there is no fine.");
            else
                alert.setContentText("The reservation was canceled, and you have been fined:" +String.valueOf(event.getMessage()));
            alert.showAndWait();
        });

        try {
            SimpleClient.getClient().sendToServer("getReservationsByCustomerId:" + idField.getText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void handleReservations(List<reservationSummaryDTO> event) {
        if (event == null || event.isEmpty()) {
            System.out.println("No reservations available.");
            reservationList.setAll(event);
            return;
        }

        System.out.println("Received " + event.size() + " reservations from server.");
        reservationList.setAll(event);
    }

    public void displayReservations(List<reservationSummaryDTO> reservations) {
        reservationList.setAll(reservations);
        messageLabel.setText("Found " + reservations.size() + " reservations.");
    }

    @FXML
    public void handleBack() {
        onClose();
        SimpleClient.getClient().navigateTo("startPageView");
    }

    public void displayMessage(String msg) {
        messageLabel.setText(msg);
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }
}