package pl.edu.agh.ki.lab.to.yourflights.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class Reservation extends RecursiveTreeObject<Reservation> {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;

    @NotEmpty
    private String reservationDate;

    @NotEmpty
    private String userName;

    @NotEmpty
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerID")
    private Customer customer;

    public Reservation(String reservationDate, Customer customer, String userName, String status) {
        this.userName = userName;
        this.reservationDate = reservationDate;
        this.customer = customer;
        this.status=status;
    }

    public Reservation() {}

    public Long getId() {
        return id;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public StringProperty getReservationDateProperty(){
        return new SimpleStringProperty(reservationDate);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public StringProperty getUserNameProperty() {
        return new SimpleStringProperty(userName);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
