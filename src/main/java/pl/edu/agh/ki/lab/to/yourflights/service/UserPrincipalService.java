package pl.edu.agh.ki.lab.to.yourflights.service;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.lab.to.yourflights.model.Customer;
import pl.edu.agh.ki.lab.to.yourflights.model.User;
import pl.edu.agh.ki.lab.to.yourflights.model.UserPrincipal;
import pl.edu.agh.ki.lab.to.yourflights.repository.UserRepository;
import java.util.List;

@Service
public class UserPrincipalService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).get(0);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByUsername(String username){return userRepository.findByUsername(username);}

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void deleteAll(ObservableList<User> users) {
        userRepository.deleteAll(users);
    }

    public void deleteWithGivenCustomers(ObservableList<Customer> customers){
        for(Customer customer : customers){
            userRepository.delete(userRepository.findByUsername(customer.getUsername()).get(0));
        }
    }

    public boolean save(User user) {
        if(user != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
        }
        return false;
    }
}
