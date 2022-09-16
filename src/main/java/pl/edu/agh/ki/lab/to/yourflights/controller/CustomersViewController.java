package pl.edu.agh.ki.lab.to.yourflights.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;
import pl.edu.agh.ki.lab.to.yourflights.model.Customer;
import pl.edu.agh.ki.lab.to.yourflights.service.CustomerService;
import pl.edu.agh.ki.lab.to.yourflights.service.UserPrincipalService;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Kontroler widoku tabeli klientów
 */
@Component
public class CustomersViewController {

    private final Resource mainView;
    private final Resource airlinesView;
    private final Resource addCustomerView;
    private final Resource reservationListView;
    private final Resource reservationListViewCustomer;
    private final Resource flightView;
    private final Resource userFlightView;
    private final Resource userAirlinesView;
    private final Resource anonymousMainView;

    private CustomerService customerService;

    private UserPrincipalService userPrincipalService;

    private final ApplicationContext applicationContext;

    @FXML
    private JFXTreeTableView<Customer> customersTableView;

    @FXML
    private TreeTableColumn<Customer, String> firstNameColumn;
    @FXML
    private TreeTableColumn<Customer, String> secondNameColumn;
    @FXML
    private TreeTableColumn<Customer, String> countryColumn;
    @FXML
    private TreeTableColumn<Customer, String> cityColumn;

    @FXML
    private JFXButton buttonDeleteCustomer;
    @FXML
    private JFXButton buttonUpdateCustomer;



    public void setModel() {
        //Ustawienie kolumn
        firstNameColumn.setCellValueFactory(data -> data.getValue().getValue().getFirstNameProperty());
        secondNameColumn.setCellValueFactory(data -> data.getValue().getValue().getSecondNameProperty());
        countryColumn.setCellValueFactory(data -> data.getValue().getValue().getCountryProperty());
        cityColumn.setCellValueFactory(data -> data.getValue().getValue().getCityProperty());

        //Pobranie klientów z serwisu
        ObservableList<Customer> customers;// = FXCollections.observableList(customerService.findAll());

        FXMLLoader fxmlloader;
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.equals("[ROLE_ADMIN]")){
            customers = FXCollections.observableList(customerService.findAll());
        }
        else{
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = "";
            if(userDetails instanceof UserDetails){
                username = ((UserDetails)userDetails).getUsername();
            }
            customers = FXCollections.observableList(customerService.findByUsername(username));
        }

        //Przekazanie danych do tabeli
        final TreeItem<Customer> root = new RecursiveTreeItem<>(customers, RecursiveTreeObject::getChildren);
        customersTableView.setRoot(root);
        customersTableView.setShowRoot(false);
    }


    public CustomersViewController(CustomerService customerService,
                                   UserPrincipalService userPrincipalService,
                                   @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                                   @Value("classpath:/view/AirlinesView.fxml") Resource airlinesView,
                                   @Value("classpath:/view/AddCustomerView.fxml") Resource addCustomerView,
                                   @Value("classpath:/view/ReservationListView.fxml") Resource reservationList,
                                   @Value("classpath:/view/FlightView.fxml") Resource flightView,
                                   @Value("classpath:/view/UserView/UserFlightView.fxml") Resource userFlightView,
                                   @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                                   @Value("classpath:/view/UserView/UserAirlinesView.fxml") Resource userAirlinesView,
                                   @Value("classpath:/view/ReservationListViewCustomer.fxml") Resource reservationListViewCustomer,
                                   ApplicationContext applicationContext) {
        this.customerService = customerService;
        this.mainView = mainView;
        this.airlinesView = airlinesView;
        this.addCustomerView = addCustomerView;
        this.applicationContext = applicationContext;
        this.reservationListView = reservationList;
        this.flightView = flightView;
        this.userFlightView = userFlightView;
        this.userAirlinesView = userAirlinesView;
        this.anonymousMainView = anonymousMainView;
        this.reservationListViewCustomer = reservationListViewCustomer;
        this.userPrincipalService = userPrincipalService;
    }


    @FXML
    public void initialize() {
        this.setModel();
        customersTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setButtonsDisablePropertyBinding();
    }


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


    public void showAirlinesView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
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


    public void showAddCustomer(ActionEvent actionEvent, Customer customer) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(addCustomerView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            if(customer != null) {
                AddCustomerController controller = fxmlloader.getController();
                controller.setData(customer);
            }

            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        var customers = customersTableView.getSelectionModel().getSelectedItems().stream().map(item -> item.getValue()).collect(Collectors.toList());
        customerService.deleteAll(FXCollections.observableList(customers));
        this.setModel();
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        var customer = customersTableView.getSelectionModel().getSelectedItem();
        if(customer != null) {
            this.showAddCustomer(event, customer.getValue());
        }
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
//        this.showAddCustomer(event, null);
    }


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


    private void setButtonsDisablePropertyBinding() {
        if(buttonDeleteCustomer != null) {
            buttonDeleteCustomer.disableProperty().bind(
                    Bindings.isEmpty(customersTableView.getSelectionModel().getSelectedItems())
            );
        }
        if(buttonUpdateCustomer != null) {
            buttonUpdateCustomer.disableProperty().bind(
                    Bindings.size(customersTableView.getSelectionModel().getSelectedItems()).isNotEqualTo(1)
            );
        }
    }
}