package pl.edu.agh.ki.lab.to.yourflights.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketDiscount;
import java.util.List;

@Repository
public interface TicketDiscountRepository extends JpaRepository<TicketDiscount, Long> {
    List<TicketDiscount> findAllByName(String name);
}
