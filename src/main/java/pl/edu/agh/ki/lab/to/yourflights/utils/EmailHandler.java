package pl.edu.agh.ki.lab.to.yourflights.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.agh.ki.lab.to.yourflights.model.Customer;
import pl.edu.agh.ki.lab.to.yourflights.model.Flight;
import pl.edu.agh.ki.lab.to.yourflights.model.Reservation;
import pl.edu.agh.ki.lab.to.yourflights.model.TicketOrder;
import pl.edu.agh.ki.lab.to.yourflights.repository.ReservationRepository;
import pl.edu.agh.ki.lab.to.yourflights.service.CustomerService;
import pl.edu.agh.ki.lab.to.yourflights.service.ReservationService;
import pl.edu.agh.ki.lab.to.yourflights.service.TicketOrderService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailHandler {

    private final ReservationService reservationService;
    private final TicketOrderService ticketOrderService;
    public EmailHandler(ReservationService reservationService, TicketOrderService ticketOrderService){
        this.reservationService=reservationService;
        this.ticketOrderService=ticketOrderService;

    }

    public void sendEmail(CustomerService customerService, Flight flight, Reservation reservation) {


        String from = "trywialne.pomijamy@gmail.com";
        String password = "admin1615";
        String host = "smtp.gmail.com";
        String port = "587";
        Properties properties = new Properties();
        // Setup mail server
        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate departureDate = LocalDate.parse(flight.getDepartureDate(), formatter);
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        LocalTime departureTime = LocalTime.parse(flight.getDepartureTime());


        Runnable task = () -> {
            try {
                MimeMessage message = new MimeMessage(session);
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                List<Customer> customerList = customerService.findByUsername(username);
                String to = customerList.get(0).getEmailAddress();
                message.setFrom(from);
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject("Reservation");
                message.setText("You have created reservation for flight from " + flight.getPlaceOfDeparture() + " to " + flight.getPlaceOfDestination() + " leaving " + flight.getDepartureDate() + " at " + flight.getDepartureTime() + ".");
                reservation.setStatus("Created - informed");
                reservationService.save(reservation);
                Transport.send(message);

            } catch (MessagingException mex) {
                mex.printStackTrace();
            }
        };

        if (reservation.getStatus().equals("Created"))
        {
            executorService.submit(task);
        }

        executorService.shutdown();

        if(departureDate.getYear()==localDate.getYear() && departureDate.getMonth()==localDate.getMonth()
                && (departureDate.getDayOfMonth()==localDate.getDayOfMonth() ||
                (departureDate.getDayOfMonth()==localDate.getDayOfMonth()+1 && departureTime.getHour()<localTime.getHour())))
        {
            Runnable task1 = () -> {
                try {
                    MimeMessage message = new MimeMessage(session);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    List<Customer> customerList = customerService.findByUsername(username);
                    String to = customerList.get(0).getEmailAddress();
                    message.setFrom(from);
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                    message.setSubject("Approaching flight!");
                    message.setText("Your flight from " + flight.getPlaceOfDeparture() + " to " + flight.getPlaceOfDestination() + " leaving " + flight.getDepartureDate() + " at " + flight.getDepartureTime()
                            + " is approaching! Please arrive at the airport at least 2h before departure to check in your luggage. \nEnjoy your flight! " );
                    reservation.setStatus("Created - approaching");
                    reservationService.save(reservation);
                    Transport.send(message);

                } catch (MessagingException mex) {
                    mex.printStackTrace();
                }
            };

            executorService1.submit(task1);
            executorService1.shutdown();
        }


    }

    public void upcomingEmail(CustomerService customerService, TicketOrderService ticketOrderService){

        String from = "trywialne.pomijamy@gmail.com";
        String password = "admin1615";
        String host = "smtp.gmail.com";
        String port = "587";
        Properties properties = new Properties();
        // Setup mail server
        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        List<Reservation> reservations=reservationService.findAll();
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() != null && reservation.getStatus().equals("Created - informed")) {

                //Flight flightTmp = reservation.getTicketOrders().get(0).getTicketCategory().getFlight();
                Flight flightTmp = ticketOrderService.findByReservation(reservation).get(0).getTicketCategory().getFlight();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
                LocalDate departureDate = LocalDate.parse(flightTmp.getDepartureDate(), formatter);
                LocalDate localDate = LocalDate.now();
                LocalTime localTime = LocalTime.now();
                LocalTime departureTime = LocalTime.parse(flightTmp.getDepartureTime());
                if(departureDate.getYear()==localDate.getYear() && departureDate.getMonth()==localDate.getMonth()
                && (departureDate.getDayOfMonth()==localDate.getDayOfMonth() ||
                        (departureDate.getDayOfMonth()==localDate.getDayOfMonth()+1 && (departureTime.getHour()<localTime.getHour() ||
                                (departureTime.getHour()==localTime.getHour() && departureTime.getMinute()<localTime.getMinute())))))
                {
                    Runnable task1 = () -> {
                        try {
                            MimeMessage message = new MimeMessage(session);
                            List<Customer> customerList = customerService.findByUsername(reservation.getUserName());
                            String to = customerList.get(0).getEmailAddress();
                            message.setFrom(from);
                            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                            message.setSubject("Approaching flight!");
                            message.setText("Your flight from " + flightTmp.getPlaceOfDeparture() + " to " + flightTmp.getPlaceOfDestination() + " leaving " + flightTmp.getDepartureDate() + " at " + flightTmp.getDepartureTime()
                                    + " is approaching! Please arrive at the airport at least 2h before departure to check in your luggage. \nEnjoy your flight! " );
                            reservation.setStatus("Created - approaching!");
                            reservationService.save(reservation);
                            Transport.send(message);

                        } catch (MessagingException mex) {
                            mex.printStackTrace();
                        }
                    };

                    executorService.submit(task1);
                    executorService.shutdown();

                }
            }
        }
    }

}

