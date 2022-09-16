package pl.edu.agh.ki.lab.to.yourflights;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Główna klasa aplikacji
 * Jest aplikacją Springa i uruchamia aplikację Javafx zdefiniowaną w klasie JavafxApplication
 */
@SpringBootApplication
public class YourflightsApplication {

    public static void main(String[] args) {
        Application.launch(JavafxApplication.class, args);
    }
}
