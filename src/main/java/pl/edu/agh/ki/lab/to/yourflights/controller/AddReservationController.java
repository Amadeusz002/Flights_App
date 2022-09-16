package pl.edu.agh.ki.lab.to.yourflights.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.lab.to.yourflights.model.*;
import pl.edu.agh.ki.lab.to.yourflights.service.*;
import pl.edu.agh.ki.lab.to.yourflights.utils.EmailHandler;
import pl.edu.agh.ki.lab.to.yourflights.utils.Validator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Kontroler obsługujący formularz do dodawania rezerwacji
 */
@Component
public class AddReservationController {


    private final Resource reservationList;

    private final ApplicationContext applicationContext;

    private final ReservationService reservationService;
    private final TicketOrderService ticketOrderService;
    private final TicketDiscountService ticketDiscountService;
    private final TicketCategoryService ticketCategoryService;
    private final CustomerService customerService;

    @FXML
    public ComboBox<Integer> seatsCombo;
    @FXML
    public ComboBox<String> ticketCategoryCombo;
    @FXML
    public ComboBox<String> discountCombo;
    @FXML
    private JFXButton buttonDeleteTicketOrder, buttonAddTicketOrder, errorField;

    @FXML
    private JFXTreeTableView<TicketOrder> reservationOverviewTableView;


    @FXML
    private TreeTableColumn<TicketOrder, String> category;
    @FXML
    private TreeTableColumn<TicketOrder, String> seats;
    @FXML
    private TreeTableColumn<TicketOrder, String> discount;
    @FXML
    private TreeTableColumn<TicketOrder, String> totalCost;

    private Flight flight;
    private Reservation reservation = null;
    private ObservableList<TicketOrder> ticketOrdersList = FXCollections.observableArrayList();


    @FXML
    public Label numberOfSeatsValidationLabel;
    @FXML
    public Label ticketCategoryValidationLabel;

    /**
     * Metoda ustawiająca lot dla którego zostanie utworzona rezerwacja
     * I aktualizująca pola formularza, jeśli jest to edycja istniejącego lotu a nie dodawanie nowego
     * @param flight lot dla rezerwacji
     */
    public void setData(Flight flight) {
        this.flight = flight;
        this.updateControls();
    }

    /**
     * Metoda modyfikująca istniejącą rezerwację
     * @param flight lot dla rezerwacji
     * @param reservation lot dla rezerwacji
     */
    public void setData(Flight flight, Reservation reservation) {
        this.flight = flight;
        this.reservation = reservation;
        ticketOrdersList.addAll(ticketOrderService.findByReservation(reservation));
        this.updateControls();
    }

    /**
     * Metoda aktualizująca wartości pól tekstowych, w zależności od otrzymanej rezerwacji do edycji
     */
    private void updateControls() {
        ticketCategoryCombo.getItems().setAll(ticketCategoryService.findByFlight(flight).stream()
                .filter(ticketCategory -> (ticketCategoryService.getNumberOfFreeSeats(ticketCategory) - getNumberOfSeatsTakenByTicketOrders(ticketCategory)) > 0)
                .map(TicketCategory::getCategoryName)
                .collect(Collectors.toList())
        );

        discountCombo.getItems().setAll(ticketDiscountService.findAll().stream().map(TicketDiscount::getName)
                .collect(Collectors.toList())
        );

        this.ticketCategoryCombo.valueProperty().setValue(null);
        this.seatsCombo.valueProperty().setValue(null);

        discountCombo.getItems().add("None");
    }

    private void setModel() {
        //Ustawienie kolumn
        seats.setCellValueFactory(data -> data.getValue().getValue().getNumberOfSeatsProperty());
        category.setCellValueFactory(data -> data.getValue().getValue().getTicketCategory().getNameProperty());
        discount.setCellValueFactory(data -> {
            TicketOrder order = data.getValue().getValue();
            return order.getTicketDiscount() != null ? order.getTicketDiscount().getNameProperty() : new SimpleStringProperty("None");
        });
        totalCost.setCellValueFactory(data -> {
            TicketOrder ticketOrder = data.getValue().getValue();
            TicketCategory ticketCategory = data.getValue().getValue().getTicketCategory();
            TicketDiscount ticketDiscount = data.getValue().getValue().getTicketDiscount();

            if(ticketCategory == null){
                return null;
            }

            return new SimpleStringProperty(ticketOrderService.getTicketOrderSummaryCost(ticketOrder).toString());
        });

        //Przekazanie danych do tabeli
        final TreeItem<TicketOrder> root = new RecursiveTreeItem<>(ticketOrdersList, RecursiveTreeObject::getChildren);
        reservationOverviewTableView.setRoot(root);
        reservationOverviewTableView.setShowRoot(false);
    }

    private void showErrorMessage(JFXButton errorField, String errorMessage) {
        errorField.setText(errorMessage);
        errorField.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
    }

    private void showSuccessMessage(JFXButton errorField, String successMessage) {
        errorField.setText(successMessage);
        errorField.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
    }


    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        //Obsługa poprawności danych w formularzu
        //Wykorzystuje klasę Validator, w której zaimplementowane są metody do sprawdzania poprawności danych

        if(ticketOrdersList.size() == 0) {
            showErrorMessage(errorField, "You cannot add empty reservation!");
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        //Stworzenie nowej rezerwacji i wyczyszczenie pól formularza
        if(reservation == null) {
            // sprawdzamy czy nie istnieje już rezerwacja w tym czasie
            List<Reservation> reservationList = reservationService.findByUserName(userName).stream()
                    .filter(reservation1 -> {
                        if(ticketOrderService.findByReservation(reservation1).size() != 0) {
                            Flight flightTmp = ticketOrderService.findByReservation(reservation1).get(0).getTicketCategory().getFlight();
                            try {
                                Date dateFromTmp = new SimpleDateFormat("dd/MM/yyyy").parse(flightTmp.getDepartureDate());
                                Date dateToTmp = new SimpleDateFormat("dd/MM/yyyy").parse(flightTmp.getArrivalDate());
                                Date dateFrom = new SimpleDateFormat("dd/MM/yyyy").parse(flight.getDepartureDate());
                                Date dateTo = new SimpleDateFormat("dd/MM/yyyy").parse(flight.getArrivalDate());

                                if(dateFrom.after(dateToTmp) || dateTo.before(dateFromTmp)) {
                                    return false;
                                } else {
                                    return true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());

            if (reservationList.size() != 0) {
                // informujemy użytkownika o błędzie
                showErrorMessage(errorField, "You have a reservation in this time slot already!");
                return;
            }
//         Tworzymy rezerwację i zamówienie biletów dla niej
            reservation =
                    new Reservation(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            null, userName, "Created");
            // Zapisujemy w bazie odpowiednie relacje
        }
        List<TicketDiscount> ticketDiscounts = ticketDiscountService.findByName(discountCombo.getValue());

        TicketDiscount ticketDiscount = ticketDiscounts.size() > 0 ? ticketDiscounts.get(0) : null;

        reservationService.save(reservation);

        ticketOrdersList.forEach(ticketOrder -> {
            ticketOrder.setReservation(reservation);
            ticketOrder.setTicketDiscount(ticketDiscount);
        });

        ticketOrderService.saveAll(ticketOrdersList);

        EmailHandler emailHandler=new EmailHandler(reservationService, ticketOrderService);
        emailHandler.sendEmail(this.customerService, this.flight, reservation);

        reservation = null;
        ticketOrdersList = FXCollections.observableArrayList();

        //Po dodaniu lotu zakończonym sukcesem, następuje powrót do widoku listy lotów
        showReservationList(actionEvent);
    }


    public AddReservationController(@Value("classpath:/view/ReservationListView.fxml") Resource reservationList,
                                    ApplicationContext applicationContext,
                                    TicketOrderService ticketOrderService,
                                    ReservationService reservationService,
                                    TicketDiscountService ticketDiscountService, TicketCategoryService ticketCategoryService, CustomerService customerService) {
        this.reservationList = reservationList;
        this.applicationContext = applicationContext;
        this.reservationService = reservationService;
        this.ticketOrderService = ticketOrderService;
        this.ticketDiscountService = ticketDiscountService;
        this.ticketCategoryService = ticketCategoryService;
        this.customerService = customerService;
    }


    @FXML
    public void initialize() {
        this.setModel();
        reservationOverviewTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setDisablePropertyBinding();
    }

    /**
     * Metoda służąca do przejścia do widoku listy rezerwacji
     * @param actionEvent event emitowany przez przycisk
     */
    public void showReservationList(ActionEvent actionEvent) {
        try {
            //ładujemy widok z pliku .fxml
            FXMLLoader fxmlloader = new FXMLLoader(reservationList.getURL());

            //Spring wstrzykuje odpowiedni kontroler obsługujący dany plik .fxml na podstawie kontekstu aplikacji
            fxmlloader.setControllerFactory(applicationContext::getBean);

            //wczytanie sceny
            Parent parent = fxmlloader.load();

            //pobieramy stage z którego wywołany został actionEvent - bo nie chcemy tworzyć za każdym razem nowego Stage
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            //utworzenie i wyświetlenie sceny
            Scene scene = new Scene(parent);
            stage.setScene(scene);

            ticketOrdersList = FXCollections.observableArrayList();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAddAction() {
        boolean numberOfSeatsValidation = Validator.validateNotEmpty(seatsCombo, numberOfSeatsValidationLabel);
        boolean ticketCategoryValidation = Validator.validateNotEmpty(ticketCategoryCombo, ticketCategoryValidationLabel);

        if(!numberOfSeatsValidation || !ticketCategoryValidation){
            showErrorMessage(errorField, "You have to fill empty fields!");
            return;
        }

        TicketCategory ticketCategory = ticketCategoryService.findByFlight(flight).stream()
                .filter(ticketCategory1 -> ticketCategory1.getCategoryName().equals(ticketCategoryCombo.getValue()))
                .findFirst().get();

        List<TicketDiscount> ticketDiscounts = ticketDiscountService.findByName(discountCombo.getValue());
        TicketDiscount ticketDiscount = ticketDiscounts.size() > 0 ? ticketDiscounts.get(0) : null;

        ticketOrdersList.addAll(new TicketOrder(seatsCombo.getValue(), ticketDiscount, reservation, ticketCategory));
        showSuccessMessage(errorField, "Ticket order added successfully!");
        ticketOrderService.saveAll(ticketOrdersList);
        setModel();
        this.updateControls();
    }

    public void handleDeleteAction() {
        var ticketOrders = reservationOverviewTableView.getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList());
        ticketOrdersList.removeAll(ticketOrders);
        ticketOrderService.deleteAll(FXCollections.observableArrayList(ticketOrders));
        showSuccessMessage(errorField, "Ticket order deleted successfully!");
        this.updateControls();
    }

    /**
     * Metoda obliczająca ile miejsc jest zajete przez obecnie dodane zamowienia na bilety dla danej kategorii
     * @param ticketCategory kategoria biletów
     * @return
     */
    private int getNumberOfSeatsTakenByTicketOrders(TicketCategory ticketCategory) {
        return ticketOrdersList.stream()
                .filter(ticketOrder -> ticketOrder.getTicketCategory().getCategoryName().equals(ticketCategory.getCategoryName()))
                .filter(ticketOrder -> ticketOrderService.findAll().contains(ticketOrder))
                .map(ticketOrder -> ticketOrder.getNumberOfSeats())
                .collect(Collectors.summingInt(Integer::intValue));
    }

    /**
     * Metoda ustawiająca liczbę miejsc do wyboru w zaleznosci od wybranej kategorii i tego ile wolnych miejsc na nia zostalo
     * @param actionEvent event
     */
    public void setSeatsComboBox(MouseEvent actionEvent) {
        TicketCategory ticketCategory = ticketCategoryService.findByFlight(flight).stream()
                .filter(category -> category.getCategoryName().equals(ticketCategoryCombo.getValue()))
                .collect(Collectors.toList())
                .get(0);

        int numberOfSeatsTakenByTicketOrders = getNumberOfSeatsTakenByTicketOrders(ticketCategory);

        int numberOfFreeSeats = ticketCategoryService.getNumberOfFreeSeats(ticketCategory) - numberOfSeatsTakenByTicketOrders;

        seatsCombo.getItems().setAll(
                IntStream.rangeClosed(1, numberOfFreeSeats).boxed().collect(Collectors.toList())
        );
    }

    private void setDisablePropertyBinding() {
        if(buttonDeleteTicketOrder != null) {
            buttonDeleteTicketOrder.disableProperty().bind(
                    Bindings.isEmpty(reservationOverviewTableView.getSelectionModel().getSelectedItems())
            );
        }

        seatsCombo.disableProperty().bind(
                Bindings
                        .isNull(ticketCategoryCombo.valueProperty())
        );
    }
}