package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DTO.dishDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class updateDishController {

    @FXML
    private TextField Textbox;

    @FXML
    private Button UpdatePrice;

    @FXML
    private Label dishID;

    @FXML
    private Label dishIngredients;

    @FXML
    private Label dishName;

    @FXML
    private Label dishPersonalPrefrence;

    @FXML
    private Label dishPrice;

    @FXML
    private Label priceupdated;

    @FXML
    private Button goBackToMenu;

    @FXML
    void onClickUpdatePrice(ActionEvent event) throws IOException {
        String newPrice = Textbox.getText();
        SimpleClient.getClient().sendToServer("updatePrice |"+ dishID.getText().substring(5) +"|"+ newPrice );
        priceupdated.setText("Price has been updated to " + newPrice);
    }

    @FXML
    void onclickGoBackToMenu(ActionEvent event) throws IOException {
        SimpleClient.getClient().sendToServer("getDishes");
    }

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void putDishDetails(dishDTO dish) {
        dishID.setText("ID : " + String.valueOf(dish.getId()));
        dishIngredients.setText("Ingredients : " + dish.getIngredients());
        dishName.setText("Name : " + dish.getName());
        dishPrice.setText("Price : " + dish.getPrice());
    }

}
