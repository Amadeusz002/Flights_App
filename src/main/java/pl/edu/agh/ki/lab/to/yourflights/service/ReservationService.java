package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.model.Reservation;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketOrder;
import pl.edu.agh.ki.lab.to.yourflights.repository.FlightRepository;
import pl.edu.agh.ki.lab.to.yourflights.repository.ReservationRepository;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketOrderService ticketOrderService;

    public ReservationService(ReservationRepository reservationRepository, TicketOrderService ticketOrderService) {
        this.reservationRepository = reservationRepository;
        this.ticketOrderService = ticketOrderService;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> findByUserName(String userName) {
        return reservationRepository.findByUserName(userName);
    }

    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    public void deleteAll(ObservableList<Reservation> reservations) {
        reservationRepository.deleteAll(reservations);
    }

    public boolean save(Reservation reservation) {
        if(reservation != null) {
            List<Reservation> reservations = findByUserName(reservation.getUserName());
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }
}
