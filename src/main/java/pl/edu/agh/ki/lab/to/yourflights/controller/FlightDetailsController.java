package pl.edu.agh.ki.lab.to.yourflights.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.service.FlightService;
import pl.edu.agh.ki.lab.to.yourflights.service.ReservationService;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class FlightDetailsController {

    private final Resource flightsView;
    private final ApplicationContext applicationContext;

    private Flight flight;

    private final ReservationService reservationService;

    private final FlightService flightService;

    @FXML
    private Text placeOfDeparture;
    @FXML
    private Text placeOfDestination;
    @FXML
    private Text airline;
    @FXML
    private Text departureTime;
    @FXML
    private Text arrivalTime;


    @FXML
    private Text numberOfReservations;
    @FXML
    private Text numberOfFreeSeats;
    @FXML
    private Text numberOfSeats;
    @FXML
    private Text numberOfTakenSeats;
    @FXML
    private Text flightTotalIncome;



    public FlightDetailsController(ApplicationContext applicationContext,
                                   @Value("classpath:/view/FlightView.fxml") Resource flightsView,
                                   ReservationService reservationService,
                                   FlightService flightService) {
        this.applicationContext = applicationContext;
        this.flightsView = flightsView;
        this.reservationService = reservationService;
        this.flightService = flightService;
    }

    /**
     * Metoda ustawiająca lot
     * @param flight lot
     */
    public void setData(Flight flight) {
        this.flight = flight;
        updateFields();
    }

    /**
     * Metoda ustawiająca wartości pól tekstowych detalach lotu
     */
    private void updateFields() {
        placeOfDeparture.setText(flight.getPlaceOfDeparture());
        placeOfDestination.setText(flight.getPlaceOfDestination());
        airline.setText(flight.getAirline().getName());
        departureTime.setText(flight.getDepartureDate() + " " + flight.getDepartureTime());
        arrivalTime.setText(flight.getArrivalDate() + " " + flight.getArrivalTime());

        if(flightTotalIncome != null) {
            flightTotalIncome.setText(flightService.getTotalIncomeFromFlight(flight).toString());
        }

        if(numberOfReservations != null) {
            numberOfReservations.setText(Integer.toString(flightService.getNumberOfReservationsForFlight(flight)));
        }

        if(numberOfSeats != null) {
            numberOfSeats.setText(Integer.toString(flightService.getNumberOfSeatsForFlight(flight)));
        }

        if(numberOfTakenSeats != null) {
            numberOfTakenSeats.setText(Integer.toString(flightService.getNumberOfTakenSeatsForFlight(flight)));
        }

        if(numberOfFreeSeats != null){
            numberOfFreeSeats.setText(Integer.toString(Integer.parseInt(numberOfSeats.getText()) - Integer.parseInt(numberOfTakenSeats.getText())));
        }
    }

    /**
     * Metoda służąca do przejścia do widoku lotów
     * @param actionEvent event emitowany przez przycisk
     */
    public void showFlightsView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(flightsView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
