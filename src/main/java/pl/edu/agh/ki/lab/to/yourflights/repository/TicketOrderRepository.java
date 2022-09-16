package pl.edu.agh.ki.lab.to.yourflights.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ki.lab.to.yourflights.model.Reservation;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketCategory;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketOrder;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketOrderRepository extends JpaRepository<TicketOrder, UUID> {
    List<TicketOrder> findByReservation(Reservation reservation);
    List<TicketOrder> findByTicketCategory(TicketCategory ticketCategory);
}
