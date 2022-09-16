package pl.edu.agh.ki.lab.to.yourflights.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DestinationController {

    private final ApplicationContext applicationContext;

    private String destination;

    @FXML
    private Text placeOfDestination;

    public DestinationController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setData(String destination) {
        this.destination = destination;
        placeOfDestination.setText(destination);
    }
}
