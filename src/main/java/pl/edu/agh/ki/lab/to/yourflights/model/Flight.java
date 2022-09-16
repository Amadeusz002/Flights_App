package pl.edu.agh.ki.lab.to.yourflights.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.istack.NotNull;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class Flight extends RecursiveTreeObject<Flight> {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;

    @NotEmpty
    private String placeOfDeparture;
    @NotEmpty
    private String placeOfDestination;

    @NotNull
    private String departureDate;
    @NotNull
    private String arrivalDate;

    @NotNull
    private String departureTime;
    @NotNull
    private String arrivalTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airlineID")
    private Airline airline;

    public Flight(String placeOfDeparture, String placeOfDestination, String departureDate, String arrivalDate, Airline airline, String departureTime, String arrivalTime) {
        this.placeOfDeparture = placeOfDeparture;
        this.placeOfDestination = placeOfDestination;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.airline = airline;
        this.departureTime=departureTime;
        this.arrivalTime=arrivalTime;
    }

    public Flight() {}

    public void setPlaceOfDeparture(String placeOfDeparture) {
        this.placeOfDeparture = placeOfDeparture;
    }

    public void setPlaceOfDestination(String placeOfDestination) {
        this.placeOfDestination = placeOfDestination;
    }

    public void setDepartureDate(String departureTime) {
        this.departureDate = departureTime;
    }

    public void setArrivalDate(String arrivalTime) {
        this.arrivalDate = arrivalTime;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public String getPlaceOfDeparture() {
        return placeOfDeparture;
    }

    public String getPlaceOfDestination() {
        return placeOfDestination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public Airline getAirline() {
        return airline;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StringProperty getplaceOfDepartureProperty(){
        return new SimpleStringProperty(placeOfDeparture);
    }

    public StringProperty getplaceOfDestinationProperty(){
        return new SimpleStringProperty(placeOfDestination);
    }

    public StringProperty getdepartureDateProperty(){
        return new SimpleStringProperty(departureDate);
    }

    public StringProperty getarrivalDateProperty(){
        return new SimpleStringProperty(arrivalDate);
    }

    public StringProperty getAirlineNameProperty(){
        return new SimpleStringProperty(airline.getName());
    }

    public StringProperty getDepartureTimeProperty(){
        return new SimpleStringProperty(departureTime);
    }

    public StringProperty getArrivalTimeProperty(){
        return new SimpleStringProperty(arrivalTime);
    }
}
