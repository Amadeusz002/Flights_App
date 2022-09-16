package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.Airline;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketCategory;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketOrder;
import pl.edu.agh.ki.lab.to.yourflights.repository.FlightRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final TicketCategoryService ticketCategoryService;
    private final TicketOrderService ticketOrderService;

    public FlightService(FlightRepository flightRepository,
                         TicketCategoryService ticketCategoryService,
                         TicketOrderService ticketOrderService) {
        this.flightRepository = flightRepository;
        this.ticketCategoryService = ticketCategoryService;
        this.ticketOrderService = ticketOrderService;
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public void delete(Flight flight) {
        flightRepository.delete(flight);
    }

    public void deleteAll(ObservableList<Flight> flights) {
        flightRepository.deleteAll(flights);
    }

    public void save(Flight flight) {
        if(flight != null) {
            flightRepository.save(flight);
        }
    }

    public List<Flight> findByAirline(Airline airline){
        return flightRepository.findByAirline(airline);
    }

    public int getNumberOfTakenSeatsForFlight(Flight flight) {
        int numberOfTakenSeats = 0;
        List<TicketCategory> ticketCategories = ticketCategoryService.findByFlight(flight);
        for(TicketCategory ticketCategory : ticketCategories) {
            List<TicketOrder> ticketOrders = ticketOrderService.findByTicketCategory(ticketCategory);
            for(TicketOrder ticketOrder : ticketOrders) {
                if(ticketOrder.getTicketCategory().getId() == ticketCategory.getId()){
                    numberOfTakenSeats += ticketOrder.getNumberOfSeats();
                }
            }
        }
        return numberOfTakenSeats;
    }

    public int getNumberOfSeatsForFlight(Flight flight) {
        int numberOfSeats = 0;
        List<TicketCategory> ticketCategories = ticketCategoryService.findByFlight(flight);
        for(TicketCategory ticketCategory : ticketCategories) {
            numberOfSeats += ticketCategory.getTotalNumberOfSeats();
        }
        return numberOfSeats;
    }

    public int getNumberOfReservationsForFlight(Flight flight) {
        List<Long> reservations = new LinkedList<>();
        List<TicketCategory> ticketCategories = ticketCategoryService.findByFlight(flight);
        for(TicketCategory ticketCategory : ticketCategories) {
            List<TicketOrder> ticketOrders = ticketOrderService.findByTicketCategory(ticketCategory);
            for(TicketOrder ticketOrder : ticketOrders) {
                reservations.add(ticketOrder.getReservation().getId());
            }
        }
        return (int)reservations.stream().distinct().count();
    }

    public BigDecimal getTotalIncomeFromFlight(Flight flight) {
        BigDecimal totalIncome = new BigDecimal(0);
        List<TicketCategory> ticketCategories = ticketCategoryService.findByFlight(flight);
        for(TicketCategory ticketCategory : ticketCategories) {
            List<TicketOrder> ticketOrders = ticketOrderService.findByTicketCategory(ticketCategory);
            for(TicketOrder ticketOrder : ticketOrders) {
                totalIncome = totalIncome.add(ticketOrderService.getTicketOrderSummaryCost(ticketOrder));
            }
        }
        return totalIncome;
    }

    public List<Flight> getFlightsSortedDescendingBasedOnNumberOfReservations() {
        Map<Flight, Integer> map = new HashMap<>();
        findAll().stream().forEach(flight -> map.put(flight, getNumberOfReservationsForFlight(flight)));
        Map<Flight, Integer> result = map.entrySet()
                .stream()
                .sorted(Map.Entry.<Flight, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return new ArrayList<>(result.keySet());
    }

    public List<String> getFlightDestinationsSortedDescendingBasedOnNumberOfReservations() {
        List<Flight> flights = getFlightsSortedDescendingBasedOnNumberOfReservations();
        return flights.stream().map(flight -> flight.getPlaceOfDestination()).distinct().collect(Collectors.toList());
    }
}
