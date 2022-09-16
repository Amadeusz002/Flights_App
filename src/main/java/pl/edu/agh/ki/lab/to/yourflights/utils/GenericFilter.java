package pl.edu.agh.ki.lab.to.yourflights.utils;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class GenericFilter<Type extends RecursiveTreeObject<Type>> {


    private final JFXTreeTableView<Type> filteredTable;

    private final List<Predicate<Type>> predicateList = new LinkedList<>();

    public GenericFilter(JFXTreeTableView<Type> filteredTable ) {
        this.filteredTable = filteredTable;
    }

    public void addPredicate(Predicate<Type> predicate) {
        predicateList.add(predicate);
    }

    public <K> void setListener(Property<K> property) {
        property.addListener(new ChangeListener<K>() {
            @Override
            public void changed(ObservableValue<? extends K> observable, K oldValue, K newValue) {
                filteredTable.setPredicate(typeTreeItem -> predicateList.stream()
                        .allMatch(predicate -> predicate.test(typeTreeItem.getValue())));
            }
        });
    }

}
