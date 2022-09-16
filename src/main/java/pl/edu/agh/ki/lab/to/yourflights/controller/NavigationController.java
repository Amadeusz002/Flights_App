package pl.edu.agh.ki.lab.to.yourflights.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;

import java.io.IOException;
import java.util.Collections;

@Component
public class NavigationController {

    private final Resource navigationView;
    private final Resource mainView;
    private final Resource airlinesView;
    private final Resource flightsView;
    private final Resource customersView;
    private final Resource loginView;
    private final ApplicationContext applicationContext;

    @FXML
    private AnchorPane componentPane;

    @FXML
    private JFXButton buttonLogin;

    @FXML
    private JFXButton buttonLogout;

    public NavigationController(@Value("classpath:/view/Navigation/NavigationView.fxml") Resource navigationView,
                                @Value("classpath:/view/Main/MainView.fxml") Resource mainView,
                                @Value("classpath:/view/Airlines/AirlinesView.fxml") Resource airlinesView,
                                @Value("classpath:/view/Flights/FlightsView.fxml") Resource flightsView,
                                @Value("classpath:/view/Customers/CustomersView.fxml") Resource customersView,
                                @Value("classpath:/view/AuthView/LoginView.fxml") Resource loginView,
                                ApplicationContext applicationContext) {
        this.navigationView = navigationView;
        this.mainView = mainView;
        this.airlinesView = airlinesView;
        this.flightsView = flightsView;
        this.customersView = customersView;
        this.loginView = loginView;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        this.setupBindings();
        this.showMainView();
    }

    public void showMainView() {
        try {
            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(mainView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            this.componentPane.getChildren().clear();
            this.componentPane.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAirlinesView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(airlinesView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            this.componentPane.getChildren().clear();
            this.componentPane.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showFlightsView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(flightsView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            this.componentPane.getChildren().clear();
            this.componentPane.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCustomersView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(customersView.getURL());
            fxmlloader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlloader.load();
            this.componentPane.getChildren().clear();
            this.componentPane.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
    void handleLogout(ActionEvent event) {
        JavafxApplication.logout();
        try {
            FXMLLoader fxmlloader = new FXMLLoader(navigationView.getURL());
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

    private void setupBindings() {
        String r = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        ObservableList role = FXCollections.observableArrayList(Collections.singletonList(r));

        buttonLogin.visibleProperty().bind(
                Bindings.valueAt(role, 0)
                .isEqualTo("[ROLE_ANONYMOUS]")
        );

        buttonLogout.visibleProperty().bind(
                Bindings.valueAt(role, 0)
                        .isNotEqualTo("[ROLE_ANONYMOUS]")
        );
    }
}
