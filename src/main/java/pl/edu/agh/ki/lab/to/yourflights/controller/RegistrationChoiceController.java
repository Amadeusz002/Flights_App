package pl.edu.agh.ki.lab.to.yourflights.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.JavafxApplication;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RegistrationChoiceController {


    /**
     * Kontekst aplikacji Springa
     */
    private final ApplicationContext applicationContext;

    /**
     * Widoki
     */
    private final Resource mainView;
    private final Resource anonymousMainView;
    private final Resource registrationView;
    private final Resource registrationAirlineView;

    /**
     * Konstruktor, Spring wstrzykuje odpowiednie zależności
     * @param applicationContext
     * @param mainView
     * @param anonymousMainView
     */
    public RegistrationChoiceController(ApplicationContext applicationContext,
                           @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                           @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                                        @Value("classpath:/view/AuthView/RegistrationView.fxml") Resource registrationView,
                                        @Value("classpath:/view/AuthView/AirlineRegistrationView.fxml") Resource registrationAirlineView) {
        this.applicationContext = applicationContext;
        this.mainView = mainView;
        this.anonymousMainView = anonymousMainView;
        this.registrationView = registrationView;
        this.registrationAirlineView = registrationAirlineView;
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
     * Metoda służąca do przejścia do widoku rejestracji
     * @param actionEvent
     */
    public void showRegistrationView(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlloader = new FXMLLoader(registrationView.getURL());
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

    public void showRegistrationAirlineView(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlloader = new FXMLLoader(registrationAirlineView.getURL());
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