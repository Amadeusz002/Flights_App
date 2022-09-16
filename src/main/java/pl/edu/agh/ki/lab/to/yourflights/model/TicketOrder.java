package pl.edu.agh.ki.lab.to.yourflights.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entity class representing ticket order
 * Example ticket order: 3 tickets in Business Class, with Student Discount,
 * in given reservation (single reservation can have multiple ticket orders)
 */
@Entity
public class TicketOrder extends RecursiveTreeObject<TicketOrder> {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;

    @NotNull
    private int numberOfSeats;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticketDiscountID")
    private TicketDiscount ticketDiscount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservationId")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticketCategoryID")
    private TicketCategory ticketCategory;

    public TicketOrder() {}

    public TicketOrder(int numberOfSeats, TicketDiscount ticketDiscount, Reservation reservation, TicketCategory ticketCategory){
        this.numberOfSeats = numberOfSeats;
        this.ticketDiscount = ticketDiscount;
        this.reservation = reservation;
        this.ticketCategory = ticketCategory;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public TicketDiscount getTicketDiscount() {
        return ticketDiscount;
    }

    public void setTicketDiscount(TicketDiscount ticketDiscount) {
        this.ticketDiscount = ticketDiscount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public TicketCategory getTicketCategory() {
        return ticketCategory;
    }

    public void setTicketCategory(TicketCategory ticketCategory) {
        this.ticketCategory = ticketCategory;
    }

    public StringProperty getNumberOfSeatsProperty(){
        return new SimpleStringProperty(Integer.toString(numberOfSeats));
    }
}
