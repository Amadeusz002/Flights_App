package pl.edu.agh.ki.lab.to.yourflights;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.model.Reservation;
import pl.edu.agh.ki.lab.to.yourflights.service.CustomerService;
import pl.edu.agh.ki.lab.to.yourflights.service.MockDataService;
import pl.edu.agh.ki.lab.to.yourflights.service.ReservationService;
import pl.edu.agh.ki.lab.to.yourflights.service.TicketOrderService;
import pl.edu.agh.ki.lab.to.yourflights.utils.EmailHandler;

import java.io.IOException;

/**
 * Klasa służąca do inicjalizacji aplikacji
 * Nasłuchuje na StageReadyEvent emitowany przez JavafxApplication i inicjalizuje aplikację
 */
@Component
public class StageInitializer implements ApplicationListener<JavafxApplication.StageReadyEvent> {

    private final Resource mainView;
    private final Resource anonymousMainView;
    private final String applicationTitle;
    private final MockDataService mockDataService;
    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final TicketOrderService ticketOrderService;

    private final Resource navigationView;

    /**
     * Kontekst aplikacji Springa
     */
    private final ApplicationContext applicationContext;

    /**
     * Konstruktor, zależności są automatycznie wstrzykiwane przez Springa
     * @param applicationTitle tytuł aplikacji
     * @param mainView główny widok aplikacji
     * @param applicationContext kontekst aplikacji Springa
     */
    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                            @Value("classpath:/view/Navigation/NavigationView.fxml") Resource navigationView,
                            @Value("classpath:/view/MainView/MainView.fxml") Resource mainView,
                            @Value("classpath:/view/MainView/AnonymousMainView.fxml") Resource anonymousMainView,
                            ApplicationContext applicationContext,
                            MockDataService mockDataService,
                            ReservationService reservationService,
                            CustomerService customerService,
                            TicketOrderService ticketOrderService) {
        this.applicationTitle = applicationTitle;
        this.navigationView = navigationView;
        this.mainView = mainView;
        this.anonymousMainView = anonymousMainView;
        this.applicationContext = applicationContext;
        this.mockDataService = mockDataService;
        this.reservationService=reservationService;
        this.customerService=customerService;
        this.ticketOrderService=ticketOrderService;
    }

//    /**
//     * Metoda reagująca na StageReadyEvent i inicjalizująca aplikację
//     */
//    @Override
//    public void onApplicationEvent(JavafxApplication.StageReadyEvent event) {
//        try {
//            //ładujemy główny widok z pliku .fxml
//            FXMLLoader fxmlloader = new FXMLLoader(anonymousMainView.getURL());
//
//            //Spring wstrzykuje odpowiedni kontroler obsługujący dany plik .fxml na podstawie kontekstu aplikacji
//            fxmlloader.setControllerFactory(applicationContext::getBean);
//
//            //wczytanie sceny
//            Parent parent = fxmlloader.load();
//
//            //pobieramy stage z którego wywołany został event
//            Stage stage = event.getStage();
//
//            //wypełnienie wbudowanej bazy danych przykładowymi danymi startowymi
////            mockDataService.createMockData();
//
//            //utworzenie i wyświetlenie sceny
//            Scene scene = new Scene(parent);
//            stage.setScene(scene);
//            stage.setTitle(applicationTitle);
//            stage.show();
//
//            EmailHandler emailHandler=new EmailHandler(reservationService,ticketOrderService);
//            emailHandler.upcomingEmail(customerService,ticketOrderService);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Metoda reagująca na StageReadyEvent i inicjalizująca aplikację
     */
    @Override
    public void onApplicationEvent(JavafxApplication.StageReadyEvent event) {
        try {
            //ładujemy główny widok z pliku .fxml
            FXMLLoader fxmlloader = new FXMLLoader(navigationView.getURL());

            //Spring wstrzykuje odpowiedni kontroler obsługujący dany plik .fxml na podstawie kontekstu aplikacji
            fxmlloader.setControllerFactory(applicationContext::getBean);

            //wczytanie sceny
            Parent parent = fxmlloader.load();

            //pobieramy stage z którego wywołany został event
            Stage stage = event.getStage();

            //wypełnienie wbudowanej bazy danych przykładowymi danymi startowymi
//            mockDataService.createMockData();

            //utworzenie i wyświetlenie sceny
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setTitle(applicationTitle);
            stage.show();

            EmailHandler emailHandler=new EmailHandler(reservationService,ticketOrderService);
            emailHandler.upcomingEmail(customerService,ticketOrderService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
