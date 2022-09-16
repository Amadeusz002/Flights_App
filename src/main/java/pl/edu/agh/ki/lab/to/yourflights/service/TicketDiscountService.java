package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.*;
import pl.edu.agh.ki.lab.to.yourflights.repository.*;
import java.util.List;

@Service
public class TicketDiscountService {

    private final TicketDiscountRepository ticketDiscountRepository;

    public TicketDiscountService(TicketDiscountRepository ticketDiscountRepository) {
        this.ticketDiscountRepository = ticketDiscountRepository;
    }

    public List<TicketDiscount> findAll() {
        return ticketDiscountRepository.findAll();
    }

    public List<TicketDiscount> findByName(String name) {
        return ticketDiscountRepository.findAllByName(name);
    }

    public void delete(TicketDiscount ticketDiscount) {
        ticketDiscountRepository.delete(ticketDiscount);
    }

    public void deleteAll(ObservableList<TicketDiscount> ticketDiscounts) {
        ticketDiscountRepository.deleteAll(ticketDiscounts);
    }

    public boolean save(TicketDiscount ticketDiscount) {
        if(ticketDiscount != null) {
            ticketDiscountRepository.save(ticketDiscount);
        }
        return false;
    }
}
