package pl.edu.agh.ki.lab.to.yourflights.controller;

import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.service.AirlineService;
import pl.edu.agh.ki.lab.to.yourflights.service.FlightService;
import pl.edu.agh.ki.lab.to.yourflights.service.UserPrincipalService;
import pl.edu.agh.ki.lab.to.yourflights.utils.GenericFilter;


@RestController
@RequestMapping("/api/v1")
public class FlightController {

    /**
     * Widoki
     */
    private final Resource mainView;
    private final Resource customersView;
    private final Resource airlinesView;
    private final Resource reservationListView;
    private final Resource reservationListViewCustomer;
    private final Resource addFlightView;
    private final Resource addReservationView;
    private final Resource anonymousMainView;
    private final Resource anonymousAirlineView;
    private final Resource loginView;
    private final Resource userAirlinesView;
    private final Resource userCustomersView;
    private final Resource flightDetailsView;
    private final Resource ticketCategoryView;
    private final Resource discountsView;


    /**
     * Serwisy
     */
    private final FlightService flightService;
    private final AirlineService airlineService;
    private final UserPrincipalService userPrincipalService;


    /**
     * Kontekst aplikacji Springa
     */
    private final ApplicationContext applicationContext;

    /**
     * Widok lotów
     */
    @FXML
    private JFXTreeTableView<Flight> flightsTableView;

    /**
     * Kolumny tabeli
     */
    @FXML
    private TreeTableColumn<Flight, String> departure;
    @FXML
    private TreeTableColumn<Flight, String> destination;
    @FXML
    private TreeTableColumn<Flight, String> departureDate;
    @FXML
    private TreeTableColumn<Flight, String> arrivalDate;
    @FXML
    private TreeTableColumn<Flight, String> airlineName;
    @FXML
    private TreeTableColumn<Flight, String> departureTime;
    @FXML
    private TreeTableColumn<Flight, String> arrivalTime;

    //Pola filtrów
    @FXML
    private JFXTextField departureInput;
    @FXML
    private JFXTextField destinationInput;
    @FXML
    private JFXDatePicker datePicker;

    //Lista zawierająca predykaty służące do filtrowania danych
    private final List<Predicate<Flight>> predicates = new LinkedList<>();

    /**
     * Przyciski
     */
    @FXML
    private JFXButton buttonAddReservation;
    @FXML
    private JFXButton buttonDeleteFlight;
    @FXML
    private JFXButton buttonUpdateFlight;
    @FXML
    private JFXButton buttonShowFlightDetails;
    @FXML
    private JFXButton buttonShowTicketCategories;

    /**
     * Metoda która wczytuje dane do tabeli lotów
     */
    public void setModel() {
        //Ustawienie kolumn
        departure.setCellValueFactory(data -> data.getValue().getValue().getplaceOfDepartureProperty());
        destination.setCellValueFactory(data -> data.getValue().getValue().getplaceOfDestinationProperty());
        departureDate.setCellValueFactory(data -> data.getValue().getValue().getdepartureDateProperty());
        arrivalDate.setCellValueFactory(data -> data.getValue().getValue().getarrivalDateProperty());
        airlineName.setCellValueFactory(data-> data.getValue().getValue().getAirlineNameProperty());
        departureTime.setCellValueFactory(data-> data.getValue().getValue().getDepartureTimeProperty());
        arrivalTime.setCellValueFactory(data-> data.getValue().getValue().getArrivalTimeProperty());

        //Pobranie lotów z serwisu
        //Jeśli zalogowanym użytkownikiem jest linia lotnicza, to pobierze tylko loty danej linii
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        ObservableList<Flight> flights;

        if(role.equals("[AIRLINE]")){
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = "";
            if(userDetails instanceof UserDetails){
                username = ((UserDetails)userDetails).getUsername();
            }
           flights = FXCollections.observableList(flightService.findByAirline(airlineService.findByUser(userPrincipalService.findByUsername(username).get(0))));
        }
        else{
            flights = FXCollections.observableList(flightService.findAll());
        }


        //Przekazanie danych do tabeli
        final TreeItem<Flight> root = new RecursiveTreeItem<>(flights, RecursiveTreeObject::getChildren);
        flightsTableView.setRoot(root);
        flightsTableView.setShowRoot(false);
    }

    /**
     * Konstruktor, Spring wstrzykuje odpowiedznie zależności
     * @param flightService
     * @param applicationContext
     * @param airlinesView
     * @param customersView
     * @param mainView
     * @param reservationListView
     * @param addReservationView
     * @param addFlightView
     * @param loginView
     * @param ticketCategoryView
     * @param anonymousMainView
     * @param anonymousAirlineView
     * @param userAirlinesView
     * @param reservationListViewCustomer
     * @param userCustomersView
     * @param discountsView
     */
    public FlightController(FlightService flightService, AirlineService airlineService,
                            UserPrincipalService userPrincipalService, ApplicationContext applicationContext,
                            @Value("classpath:/view/AirlinesView.fxml") Resource airlinesView,
                            @Value("classpath:/view/CustomersView.fxml") Resource customersView,
                            @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                            @Value("classpath:/view/ReservationListView.fxml") Resource reservationListView,
                            @Value("classpath:/view/AddReservationView.fxml") Resource addReservationView,
                            @Value("classpath:/view/TicketCategoryView.fxml") Resource ticketCategoryView,
                            @Value("classpath:/view/AddFlightView.fxml") Resource addFlightView,
                            @Value("classpath:/view/AuthView/LoginView.fxml") Resource loginView,
                            @Value("classpath:/view/DiscountsView.fxml") Resource discountsView,
                            @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                            @Value("classpath:/view/AnonymousView/AnonymousAirlinesView.fxml") Resource anonymousAirlineView,
                            @Value("classpath:/view/UserView/UserAirlinesView.fxml") Resource userAirlinesView,
                            @Value("classpath:/view/ReservationListViewCustomer.fxml") Resource reservationListViewCustomer,
                            @Value("classpath:/view/UserView/UserCustomersView.fxml") Resource userCustomersView,
                            @Value("classpath:/view/AdminView/FlightDetailsView.fxml") Resource flightDetailsView) {
        this.applicationContext = applicationContext;
        this.airlinesView = airlinesView;
        this.customersView = customersView;
        this.mainView = mainView;
        this.flightService = flightService;
        this.airlineService = airlineService;
        this.reservationListView = reservationListView;
        this.addReservationView = addReservationView;
        this.addFlightView = addFlightView;
        this.loginView = loginView;
        this.anonymousMainView = anonymousMainView;
        this.anonymousAirlineView = anonymousAirlineView;
        this.userAirlinesView = userAirlinesView;
        this.userCustomersView = userCustomersView;
        this.reservationListViewCustomer = reservationListViewCustomer;
        this.flightDetailsView = flightDetailsView;
        this.ticketCategoryView = ticketCategoryView;
        this.discountsView = discountsView;
        this.userPrincipalService = userPrincipalService;
    }

    /**
     * Metoda która inicjalizuje obsługę filtrowanie
     */
    private void setPredicates() {

        // Generyczna klasa filtrów dla danego modelu
        GenericFilter<Flight> airlineFilter = new GenericFilter<>(flightsTableView);
        // Dodanie do listy predykatów testujących zawartość filtrów
        //filtrowanie na podstawie lotniska źródłowego
        airlineFilter.addPredicate(testedValue -> testedValue.getPlaceOfDeparture().toLowerCase().contains(departureInput.getText().toLowerCase()));
        //filtrowanie na podstawie lotniska docelowego
        airlineFilter.addPredicate(testedValue -> testedValue.getPlaceOfDestination().toLowerCase().contains(destinationInput.getText().toLowerCase()));
        //filtrowanie na podstawie daty wylotu
        airlineFilter.addPredicate(testedValue -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
                    return datePicker.getValue() == null || datePicker.getValue().isEqual(LocalDate.parse(testedValue.getDepartureDate(), formatter));
        });
        // dodanie do filtrów obserwatorów zmiany wartości (sprawdzanie predykatów po zmianie wartości filtra)
        airlineFilter.setListener(departureInput.textProperty());
        airlineFilter.setListener(destinationInput.textProperty());
        airlineFilter.setListener(datePicker.valueProperty());
    }

    /**
     * Metoda wywoływana po inicjalizacji widoku
     */
    @FXML
    public void initialize() {
        this.setModel();
        setPredicates();
        flightsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setButtonsDisablePropertyBinding();
    }

    /**
     * Metoda służąca do przejścia do widoku głównego
     * @param actionEvent event emitowany przez przycisk
     */
    public void showMainView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().equals("[ROLE_ANONYMOUS]")){
                fxmlloader = new FXMLLoader(anonymousMainView.getURL());
            }
            else{
                fxmlloader = new FXMLLoader(mainView.getURL());
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
     * Metoda służąca do przejścia do widoku przewoźników
     * @param actionEvent event emitowany przez przycisk
     */
    public void showAirlinesView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            if(role.equals("[ROLE_ANONYMOUS]")){
                fxmlloader = new FXMLLoader(anonymousAirlineView.getURL());
            }
            else if(role.equals("[ROLE_ADMIN]") || role.equals("[AIRLINE]")){
                fxmlloader = new FXMLLoader(airlinesView.getURL());
            }
            else{
                fxmlloader = new FXMLLoader(userAirlinesView.getURL());
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
     * Metoda służąca do przejścia do widoku tabeli rezerwacji
     * @param actionEvent event emitowany przez przycisk
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
     * Metoda służąca do przejścia do formularza dodawania/edycji rezerwacji
     * @param actionEvent event emitowany przez przycisk
     */
    public void showAddReservation(ActionEvent actionEvent, Flight flight) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(addReservationView.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            AddReservationController controller = fxmlLoader.getController();
            controller.setData(flight);
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda służąca do przejścia do widoku formularza do dodawania/edycji lotów
     * @param actionEvent event emitowany przez przycisk
     */
    public void showAddFlight(ActionEvent actionEvent, Flight flight) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(addFlightView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            if(flight != null) {
                AddFlightController controller = fxmlloader.getController();
                controller.setData(flight);
            }

            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda służąca do przejścia do widoku logowania
     * @param actionEvent event emitowany przez przycisk
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

    public void handleShowFlightDetailsView(ActionEvent event) {
        var flight = flightsTableView.getSelectionModel().getSelectedItem();
        if(flight != null) {
            this.showFlightDetailsView(event, flight.getValue());
        }
    }

    /**
     * Metoda służąca do przejścia do widoku szczegółów lotu
     * @param actionEvent event emitowany przez przycisk
     */
    public void showFlightDetailsView(ActionEvent actionEvent, Flight flight) {
        try {
            FXMLLoader fxmlloader;
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            fxmlloader = new FXMLLoader(flightDetailsView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            if(flight != null) {
                FlightDetailsController controller = fxmlloader.getController();
                controller.setData(flight);
            }

            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda służąca do przejścia do widoku kategorii biletów dla danego lotu
     * @param actionEvent event emitowany przez przycisk
     * @param flight obiekt lotu
     */
    public void showTicketCategory(ActionEvent actionEvent, Flight flight) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(ticketCategoryView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            TicketCategoryViewController controller = fxmlloader.getController();
            controller.setData(flight);

            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDiscountsView(ActionEvent actionEvent) {
        try {
            //ładujemy widok z pliku .fxml
            FXMLLoader fxmlloader = new FXMLLoader(discountsView.getURL());

            //Spring wstrzykuje odpowiedni kontroler obsługujący dany plik .fxml na podstawie kontekstu aplikacji
            fxmlloader.setControllerFactory(applicationContext::getBean);

            //wczytanie sceny
            Parent parent = fxmlloader.load();

            //pobieramy stage z którego wywołany został actionEvent - bo nie chcemy tworzyć za każdym razem nowego Stage
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            //utworzenie i wyświetlenie sceny
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        var flights = flightsTableView.getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList());
        flightService.deleteAll(FXCollections.observableList(flights));
        this.setModel();
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        var flight = flightsTableView.getSelectionModel().getSelectedItem();
        if(flight != null) {
            this.showAddFlight(event, flight.getValue());
        }
    }

    @FXML
    private void handleShowTicketCategories(ActionEvent event) {
        var flight = flightsTableView.getSelectionModel().getSelectedItem();
        if(flight != null) {
            this.showTicketCategory(event, flight.getValue());
        }
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        this.showAddFlight(event, null);
    }

    @FXML
    private void handleAddReservationAction(ActionEvent event) {
        var flight = flightsTableView.getSelectionModel().getSelectedItem();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals("anonymous") && flight != null) {
            this.showAddReservation(event, flight.getValue());
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

    /**
     * Metoda resetująca filtry
     */
    public void resetFilters() {
        departureInput.clear();
        destinationInput.clear();
        datePicker.setValue(null);
    }

    /**
     * Metoda ustawiająca powiązanie atrybutu 'disabled' przycisków z zaznaczeniem w tabeli
     * Po to aby przyciski Delete, Update i AddReservation były nieaktywne w sytuacji gdy nic nie jest zaznaczone w tabeli
     */
    private void setButtonsDisablePropertyBinding() {
        if(buttonAddReservation != null) {
            buttonAddReservation.disableProperty().bind(
                    Bindings.size(flightsTableView.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }
        if(buttonDeleteFlight != null) {
            buttonDeleteFlight.disableProperty().bind(
                    Bindings.isEmpty(flightsTableView.getSelectionModel().getSelectedItems())
            );
        }
        if(buttonUpdateFlight != null) {
            buttonUpdateFlight.disableProperty().bind(
                    Bindings.size(flightsTableView.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }
        if(buttonShowFlightDetails != null) {
            buttonShowFlightDetails.disableProperty().bind(
                    Bindings.size(flightsTableView.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }
        if(buttonShowTicketCategories != null) {
            buttonShowTicketCategories.disableProperty().bind(
                    Bindings.size(flightsTableView.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }
    }
}