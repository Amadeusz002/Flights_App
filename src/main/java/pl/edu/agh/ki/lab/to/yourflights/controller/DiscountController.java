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
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;
import pl.edu.agh.ki.lab.to.yourflights.model.Airline;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketCategory;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketDiscount;
import pl.edu.agh.ki.lab.to.yourflights.service.AirlineService;
import pl.edu.agh.ki.lab.to.yourflights.service.TicketDiscountService;
import pl.edu.agh.ki.lab.to.yourflights.utils.GenericFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kontroler widoku tabeli przewoźników
 * Oznaczenie @Component pozwala Springowi na wstrzykiwanie kontrolera tam gdzie jest potrzebny
 */
@Component
public class DiscountController {

    /**
     * Serwis linii lotniczych
     */
    private TicketDiscountService ticketDiscountService;

    /**
     * Widoki
     */
    private final Resource mainView;
    private final Resource addDiscountView;
    private final Resource reservationListView;
    private final Resource reservationListViewCustomer;
    private final Resource anonymousMainView;
    private final Resource flightView;
    private final Resource anonymousFlightView;
    private final Resource userFlightView;
    private final Resource loginView;
    private final Resource customersView;
    private final Resource userCustomersView;
    private final Resource userAirlinesView;
    private final Resource airlinesView;
    private final Resource anonymousAirlineView;

    /**
     * Kontekrs aplikacji Springa
     */
    private final ApplicationContext applicationContext;

    /**
     * Tabela przewoźników
     */
    @FXML
    private JFXTreeTableView<TicketDiscount> discountsTableView;

    /**
     * Kolumny tabeli
     */
    @FXML
    private TreeTableColumn<TicketDiscount, String> nameColumn;
    @FXML
    private TreeTableColumn<TicketDiscount, String> percentageColumn;

    /**
     * Przyciski
     */
    @FXML
    private JFXButton buttonDeleteDiscount;
    @FXML
    private JFXButton buttonUpdateDiscount;

    /**
     * Metoda która wczytuje dane do tabeli przwoźników
     */
    public void setModel() {
        //Ustawienie kolumn
        nameColumn.setCellValueFactory(data -> data.getValue().getValue().getNameProperty());
        percentageColumn.setCellValueFactory(data -> data.getValue().getValue().getDiscountProperty());

        //Pobranie przewoźników z serwisu
        ObservableList<TicketDiscount> ticketDiscounts = FXCollections.observableList(ticketDiscountService.findAll());

        //Przekazanie danych do tabeli
        final TreeItem<TicketDiscount> root = new RecursiveTreeItem<>(ticketDiscounts, RecursiveTreeObject::getChildren);
        discountsTableView.setRoot(root);
        discountsTableView.setShowRoot(false);
    }

    /**
     * Konstruktor, Spring wstrzykuje odpowiednie zależności
     * @param ticketDiscountService serwis do pobierania danych o przewoźnikach
     * @param mainView główny widok aplikacji
     * @param addDiscountView widok formularza do dodawania przewoźników
     * @param applicationContext kontekst aplikacji Springa
     */
    public DiscountController(TicketDiscountService ticketDiscountService,
                                  @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                                  @Value("classpath:/view/CustomersView.fxml") Resource customersView,
                                  @Value("classpath:/view/AdminView/AddDiscountView.fxml") Resource addDiscountView,
                                  @Value("classpath:/view/ReservationListView.fxml") Resource reservationList,
                                  @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                                  @Value("classpath:/view/FlightView.fxml") Resource flightView,
                                  @Value("classpath:/view/AnonymousView/AnonymousFlightView.fxml") Resource anonymousFlightView,
                                  @Value("classpath:/view/UserView/UserFlightView.fxml") Resource userFlightView,
                                  @Value("classpath:/view/UserView/UserCustomersView.fxml") Resource userCustomersView,
                                  @Value("classpath:/view/AuthView/LoginView.fxml") Resource loginView,
                                  @Value("classpath:/view/ReservationListViewCustomer.fxml") Resource reservationListViewCustomer,
                                  @Value("classpath:/view/AirlinesView.fxml") Resource airlinesView,
                                  @Value("classpath:/view/UserView/UserAirlinesView.fxml") Resource userAirlinesView,
                                  @Value("classpath:/view/AnonymousView/AnonymousAirlinesView.fxml") Resource anonymousAirlineView,
                                  ApplicationContext applicationContext) {
        this.ticketDiscountService = ticketDiscountService;
        this.mainView = mainView;
        this.addDiscountView = addDiscountView;
        this.applicationContext = applicationContext;
        this.customersView = customersView;
        this.anonymousMainView = anonymousMainView;
        this.reservationListView = reservationList;
        this.flightView = flightView;
        this.anonymousFlightView = anonymousFlightView;
        this.loginView = loginView;
        this.userCustomersView = userCustomersView;
        this.userFlightView = userFlightView;
        this.reservationListViewCustomer = reservationListViewCustomer;
        this.airlinesView = airlinesView;
        this.anonymousAirlineView = anonymousAirlineView;
        this.userAirlinesView = userAirlinesView;
    }



    /**
     * Metoda wywoływana po inicjalizacji widoku
     */
    @FXML
    public void initialize() {
        this.setModel();
        discountsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setButtonsDisablePropertyBinding();
    }

    /**
     * Metoda służąca do przejścia do głównego widoku
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
     * Metoda służąca do przejścia do widoku formularza dodawania/edycji przewoźników
     * @param actionEvent event emitowany przez przycisk
     */
    public void showAddDiscount(ActionEvent actionEvent, TicketDiscount discount) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(addDiscountView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            if(discount != null) {
                AddDiscountController controller = fxmlloader.getController();
                controller.setData(discount);
            }

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
     * Metoda służąca do przejścia do widoku przewoźników
     * @param actionEvent event emitowany przez przycisk
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
        var discounts = discountsTableView.getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList());
        ticketDiscountService.deleteAll(FXCollections.observableList(discounts));
        this.setModel();
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        var discount = discountsTableView.getSelectionModel().getSelectedItem();
        if(discount != null) {
            this.showAddDiscount(event, discount.getValue());
        }
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        this.showAddDiscount(event, null);
    }

    /**
     * Metoda obsługująca wylogowanie użytkownika
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
     * Metoda ustawiająca powiązanie atrybutu 'disabled' przycisków z zaznaczeniem w tabeli
     * Po to aby przyciski Delete i Update były nieaktywne w sytuacji gdy nic nie jest zaznaczone w tabeli
     */
    private void setButtonsDisablePropertyBinding() {
        if(buttonDeleteDiscount != null) {
            buttonDeleteDiscount.disableProperty().bind(
                    Bindings.isEmpty(discountsTableView.getSelectionModel().getSelectedItems())
            );
        }
        if(buttonUpdateDiscount != null) {
            buttonUpdateDiscount.disableProperty().bind(
                    Bindings.size(discountsTableView.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }
    }
}
