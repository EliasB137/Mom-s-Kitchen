package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.time.Month;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.client.events.DataPointEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.DataPoint;
import il.cshaifasweng.OCSFMediatorExample.entities.DTO.responseDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ReportFeedbackController {

    @FXML
    private Button backButton;

    @FXML
    private LineChart<Integer, Integer> feedbackChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;


    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);

        // Initialize chart components
        Platform.runLater(() -> {
            xAxis.setLabel("Month");
            yAxis.setLabel("Number of Feedbacks");
            feedbackChart.setTitle("Feedback Submissions by Month");

            // Set x-axis ticks to show month numbers clearly
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(1);
            xAxis.setUpperBound(12);
            xAxis.setTickUnit(1);
            xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
                @Override
                public String toString(Number value) {
                    int month = value.intValue();
                    if (month >= 1 && month <= 12) {
                        return Month.of(month).toString().substring(0, 3);
                    }
                    return super.toString(value);
                }
            });
        });

        // Request data from server
        responseDTO response = new responseDTO("getFeedBackReport", new Object[]{SimpleClient.getUserID(), SimpleClient.getSelectedRestaurant()});
        try {
            SimpleClient.getClient().sendToServer(response);
        } catch (IOException e) {
            Platform.runLater(() -> {
                // Handle error on JavaFX thread - you could show an alert dialog here
                System.err.println("Error sending request to server: " + e.getMessage());
            });
        }
    }

    @Subscribe
    public void loadReport(DataPointEvent eventList) {
        List<DataPoint> event = eventList.getPayload();
        System.out.println("EventBus triggered loadReport()!");

        // Handle UI updates on JavaFX Application Thread
        Platform.runLater(() -> {
            if (event == null || event.isEmpty()) {
                System.out.println("[ERROR] Should not be here!");
                return;
            }
            populateChart(event);
        });
    }

    private void populateChart(List<DataPoint> dataPoints) {
        // Create a series for the chart
        XYChart.Series<Integer, Integer> series = new XYChart.Series<>();
        series.setName("Feedback Count");

        // Add data points to the series
        for (DataPoint point : dataPoints) {
            series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
        }

        // Clear existing data and add the new series
        feedbackChart.getData().clear();
        feedbackChart.getData().add(series);
    }

    @FXML
    public void backAction() {
        onClose();
        SimpleClient.getClient().navigateTo("ReportsPickView");
    }

    public void onClose() {
        EventBus.getDefault().unregister(this);
    }
}