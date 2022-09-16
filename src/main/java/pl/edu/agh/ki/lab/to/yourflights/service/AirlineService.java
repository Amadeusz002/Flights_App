package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.*;
import pl.edu.agh.ki.lab.to.yourflights.repository.AirlineRepository;
import pl.edu.agh.ki.lab.to.yourflights.repository.FlightRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;
    private TicketCategoryService ticketCategoryService;
    private TicketOrderService ticketOrderService;

    public AirlineService(AirlineRepository airlineRepository, FlightRepository flightRepository,
                            TicketCategoryService ticketCategoryService, TicketOrderService ticketOrderService) {
        this.airlineRepository = airlineRepository;
        this.flightRepository = flightRepository;
        this.ticketCategoryService = ticketCategoryService;
        this.ticketOrderService = ticketOrderService;
    }

    public List<Airline> findAll() {
        return airlineRepository.findAll();
    }

    public Airline findByName(String value){
        return airlineRepository.findByName(value);
    }

    public Airline findByUser(User user){
        return airlineRepository.findByUser(user);
    }

    public void delete(Airline airline) {
        airlineRepository.delete(airline);
    }

    public void deleteAll(ObservableList<Airline> airlines) {
        airlineRepository.deleteAll(airlines);
    }

    public void save(Airline airline) {
        if(airline != null) {
            airlineRepository.save(airline);
        }
    }

    public List<String> getCountries() {
        return this.findAll().stream().map(airline -> airline.getCountry()).distinct().collect(Collectors.toList());
    }

    public List<Reservation> getReservationsForAirline(Airline airline) {
        List<Reservation> reservations = new LinkedList<>();
        List<Flight> flights = flightRepository.findByAirline(airline);
        for(Flight flight: flights){
            List<TicketCategory> ticketCategories = ticketCategoryService.findByFlight(flight);
            for(TicketCategory ticketCategory : ticketCategories) {
                List<TicketOrder> ticketOrders = ticketOrderService.findByTicketCategory(ticketCategory);
                for(TicketOrder ticketOrder : ticketOrders) {
                    reservations.add(ticketOrder.getReservation());
                }
            }
        }
        return reservations.stream().distinct().collect(Collectors.toList());
    }

}
