package pl.edu.agh.ki.lab.to.yourflights.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entity representing ticket category (for example: Business Category, Economic Category)
 */
@Entity
public class TicketCategory extends RecursiveTreeObject<TicketCategory> {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;

    @NotEmpty
    private String categoryName;
    @NotNull
    private BigDecimal categoryPrice;
    @NotNull
    private int totalNumberOfSeats;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flightID")
    private Flight flight;

    public TicketCategory(){}

    public TicketCategory(String categoryName, BigDecimal categoryPrice, int totalNumberOfSeats, Flight flight){
        this.categoryName = categoryName;
        this.categoryPrice = categoryPrice;
        this.totalNumberOfSeats = totalNumberOfSeats;
        this.flight = flight;
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getCategoryPrice() {
        return categoryPrice;
    }

    public void setCategoryPrice(BigDecimal categoryPrice) {
        this.categoryPrice = categoryPrice;
    }

    public int getTotalNumberOfSeats() {
        return totalNumberOfSeats;
    }

    public void setTotalNumberOfSeats(int totalNumberOfSeats) {
        this.totalNumberOfSeats = totalNumberOfSeats;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public StringProperty getFlightIDProperty(){
        return new SimpleStringProperty(Long.toString(flight.getId()));
    }

    public StringProperty getNameProperty(){
        return new SimpleStringProperty(categoryName);
    }

    public StringProperty getPriceProperty(){
        return new SimpleStringProperty(categoryPrice.toString());
    }

    public StringProperty getNumberOfSeatsProperty(){
        return new SimpleStringProperty(Integer.toString(totalNumberOfSeats));
    }
}
