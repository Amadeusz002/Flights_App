package pl.edu.agh.ki.lab.to.yourflights.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class TicketDiscount extends RecursiveTreeObject<TicketDiscount> {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;

    private double discount;
    @NotEmpty
    private String name;

    public TicketDiscount(double discount, String name) {
        this.discount = discount;
        this.name = name;
    }

    public TicketDiscount() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public StringProperty getNameProperty(){
        return new SimpleStringProperty(name);
    }

    public StringProperty getDiscountProperty(){
        return new SimpleStringProperty(Double.toString(discount));
    }
}
