package pl.edu.agh.ki.lab.to.yourflights.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.model.Customer;
import pl.edu.agh.ki.lab.to.yourflights.service.CustomerService;
import pl.edu.agh.ki.lab.to.yourflights.utils.Validator;

import java.io.IOException;


/**
 * Kontroler obsługujący formularz do dodawania klientów
 */
@Component
public class AddCustomerController {

    private final Resource customerView;
    private final Resource userCustomerView;

    private final ApplicationContext applicationContext;

    @FXML
    public TextField firstName, lastName, country,city, street, postalCode, phoneNumber, emailAddress;

    @FXML
    public Label firstNameValidationLabel, lastNameValidationLabel,
            countryValidationLabel, cityValidationLabel, streetValidationLabel,
            postalCodeValidationLabel, phoneNumberValidationLabel, emailAddressValidationLabel;

    @FXML
    public Text actiontarget;

    private CustomerService customerService;
    private Customer customer;

    /**
     * Metoda ustawiająca klienta do edycji
     * @param customer klient do edycji, może być nullem
     */
    public void setData(Customer customer) {
        this.customer = customer;
        updateControls();
    }

    /**
     * Metoda aktualizująca wartości pól tekstowych, w zależności od otrzymanego klienta do edycji
     */
    private void updateControls() {
        firstName.textProperty().setValue(customer.getFirstName());
        lastName.textProperty().setValue(customer.getSecondName());
        country.textProperty().setValue(customer.getCountry());
        city.textProperty().setValue(customer.getCity());
        street.textProperty().setValue(customer.getStreet());
        postalCode.textProperty().setValue(customer.getPostalCode());
        phoneNumber.textProperty().setValue(customer.getPhoneNumber());
        emailAddress.textProperty().setValue(customer.getEmailAddress());
    }

    /**
     * Metoda aktualizująca klienta po edycji
     */
    private void updateModel() {
        customer.setFirstName(firstName.textProperty().getValue());
        customer.setSecondName(lastName.textProperty().getValue());
        customer.setCountry(country.textProperty().getValue());
        customer.setCity(city.textProperty().getValue());
        customer.setStreet(street.textProperty().getValue());
        customer.setPostalCode(postalCode.textProperty().getValue());
        customer.setPhoneNumber(phoneNumber.textProperty().getValue());
        customer.setEmailAddress(emailAddress.textProperty().getValue());
    }

    /**
     * Metoda obsługująca dodawanie klienta po naciśnięciu przycisku "submit" w formularzu
     * Zaimplementowana została podstawowa obsługa sprawdzania poprawności wpisanych wartości
     * @param actionEvent event emitowany przez przycisk
     */
    public void handleSubmitButtonAction(ActionEvent actionEvent) {

        //Obsługa poprawności danych w formularzu
        //Wykorzystuje klasę Validator, w której zaimplementowane są metody do sprawdzania poprawności danych
        boolean firstNameValidation = Validator.validateNotEmpty(firstName, firstNameValidationLabel);
        boolean lastNameValidation = Validator.validateNotEmpty(lastName, lastNameValidationLabel);
        boolean countryValidation = Validator.validateNotEmpty(country, countryValidationLabel);
        boolean cityValidation = Validator.validateNotEmpty(city, cityValidationLabel);
        boolean streetValidation = Validator.validateNotEmpty(street, streetValidationLabel);
        boolean postalCodeValidation = Validator.validateNotEmpty(postalCode, postalCodeValidationLabel);
        boolean phoneNumberValidation = Validator.validateNotEmpty(phoneNumber, phoneNumberValidationLabel);
        boolean emailAddressValidation = Validator.validateEmail(emailAddress, emailAddressValidationLabel);
        if(!firstNameValidation || !lastNameValidation || !countryValidation || !cityValidation || !streetValidation
                || !postalCodeValidation || !phoneNumberValidation || !emailAddressValidation) {
            return;
        }

        //Stworzenie nowego klienta (jeśli to było dodawanie) lub edycja istniejącego
        if (customer == null) {
            customer = new Customer(firstName.getText(),lastName.getText(),country.getText(),city.getText(),street.getText(),postalCode.getText(),phoneNumber.getText(),emailAddress.getText());
        } else {
            updateModel();
        }

        customerService.save(customer);

//        wyczyszczenie pól formularza
        actiontarget.setText("Customer added successfully!");
        firstName.clear();
        lastName.clear();
        country.clear();
        city.clear();
        street.clear();
        postalCode.clear();
        phoneNumber.clear();
        emailAddress.clear();
        customer=null;

        //Po dodaniu klienta zakończonym sukcesem, następuje powrót do widoku listy klientów
        showCustomersView(actionEvent);
    }


    public AddCustomerController(@Value("classpath:/view/CustomersView.fxml") Resource customerView,
                                 @Value("classpath:/view/UserView/UserCustomersView.fxml") Resource userCustomerView,
                                 ApplicationContext applicationContext,
                                 CustomerService customerService){
        this.customerView = customerView;
        this.applicationContext = applicationContext;
        this.customerService = customerService;
        this.userCustomerView = userCustomerView;
    }


    public void showCustomersView(ActionEvent actionEvent) {
        try {
            //ładujemy widok z pliku .fxml
            FXMLLoader fxmlloader;
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

            //ładujemy widok w zależności od roli zalogowanego użytkownika
            if(role.equals("[ROLE_ADMIN]")){
                fxmlloader = new FXMLLoader(customerView.getURL());
            }
            else{
                fxmlloader = new FXMLLoader(userCustomerView.getURL());
            }

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
}
