package pl.edu.agh.ki.lab.to.yourflights.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.service.FlightService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kontroler głównego widoku aplikacji
 * Z niego możemy przechodzić m.in. do widoku klientów albo do widoku przewoźników
 */
@Component
public class MainViewController {

    /**
     * Widoki
     */
    private final Resource airlinesView;
    private final Resource customersView;
    private final Resource flightView;
    private final Resource reservationListView;
    private final Resource loginView;
    private final Resource registrationView;
    private final Resource mainView;
    private final Resource anonymousAirlinesView;
    private final Resource anonymousFlightView;
    private final Resource userFlightView;
    private final Resource userAirlineView;
    private final Resource userCustomersView;
    private final Resource anonymousMainView;
    private final Resource registrationChoiceView;
    private final Resource destinationView;

    private FlightService flightService;

    @FXML
    private VBox flightsList;

    @FXML
    private HBox destinationsList;

    @FXML
    private Resource flightDetailsBrief;

    private List<Flight> flights;
    private List<String> destinations;

    /**
     * Kontekst aplikacji Springa
     */
    private final ApplicationContext applicationContext;

    /**
     * Konstruktor, Spring wstrzykuje odpowiednie zależności
     * @param applicationContext kontekst aplikacji Springa
     * @param airlinesView widok tabeli przewoźników
     * @param customersView widok tabeli klientów
     * @param reservationListView widok tabeli rezerwacji
     * @param loginView widok ekranu logowania
     */
    public MainViewController(ApplicationContext applicationContext,
                              FlightService flightService,
                              @Value("classpath:/view/AirlinesView.fxml") Resource airlinesView,
                              @Value("classpath:/view/CustomersView.fxml") Resource customersView,
                              @Value("classpath:/view/CustomersView.fxml") Resource mainView,
                              @Value("classpath:/view/FlightView.fxml") Resource flightView,
                              @Value("classpath:/view/ReservationListView.fxml") Resource reservationListView,
                              @Value("classpath:/view/AuthView/LoginView.fxml") Resource loginView,
                              @Value("classpath:/view/AuthView/RegistrationView.fxml") Resource registrationView,
                              @Value("classpath:/view/AnonymousView/AnonymousAirlinesView.fxml") Resource anonymousAirlinesView,
                              @Value("classpath:/view/AnonymousView/AnonymousFlightView.fxml") Resource anonymousFlightView,
                              @Value("classpath:/view/UserView/UserFlightView.fxml") Resource userFlightView,
                              @Value("classpath:/view/UserView/UserAirlinesView.fxml") Resource userAirlineView,
                              @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                              @Value("classpath:/view/UserView/UserCustomersView.fxml") Resource userCustomersView,
                              @Value("classpath:/view/AuthView/RegistrationChoiceView.fxml") Resource registrationChoiceView,
                              @Value("classpath:/view/MainView/FlightDetailsBrief.fxml") Resource flightDetailsBrief,
                              @Value("classpath:/view/MainView/Destination.fxml") Resource destinationView) {
        this.applicationContext = applicationContext;
        this.airlinesView = airlinesView;
        this.customersView = customersView;
        this.flightView=flightView;
        this.mainView = mainView;
        this.reservationListView = reservationListView;
        this.loginView = loginView;
        this.registrationView = registrationView;
        this.anonymousAirlinesView = anonymousAirlinesView;
        this.anonymousFlightView = anonymousFlightView;
        this.userAirlineView = userAirlineView;
        this.userFlightView = userFlightView;
        this.userCustomersView = userCustomersView;
        this.anonymousMainView = anonymousMainView;
        this.registrationChoiceView = registrationChoiceView;
        this.flightDetailsBrief = flightDetailsBrief;
        this.flightService = flightService;
        this.destinationView = destinationView;
    }

    /**
     * Metoda wywoływana po inicjalizacji widoku
     */
    @FXML
    public void initialize() {
        this.setFlights();
        this.setFlightsList();
        this.setDestinations();
    }

    private void setFlights() {
        flights = flightService.getFlightsSortedDescendingBasedOnNumberOfReservations().stream().limit(3).collect(Collectors.toList());
        this.showFlightDetailsView(flights);
    }
    private void setDestinations() {
        destinations = flightService.getFlightDestinationsSortedDescendingBasedOnNumberOfReservations().stream().limit(5).collect(Collectors.toList());
        this.showMostPopularDestinations(destinations);
    }

    private void setFlightsList() {

    }

    public void showMostPopularDestinations(List<String> destinations) {
        try {
            for(String destination : destinations) {
                FXMLLoader fxmlloader;
                fxmlloader = new FXMLLoader(destinationView.getURL());
                fxmlloader.setControllerFactory(applicationContext::getBean);
                Parent parent = fxmlloader.load();
                if(destination != null) {
                    DestinationController controller = fxmlloader.getController();
                    controller.setData(destination);
                }
                this.destinationsList.getChildren().add(parent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void showFlightDetailsView(List<Flight> flights) {
        try {
            for(Flight flight : flights) {
                FXMLLoader fxmlloader;
                fxmlloader = new FXMLLoader(flightDetailsBrief.getURL());
                fxmlloader.setControllerFactory(applicationContext::getBean);
                Parent parent = fxmlloader.load();
                if(flight != null) {
                    FlightDetailsController controller = fxmlloader.getController();
                    controller.setData(flight);
                }
                this.flightsList.getChildren().add(parent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda służąca do przejścia do widoku tabeli przewoźników
     * @param actionEvent event emitowany przez przycisk
     */
    public void showAirlinesView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            if(role.equals("[ROLE_ANONYMOUS]")){
                fxmlloader = new FXMLLoader(anonymousAirlinesView.getURL());
            }
            else if(role.equals("[ROLE_ADMIN]") || role.equals("[AIRLINE]")){
                fxmlloader = new FXMLLoader(airlinesView.getURL());
            }
            else{
                fxmlloader = new FXMLLoader(userAirlineView.getURL());
            }
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

    /**
     * Metoda służąca do przejścia do widoku tabeli klientów
     * @param actionEvent event emitowany przez przycisk
     */
    public void showCustomersView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            if(role.equals("[ROLE_ADMIN]") || role.equals("[AIRLINE]")){
                fxmlloader = new FXMLLoader(customersView.getURL());
            }
            else{
                fxmlloader = new FXMLLoader(userCustomersView.getURL());
            }
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

    /**
     * Metoda służąca do przejścia do widoku rezerwacji
     * @param actionEvent
     */
    public void showReservation(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(reservationListView.getURL());
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

    /**
     * Metoda służąca do przejścia do widoku lotów
     * @param actionEvent
     */
    public void showFlightView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            if(role.equals("[ROLE_ANONYMOUS]")){
                fxmlloader = new FXMLLoader(anonymousFlightView.getURL());
            }
            else if(role.equals("[ROLE_ADMIN]") || role.equals("[AIRLINE]")){
                fxmlloader = new FXMLLoader(flightView.getURL());
            }
            else{
                fxmlloader = new FXMLLoader(userFlightView.getURL());
            }
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

    /**
     * Metoda służąca do przejścia do widoku logowania
     * @param actionEvent
     */
    public void showLoginView(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlloader = new FXMLLoader(loginView.getURL());
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

    /**
     * Metoda służąca do przejścia do widoku rejestracji - rejestracja nie jest jeszcze zaimplementowana
     * @param actionEvent
     */
    public void showRegistrationView(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlloader = new FXMLLoader(registrationChoiceView.getURL());
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

    /**
     * Metoda zapewniająca możliwość wylogowania użytkownika
     * @param event event emitowany przez przycisk
     */
    @FXML
    void handleLogout(ActionEvent event) {
        JavafxApplication.logout();
        try {
            FXMLLoader fxmlloader = new FXMLLoader(anonymousMainView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}