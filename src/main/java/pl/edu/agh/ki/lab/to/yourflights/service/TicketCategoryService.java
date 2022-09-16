package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketCategory;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketOrder;
import pl.edu.agh.ki.lab.to.yourflights.repository.TicketCategoryRepository;

import java.util.LinkedList;
import java.util.List;

@Service
public class TicketCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;
    TicketOrderService ticketOrderService;

    public TicketCategoryService(TicketCategoryRepository ticketCategoryRepository,
                                 TicketOrderService ticketOrderService) {
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.ticketOrderService = ticketOrderService;
    }

    public List<TicketCategory> findAll() {
        return ticketCategoryRepository.findAll();
    }

    public List<TicketCategory> findByFlight(Flight flight) {
        if(flight == null) return new LinkedList<>();
        return ticketCategoryRepository.findAllByFlight(flight);
    }

    public void delete(TicketCategory ticketCategory) {
        ticketCategoryRepository.delete(ticketCategory);
    }

    public void deleteAll(ObservableList<TicketCategory> ticketCategories) {
        ticketCategoryRepository.deleteAll(ticketCategories);
    }

    public boolean save(TicketCategory ticketCategory) {
        if(ticketCategory != null) {
            ticketCategoryRepository.save(ticketCategory);
        }
        return false;
    }

    public int getNumberOfFreeSeats(TicketCategory ticketCategory) {
        int numberOfTakenSeats = 0;
        List<TicketOrder> ticketOrders = ticketOrderService.findByTicketCategory(ticketCategory);
        for(TicketOrder ticketOrder : ticketOrders) {
            if(ticketOrder.getTicketCategory().getId() == ticketCategory.getId()){
                numberOfTakenSeats += ticketOrder.getNumberOfSeats();
            }
        }
        return ticketCategory.getTotalNumberOfSeats() - numberOfTakenSeats;
    }
}
