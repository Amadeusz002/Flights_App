package pl.edu.agh.ki.lab.to.yourflights.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
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
public class LoginController {


    /**
     * Kontekst aplikacji Springa
     */
    private final ApplicationContext applicationContext;

    /**
     * Widoki
     */
    private final Resource mainView;
    private final Resource anonymousMainView;

    private final Resource navigationView;

    /**
     * Pola potrzebne do autentykacji użytkownika
     */
    @Autowired
    private AuthenticationManager authManager;
    private ObservableList<String> userRoles = FXCollections.observableArrayList();

    /**
     * Pola tekstowe do formularza logowania
     */
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public Label usernameLabel;

    /**
     * Konstruktor, Spring wstrzykuje odpowiednie zależności
     * @param applicationContext
     * @param mainView
     * @param anonymousMainView
     */
    public LoginController(ApplicationContext applicationContext,
                           @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                           @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                           @Value("classpath:/view/Navigation/NavigationView.fxml") Resource navigationView) {
        this.applicationContext = applicationContext;
        this.mainView = mainView;
        this.anonymousMainView = anonymousMainView;
        this.navigationView = navigationView;
    }

    /**
     * Metoda obsługująca logowanie
     * @param event
     */
    @FXML
    void handleLogin(ActionEvent event) {

        final String userName = usernameField.getText().trim();
        final String userPassword = passwordField.getText().trim();

        try {
            Authentication request = new UsernamePasswordAuthenticationToken(userName, userPassword);
            Authentication result = authManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);

            updateUserInfo();

            usernameField.clear();
            passwordField.clear();
            showMainView(event);

        } catch (AuthenticationException e) {
            usernameLabel.setText("Incorrect username or password");
            usernameLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Metoda do aktualizacji informacji o użytkowniku
     */
    private void updateUserInfo(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> grantedAuthorities = auth.getAuthorities().stream().map(Object::toString).collect(Collectors.toList());
        userRoles.clear();
        userRoles.addAll(grantedAuthorities);
    }

    /**
     * Metoda służąca do przejścia do głównego widoku
     * @param actionEvent event emitowany przez przycisk
     */
    //todo change name to showNavigationView
    public void showMainView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlloader;
            fxmlloader = new FXMLLoader(navigationView.getURL());
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