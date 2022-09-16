package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.Customer;
import pl.edu.agh.ki.lab.to.yourflights.repository.CustomerRepository;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

    public void deleteAll(ObservableList<Customer> customers) {
        customerRepository.deleteAll(customers);
    }

    public void save(Customer customer) {
        if(customer != null) {
            customerRepository.save(customer);
        }
    }

    public List<Customer> findByUsername(String username){
        return customerRepository.findByUsername(username);
    }
}
