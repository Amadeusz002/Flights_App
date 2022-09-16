package pl.edu.agh.ki.lab.to.yourflights.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.repository.AirlineRepository;
import pl.edu.agh.ki.lab.to.yourflights.repository.CustomerRepository;
import pl.edu.agh.ki.lab.to.yourflights.repository.FlightRepository;

@Service
public class MockDataService {

    private AirlineRepository airlineRepository;
    private FlightRepository flightRepository;
    private CustomerRepository customerRepository;

    public MockDataService(AirlineRepository airlineRepository,
                           FlightRepository flightRepository,
                            CustomerRepository customerRepository) {
        this.airlineRepository = airlineRepository;
        this.flightRepository = flightRepository;
        this.customerRepository = customerRepository;
    }
}
