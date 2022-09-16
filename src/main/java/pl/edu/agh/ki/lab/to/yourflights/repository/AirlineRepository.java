package pl.edu.agh.ki.lab.to.yourflights.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.ki.lab.to.yourflights.model.Airline;
import pl.edu.agh.ki.lab.to.yourflights.model.User;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    Airline findByName(String name);
    Airline findByUser(User user);
}
