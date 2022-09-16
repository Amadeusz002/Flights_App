package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.*;
import pl.edu.agh.ki.lab.to.yourflights.repository.TicketOrderRepository;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TicketOrderService {

    private final TicketOrderRepository ticketOrderRepository;

    public TicketOrderService(TicketOrderRepository ticketOrderRepository) {
        this.ticketOrderRepository = ticketOrderRepository;
    }

    public List<TicketOrder> findAll() {
        return ticketOrderRepository.findAll();
    }

    public List<TicketOrder> findByReservation(Reservation reservation) {
        return ticketOrderRepository.findByReservation(reservation);
    }

    public List<TicketOrder> findByTicketCategory(TicketCategory ticketCategory) {
        return ticketOrderRepository.findByTicketCategory(ticketCategory);
    }

    public void delete(TicketOrder ticketOrder) {
        ticketOrderRepository.delete(ticketOrder);
    }

    public void deleteAll(ObservableList<TicketOrder> ticketOrders) {
        ticketOrderRepository.deleteAll(ticketOrders);
    }

    public boolean save(TicketOrder ticketOrder) {
        if(ticketOrder != null) {
            ticketOrderRepository.save(ticketOrder);
        }
        return false;
    }

    public boolean saveAll(List<TicketOrder> ticketOrders) {
        if(ticketOrders.size() != 0) {
            ticketOrderRepository.saveAll(ticketOrders);
        }
        return false;
    }

    public BigDecimal getTicketOrderSummaryCost(TicketOrder ticketOrder) {
        TicketDiscount ticketDiscount = ticketOrder.getTicketDiscount();
        if(ticketDiscount == null) {
            return ticketOrder.getTicketCategory().getCategoryPrice()
                    .multiply(BigDecimal.valueOf(ticketOrder.getNumberOfSeats()))
                    .setScale(2); //totalPrice = categoryPrice * numberOfSeats
        } else {
            return ticketOrder.getTicketCategory().getCategoryPrice()
                    .multiply(BigDecimal.valueOf(ticketOrder.getNumberOfSeats())) //price = categoryPrice * numberOfSeats
                    .multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(ticketDiscount.getDiscount()).divide(new BigDecimal(100))))//totalPrice = price * ( 1 - discount/100)
                    .setScale(2);
        }
    }
}
