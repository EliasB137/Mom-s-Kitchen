package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.RequestedChangesDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class approveChangesController {

    @FXML private TableView<RequestedChangesDTO> changesTable;
    @FXML private TableColumn<RequestedChangesDTO, String> nameColumn;
    @FXML private TableColumn<RequestedChangesDTO, String> priceColumn;
    @FXML private TableColumn<RequestedChangesDTO, String> ingredientsColumn;
    @FXML private TableColumn<RequestedChangesDTO, Void> actionsColumn;
    @FXML private Label statusLabel;
    @FXML private Button backButton;

    private final ObservableList<RequestedChangesDTO> changeList = FXCollections.observableArrayList();

    public approveChangesController() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPrice())));
        ingredientsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIngredients()));
        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("adminHomeView"));
        changesTable.setItems(changeList);
        addActionButtons();
        requestPendingChanges();
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox buttons = new HBox(10, approveButton, rejectButton);

            {
                approveButton.setOnAction(event -> {
                    RequestedChangesDTO change = getTableView().getItems().get(getIndex());
                    sendApproval(change, true);
                });

                rejectButton.setOnAction(event -> {
                    RequestedChangesDTO change = getTableView().getItems().get(getIndex());
                    sendApproval(change, false);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void sendApproval(RequestedChangesDTO change, boolean approved) {
        try {
            responseDTO message = new responseDTO("processChangeRequest", new Object[]{change.getDishId(), approved});
            SimpleClient.getClient().sendToServer(message);
            changeList.remove(change); // Optimistically update UI
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> statusLabel.setText("Error sending approval decision."));
        }
    }

    private void requestPendingChanges() {
        try {
            SimpleClient.getClient().sendToServer("getPendingChanges");
        } catch (IOException e) {
            statusLabel.setText("Failed to request changes from server.");
        }
    }

    @Subscribe
    public void onReceivedPendingChanges(List<RequestedChangesDTO> event) {
        Platform.runLater(() -> {
            changeList.setAll(event);
            statusLabel.setText("Loaded " + event.size() + " requested changes.");
        });
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }
}
