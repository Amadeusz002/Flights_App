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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketCategory;
import pl.edu.agh.ki.lab.to.yourflights.service.FlightService;
import pl.edu.agh.ki.lab.to.yourflights.service.TicketCategoryService;
import pl.edu.agh.ki.lab.to.yourflights.utils.GenericFilter;


@RestController
@RequestMapping("/api/v1")
public class TicketCategoryViewController {

    /**
     * Widoki
     */
    private final Resource mainView;
    private final Resource customersView;
    private final Resource airlinesView;
    private final Resource reservationListView;
    private final Resource reservationListViewCustomer;
    private final Resource addTicketCategoryView;
    private final Resource anonymousMainView;
    private final Resource anonymousAirlineView;
    private final Resource loginView;
    private final Resource userAirlinesView;
    private final Resource userCustomersView;
    private final Resource flightView;


    private Flight flight;

    /**
     * Serwis kategorii biletów
     */
    private final TicketCategoryService ticketCategoryService;
    private final FlightService flightService;


    /**
     * Kontekst aplikacji Springa
     */
    private final ApplicationContext applicationContext;

    /**
     * Widok lotów
     */
    @FXML
    private JFXTreeTableView<TicketCategory> ticketCategoryTableView;

    /**
     * Kolumny tabeli
     */
    @FXML
    private TreeTableColumn<TicketCategory, String> name;
    @FXML
    private TreeTableColumn<TicketCategory, String> price;
    @FXML
    private TreeTableColumn<TicketCategory, String> numberOfSeats;


    /**
     * Przyciski
     */
    @FXML
    private JFXButton buttonAddTicketCategory;
    @FXML
    private JFXButton buttonDeleteTicketCategory;
    @FXML
    private JFXButton buttonUpdateTicketCategory;

    @FXML
    private JFXTextField departureField, destinationField, departureDateField, airlineField;


    /**
     * Metoda która wczytuje dane do tabeli lotów
     */
    public void setModel() {
        //Ustawienie kolumn
        name.setCellValueFactory(data -> data.getValue().getValue().getNameProperty());
        price.setCellValueFactory(data -> data.getValue().getValue().getPriceProperty());
        numberOfSeats.setCellValueFactory(data -> data.getValue().getValue().getNumberOfSeatsProperty());

        //Pobranie kategorii biletów z serwisu
        ObservableList<TicketCategory> ticketCategories = FXCollections.observableList(ticketCategoryService.findByFlight(flight));

        //Przekazanie danych do tabeli
        final TreeItem<TicketCategory> root = new RecursiveTreeItem<>(ticketCategories, RecursiveTreeObject::getChildren);
        ticketCategoryTableView.setRoot(root);
        ticketCategoryTableView.setShowRoot(false);
    }

    /**
     * Konstruktor, Spring wstrzykuje odpowiedznie zależności
     * @param ticketCategoryService
     * @param flightService
     * @param applicationContext
     * @param airlinesView
     * @param customersView
     * @param mainView
     * @param reservationListView
     * @param addTicketCategoryView
     * @param loginView
     * @param anonymousMainView
     * @param anonymousAirlineView
     * @param userAirlinesView
     * @param reservationListViewCustomer
     * @param userCustomersView
     */
    public TicketCategoryViewController(TicketCategoryService ticketCategoryService,
                                        FlightService flightService,
                                        ApplicationContext applicationContext,
                                        @Value("classpath:/view/AirlinesView.fxml") Resource airlinesView,
                                        @Value("classpath:/view/FlightView.fxml") Resource flightView,
                                        @Value("classpath:/view/CustomersView.fxml") Resource customersView,
                                        @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                                        @Value("classpath:/view/ReservationListView.fxml") Resource reservationListView,
                                        @Value("classpath:/view/AdminView/AddTicketCategoryView.fxml") Resource addTicketCategoryView,
                                        @Value("classpath:/view/AuthView/LoginView.fxml") Resource loginView,
                                        @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                                        @Value("classpath:/view/AnonymousView/AnonymousAirlinesView.fxml") Resource anonymousAirlineView,
                                        @Value("classpath:/view/UserView/UserAirlinesView.fxml") Resource userAirlinesView,
                                        @Value("classpath:/view/ReservationListViewCustomer.fxml") Resource reservationListViewCustomer,
                                        @Value("classpath:/view/UserView/UserCustomersView.fxml") Resource userCustomersView) {
        this.applicationContext = applicationContext;
        this.airlinesView = airlinesView;
        this.customersView = customersView;
        this.flightView = flightView;
        this.mainView = mainView;
        this.ticketCategoryService = ticketCategoryService;
        this.flightService = flightService;
        this.reservationListView = reservationListView;
        this.addTicketCategoryView = addTicketCategoryView;
        this.loginView = loginView;
        this.anonymousMainView = anonymousMainView;
        this.anonymousAirlineView = anonymousAirlineView;
        this.userAirlinesView = userAirlinesView;
        this.userCustomersView = userCustomersView;
        this.reservationListViewCustomer = reservationListViewCustomer;
    }



    /**
     * Metoda wywoływana po inicjalizacji widoku
     */
    @FXML
    public void initialize() {
        this.setModel();
        ticketCategoryTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    public void showFlightView(ActionEvent actionEvent) {
        try {
            //ładujemy widok z pliku .fxml
            FXMLLoader fxmlloader = new FXMLLoader(flightView.getURL());

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
     * Metoda służąca do przejścia do widoku formularza do dodawania/edycji lotów
     * @param actionEvent event emitowany przez przycisk
     */
    public void showAddTicketCategory(ActionEvent actionEvent, Flight flight, TicketCategory ticketCategory) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(addTicketCategoryView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            AddTicketCategoryController controller = fxmlloader.getController();
            controller.setData(flight, ticketCategory);

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

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        var ticketCategories = ticketCategoryTableView.getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList());
        ticketCategoryService.deleteAll(FXCollections.observableList(ticketCategories));
        this.setModel();
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        var ticketCategory = ticketCategoryTableView.getSelectionModel().getSelectedItem();
        this.showAddTicketCategory(event, flight, ticketCategory.getValue());
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        this.showAddTicketCategory(event, flight, null);
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
     * Metoda która ustawia informacje o locie
     */
    public void updateControls() {
        departureField.setText(flight.getPlaceOfDeparture());
        destinationField.setText(flight.getPlaceOfDestination());
        departureDateField.setText(flight.getDepartureDate());
        airlineField.setText(flight.getAirline().getName());
    }

    /**
     * Metoda która przekazuje do kontrolera dane
     * @param flight lot dla którego dodajemy kategorie biletów
     */
    public void setData(Flight flight) {
        this.flight = flight;
        this.updateControls();
        this.setModel();
    }

    /**
     * Metoda ustawiająca powiązanie atrybutu 'disabled' przycisków z zaznaczeniem w tabeli
     * Po to aby przyciski Delete, Update i AddReservation były nieaktywne w sytuacji gdy nic nie jest zaznaczone w tabeli
     */
    private void setButtonsDisablePropertyBinding() {
        if(buttonDeleteTicketCategory != null) {
            buttonDeleteTicketCategory.disableProperty().bind(
                    Bindings.isEmpty(ticketCategoryTableView.getSelectionModel().getSelectedItems())
            );
        }
        if(buttonUpdateTicketCategory != null) {
            buttonUpdateTicketCategory.disableProperty().bind(
                    Bindings.size(ticketCategoryTableView.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }
    }
}