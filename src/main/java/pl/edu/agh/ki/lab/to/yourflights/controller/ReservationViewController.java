package pl.edu.agh.ki.lab.to.yourflights.controller;

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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;
import pl.edu.agh.ki.lab.to.yourflights.model.*;
import pl.edu.agh.ki.lab.to.yourflights.service.AirlineService;
import pl.edu.agh.ki.lab.to.yourflights.service.ReservationService;
import pl.edu.agh.ki.lab.to.yourflights.service.TicketOrderService;
import pl.edu.agh.ki.lab.to.yourflights.service.UserPrincipalService;
import pl.edu.agh.ki.lab.to.yourflights.utils.GenericFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Kontroler widoku tabeli rezerwacji
 * Oznaczenie @Component pozwala Springowi na wstrzykiwanie kontrolera tam gdzie jest potrzebny
 */
@Component
public class ReservationViewController {

    private final ReservationService reservationService;
    private final TicketOrderService ticketOrderService;
    private final AirlineService airlineService;
    private final UserPrincipalService userPrincipalService;

    private final Resource mainView;
    private final Resource customersView;
    private final Resource airlineView;
    private final Resource addReservationView;
    private final ApplicationContext applicationContext;
    private final Resource flightView;
    private final Resource userFlightView;
    private final Resource userCustomersView;
    private final Resource airlinesView;
    private final Resource userAirlinesView;
    private final Resource anonymousMainView;

    /**
     * Tabela rezerwacji
     */
    @FXML
    private JFXTreeTableView<Reservation> reservationListTable;

    /**
     * Kolumny tabeli
     */
    @FXML
    private TreeTableColumn<Reservation, String> reservationDate;
    @FXML
    private TreeTableColumn<Reservation, String> userName;
    @FXML
    private TreeTableColumn<Reservation, String> departure;
    @FXML
    private TreeTableColumn<Reservation, String> departureDate;
    @FXML
    private TreeTableColumn<Reservation, String> destination;
    @FXML
    private TreeTableColumn<Reservation, String> destinationDate;

    /**
     * Pola do filtrowania
     */
    @FXML
    private JFXTextField userNameFilter;
    @FXML
    private JFXTextField departureFilter;
    @FXML
    private JFXTextField destinationFilter;
    @FXML
    private JFXDatePicker datePicker;

    /**
     * Przyciski
     */
    @FXML
    private JFXButton buttonDeleteReservation;
    @FXML
    private JFXButton buttonUpdateReservation;

    /**
     * Metoda która wczytuje dane do tabeli rezerwacji
     */
    public void setModel() {
        //Ustawienie kolumn
        reservationDate.setCellValueFactory(data -> data.getValue().getValue().getReservationDateProperty());
        userName.setCellValueFactory(data -> data.getValue().getValue().getUserNameProperty());
        departure.setCellValueFactory(data -> ticketOrderService.findByReservation(data.getValue().getValue()).get(0).getTicketCategory().getFlight().getplaceOfDepartureProperty());
        destination.setCellValueFactory(data -> ticketOrderService.findByReservation(data.getValue().getValue()).get(0).getTicketCategory().getFlight().getplaceOfDestinationProperty());
        departureDate.setCellValueFactory(data -> ticketOrderService.findByReservation(data.getValue().getValue()).get(0).getTicketCategory().getFlight().getdepartureDateProperty());
        destinationDate.setCellValueFactory(data -> ticketOrderService.findByReservation(data.getValue().getValue()).get(0).getTicketCategory().getFlight().getarrivalDateProperty());

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        ObservableList<Reservation> reservationList;

        //Pobranie rezerwacje z serwisu
        if(role.equals("[AIRLINE]")){
            reservationList = FXCollections.observableList(airlineService.getReservationsForAirline(airlineService.findByUser(userPrincipalService.findByUsername(name).get(0))));
        }
        else{
             reservationList = FXCollections.observableList(reservationService.findAll().stream().filter(reservation -> ticketOrderService.findByReservation(reservation).size() > 0)
                    .filter(reservation -> reservation.getUserName().equals(name) || role.equals("[ROLE_ADMIN]"))
                    .collect(Collectors.toList()));
        }


        //Przekazanie danych do tabeli
        final TreeItem<Reservation> root = new RecursiveTreeItem<Reservation>(reservationList, RecursiveTreeObject::getChildren);
        reservationListTable.setRoot(root);
        reservationListTable.setShowRoot(false);
    }

    /**
     * Metoda służąca do inicjalizacji filtrowania
     */
    private void setPredicates() {
        // Generyczna klasa filtrów dla danego modelu
        GenericFilter<Reservation> reservationFilter = new GenericFilter<>(reservationListTable);
        // Dodanie do listy predykatów testujących zawartość filtrów
        //filtrowanie na podstawie nazwy użytkownika
        reservationFilter.addPredicate(testedValue -> testedValue.getUserName().toLowerCase().contains(userNameFilter.getText().toLowerCase()));
        //filtrowanie na podstawie lotniska docelowego
        reservationFilter.addPredicate(testedValue -> ticketOrderService.findByReservation(testedValue).get(0).getTicketCategory().getFlight().getPlaceOfDeparture().toLowerCase().contains(departureFilter.getText().toLowerCase()));
        //filtrowanie na podstawie lotniska źródłowego
        reservationFilter.addPredicate(testedValue -> ticketOrderService.findByReservation(testedValue).get(0).getTicketCategory().getFlight().getPlaceOfDestination().toLowerCase().contains(destinationFilter.getText().toLowerCase()));
        //filtrowanie na podstawie daty rezerwacji
        reservationFilter.addPredicate(testedValue -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
            return datePicker.getValue() == null || datePicker.getValue().isEqual(LocalDate.parse(testedValue.getReservationDate(), formatter));
        });
        // dodanie do filtrów obserwatorów zmiany wartości (sprawdzanie predykatów po zmianie wartości filtra)
        reservationFilter.setListener(userNameFilter.textProperty());
        reservationFilter.setListener(departureFilter.textProperty());
        reservationFilter.setListener(destinationFilter.textProperty());
        reservationFilter.setListener(datePicker.valueProperty());
    }

    /**
     * Konstruktor, Spring wstrzykuje odpowiednie zależności
     * @param reservationService serwis do pobierania danych o rezerwacji
     * @param ticketOrderService serwis do pobierania danych o zamówieniach biletów
     * @param airlineService
     * @param userPrincipalService
     * @param mainView główny widok aplikacji
     * @param AirlineView widok formularza do przewoźników
     * @param applicationContext kontekst aplikacji Springa
     */
    public ReservationViewController(ReservationService reservationService, TicketOrderService ticketOrderService,
                                     AirlineService airlineService, UserPrincipalService userPrincipalService, @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                                     @Value("classpath:/view/CustomersView.fxml") Resource customersView,
                                     @Value("classpath:/view/AirlinesView.fxml") Resource AirlineView,
                                     @Value("classpath:/view/AddReservationView.fxml") Resource addReservationView,
                                     @Value("classpath:/view/FlightView.fxml") Resource flightView,
                                     @Value("classpath:/view/UserView/UserFlightView.fxml") Resource userFlightView,
                                     @Value("classpath:/view/UserView/UserCustomersView.fxml") Resource userCustomersView,
                                     @Value("classpath:/view/UserView/UserAirlinesView.fxml") Resource userAirlinesView,
                                     @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                                     @Value("classpath:/view/AirlinesView.fxml") Resource airlinesView,
                                     ApplicationContext applicationContext) {
        this.reservationService = reservationService;
        this.ticketOrderService = ticketOrderService;
        this.airlineService = airlineService;
        this.userPrincipalService = userPrincipalService;
        this.mainView = mainView;
        this.airlineView = AirlineView;
        this.applicationContext = applicationContext;
        this.customersView = customersView;
        this.addReservationView = addReservationView;
        this.flightView = flightView;
        this.userFlightView = userFlightView;
        this.userCustomersView = userCustomersView;
        this.userAirlinesView = userAirlinesView;
        this.anonymousMainView = anonymousMainView;
        this.airlinesView = airlinesView;
    }

    /**
     * Metoda wywoływana po inicjalizacji widoku
     */
    @FXML
    public void initialize() {
        this.setModel();
        this.setPredicates();
        reservationListTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setButtonsDisablePropertyBinding();
    }

    /**
     * Metoda służąca do przejścia do głównego widoku
     * @param actionEvent event emitowany przez przycisk
     */
    public void showMainView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(mainView.getURL());
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
     * @param actionEvent
     */
    public void showAirlinesView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(airlineView.getURL());
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            if(role.equals("[ROLE_ADMIN]") || role.equals("[AIRLINE]")){
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
     * Metoda służąca do przejścia do widoku lotów
     * @param actionEvent
     */
    public void showFlightView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            if(role.equals("[ROLE_ADMIN]") || role.equals("[AIRLINE]")){
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
     * Metoda służąca do przejścia do widoku do dodawania/edycji rezerwacji
     * @param actionEvent
     * @param flight
     * @param reservation - rezerwacja do edycji (może być nullem)
     */
    public void showAddReservation(ActionEvent actionEvent, Flight flight, Reservation reservation) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(addReservationView.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            AddReservationController controller = fxmlLoader.getController();
            controller.setData(flight, reservation);
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        var reservations = reservationListTable.getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList());
        reservations.forEach(reservation -> ticketOrderService.deleteAll(FXCollections.observableList(ticketOrderService.findByReservation(reservation))));
        reservationService.deleteAll(FXCollections.observableList(reservations));
        this.setModel();
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        var reservation = reservationListTable.getSelectionModel().getSelectedItem();
        if(reservation != null) {
            this.showAddReservation(event, ticketOrderService.findByReservation(reservation.getValue()).get(0).getTicketCategory().getFlight(), reservation.getValue());
        }
    }

    /**
     * Metoda obsługująca wylogowywanie użytkownika
     * @param event
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
        destinationFilter.clear();
        departureFilter.clear();
        userNameFilter.clear();
        datePicker.setValue(null);
    }

    /**
     * Metoda ustawiająca powiązanie atrybutu 'disabled' przycisków z zaznaczeniem w tabeli
     * Po to aby przyciski Delete, Update i AddReservation były nieaktywne w sytuacji gdy nic nie jest zaznaczone w tabeli
     */
    private void setButtonsDisablePropertyBinding() {
        if(buttonDeleteReservation != null) {
            buttonDeleteReservation.disableProperty().bind(
                    Bindings.isEmpty(reservationListTable.getSelectionModel().getSelectedItems())
            );
        }
        if(buttonUpdateReservation != null) {
            buttonUpdateReservation.disableProperty().bind(
                    Bindings.size(reservationListTable.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }

    }
}