package pl.edu.agh.ki.lab.to.yourflights;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Klasa definiuje aplikację Javafx
 * Jest uruchamiana przez metodę main w klasie głównej YourflightsApplication
 */
public class JavafxApplication extends Application {

    /**
     * Kontekst aplikacji Springa
     */
    private ConfigurableApplicationContext applicationContext;

    /**
     * Uruchomienie aplikacji, zostaje wyemitowany StageReadyEvent informujący o gotowości stage
     */
    @Override
    public void start(Stage stage) {
        initSecurity();
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    /**
     * Event emitowany po tym kiedy aplikacja jest gotowa
     * Pozwala na przechwycenie tego eventu w klasie StageInitializer i zainicjalizowanie aplikacji
     */
    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }

    /**
     * Inicjalizacja, definiuje kontekst aplikacji Springa
     */
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(YourflightsApplication.class).run();
    }

    /**
     * Zatrzymanie aplikacji
     */
    @Override
    public void stop() {
        applicationContext.stop();
        Platform.exit();
    }


    public static void initSecurity() {
        SecurityContextHolder.setStrategyName("MODE_GLOBAL");
        initAnonymous();
    }

    public static void initAnonymous() {
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(
                "anonymous", "anonymous",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void logout(){
        SecurityContextHolder.clearContext();
        initAnonymous();
    }
}
