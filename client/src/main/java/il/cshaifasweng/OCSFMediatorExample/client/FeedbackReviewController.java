package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.FeedbackDTO;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FeedbackReviewController {

    @FXML private TableView<FeedbackDTO> feedbackTable;
    @FXML private TableColumn<FeedbackDTO, String> nameColumn;
    @FXML private TableColumn<FeedbackDTO, String> emailColumn;
    @FXML private TableColumn<FeedbackDTO, String> messageColumn;
    @FXML private TableColumn<FeedbackDTO, String> deliveryColumn;
    @FXML private TableColumn<FeedbackDTO, String> respondedColumn;
    @FXML private TableColumn<FeedbackDTO, String> compensatedColumn;

    @FXML private ComboBox<String> filterComboBox;
    @FXML private CheckBox compensatedFilterCheckbox;
    @FXML private Button backButton;

    private ObservableList<FeedbackDTO> allFeedbacks = FXCollections.observableArrayList();
    private FeedbackDTO selectedFeedback;

    public static FeedbackDTO feedbackToRespond;

    public FeedbackReviewController() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        messageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFeedback()));
        deliveryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isDelivery() ? "Delivery" : "Dine-In"));
        respondedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getResponded() ? "Yes" : "No"));
        compensatedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCompensated() ? "Yes" : "No"));

        filterComboBox.setItems(FXCollections.observableArrayList("All", "Responded", "Not Responded"));
        filterComboBox.setValue("All");

        filterComboBox.setOnAction(e -> applyFilters());
        compensatedFilterCheckbox.setOnAction(e -> applyFilters());

        feedbackTable.setOnMouseClicked(this::handleFeedbackSelection);

        backButton.setOnAction(event -> SimpleClient.getClient().navigateTo("adminHomeView"));

        requestFeedbacksFromServer();
    }

    private void requestFeedbacksFromServer() {
        try {
            responseDTO request = new responseDTO("getAllFeedbacks", new Object[]{});
            SimpleClient.getClient().sendToServer(request);
        } catch (IOException e) {
            System.err.println("Error requesting feedbacks: " + e.getMessage());
        }
    }


    private void applyFilters() {
        String filterType = filterComboBox.getValue();
        boolean filterByCompensated = compensatedFilterCheckbox.isSelected();

        List<FeedbackDTO> filteredList = allFeedbacks.stream()
                .filter(fb -> {
                    if (filterType.equals("Responded") && !fb.getResponded()) return false;
                    if (filterType.equals("Not Responded") && fb.getResponded()) return false;
                    if (filterType.equals("Responded") && filterByCompensated && !fb.getCompensated()) return false;
                    return true;
                }).collect(Collectors.toList());

        feedbackTable.setItems(FXCollections.observableArrayList(filteredList));
        feedbackTable.refresh();
    }

    private void handleFeedbackSelection(MouseEvent event) {
        selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            feedbackToRespond = selectedFeedback; // Set the selected feedback statically
            SimpleClient.getClient().navigateTo("FeedbackResponseView"); // Navigate to response UI
        }
    }

    @Subscribe
    public void onFeedbackListReceived(List<FeedbackDTO> feedbackList) {
        if (feedbackList != null) {
            allFeedbacks.setAll(feedbackList);
            applyFilters();
        }
    }

    @Subscribe
    public void onFeedbackListUpdated(responseDTO event) {
        if (!"feedbackListUpdated".equals(event.getMessage())) return;

        Object[] payload = event.getPayload();
        if (payload.length > 0 && payload[0] instanceof List<?>) {
            List<?> rawList = (List<?>) payload[0];
            List<FeedbackDTO> updatedList = rawList.stream()
                    .filter(obj -> obj instanceof FeedbackDTO)
                    .map(obj -> (FeedbackDTO) obj)
                    .collect(Collectors.toList());

            allFeedbacks.setAll(updatedList);
            applyFilters();
        }
    }

}
