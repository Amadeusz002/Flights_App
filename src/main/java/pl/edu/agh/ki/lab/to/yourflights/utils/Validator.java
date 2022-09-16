package pl.edu.agh.ki.lab.to.yourflights.utils;

import com.jfoenix.controls.JFXTimePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Class implementing form validation methods
 */
public class Validator {
    public Validator(){}

    /**
     * Method checking if form field is not empty
     * @param field form field
     * @param label error message label
     * @return true if the field is not empty, false otherwise
     */
    public static boolean validateNotEmpty(TextField field, Label label) {
        if(field.getText() == null || field.getText().isEmpty()) {
            label.setText("This field cannot be empty!");
            label.setTextFill(Color.RED);
            return false;
        } else {
            label.setText("Correct!");
            label.setTextFill(Color.GREEN);
        }
        return true;
    }

    public static boolean validateNotEmpty(ComboBox comboBox, Label label) {
        if(comboBox.getValue() == null) {
            label.setText("This field cannot be empty!");
            label.setTextFill(Color.RED);
            return false;
        } else {
            label.setText("Correct!");
            label.setTextFill(Color.GREEN);
        }
        return true;
    }

    public static boolean validateMoneyFormat(TextField field, Label label) {
        String value = field.textProperty().getValue();
        if (!Pattern.matches("[0-9]*\\.?[0-9]?[0-9]?", value)) {
            label.setText("Wrong money format!");
            label.setTextFill(Color.RED);
            return false;
        } else {
            try {
                BigDecimal decimal = new BigDecimal(value);
            }
            catch (Exception e) {
                label.setText("Error while parsing money!");
                label.setTextFill(Color.RED);
                return false;
            }
            label.setText("Correct!");
            label.setTextFill(Color.GREEN);
        }
        return true;
    }

    public static boolean validatePositiveNumber(TextField field, Label label) {
        String value = field.textProperty().getValue();
        try {
            double aDouble = Double.parseDouble(value);
            if( aDouble <= 0 || aDouble >= 500) {
                label.setText("Wrong number range!");
                label.setTextFill(Color.RED);
                return false;
            }
        }
        catch (Exception e) {
            label.setText("Wrong number format!");
            label.setTextFill(Color.RED);
            return false;
        }
        label.setText("Correct!");
        label.setTextFill(Color.GREEN);
        return true;
    }

    /**
     * Date validation method
     * @param departure - departure date
     * @param arrival - departure time
     * @param label - error message label
     * @return true if both dates have valid format and departure date is before arrival date, false otherwise
     */
    public static boolean validateDate(DatePicker departure, DatePicker arrival, Label label) {
        if (departure.getValue() == null || departure.getValue().toString().isEmpty()) {
            label.setText(departure.getId() + " cannot be empty!");
            label.setTextFill(Color.RED);
            return false;
        } else {
            String date_format=departure.getValue().toString();
            if (date_format.matches("\\\\d{4}-[01]\\\\d-[0-3]\\\\d")) {
                label.setText("Date format is  incorrect!");
                label.setTextFill(Color.RED);
                return false;
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.setLenient(false);
                try {
                    df.parse(departure.getValue().toString());
                    if(departure.getValue().getDayOfMonth()>arrival.getValue().getDayOfMonth() ||
                            departure.getValue().getMonthValue()>arrival.getValue().getMonthValue() ||
                    departure.getValue().getYear()>arrival.getValue().getYear()){
                        label.setText("Departure date can not be after arrival time!");
                        label.setTextFill(Color.RED);
                        return false;
                    }
                    else {
                        label.setText("Correct!");
                        label.setTextFill(Color.GREEN);
                        return true;
                    }
                } catch (ParseException ex) {
                    label.setText("Date format is incorrect!");
                    label.setTextFill(Color.RED);
                    return false;
                }

            }
        }
    }

    public static boolean validateTime(JFXTimePicker departure, JFXTimePicker arrival, DatePicker departureDate, DatePicker arrivalDate,  Label label){
        if (departure.getValue() == null || departure.getValue().toString().isEmpty() || arrival.getValue()==null) {
            if(departure.getValue()==null){
                label.setText(departure.getId() + " cannot be empty!");
                label.setTextFill(Color.RED);
                return false;
            }else{
                label.setText(arrival.getId() + " cannot be empty!");
                label.setTextFill(Color.RED);
                return false;
            }

        } else {
            SimpleDateFormat df = new SimpleDateFormat("H:mm");
            df.setLenient(false);
            try {
                df.parse(departure.getValue().toString());
                if(departureDate.getValue().getYear()==arrivalDate.getValue().getYear() &&
                    departureDate.getValue().getMonthValue()==arrivalDate.getValue().getMonthValue() &&
                        departureDate.getValue().getDayOfMonth()==arrivalDate.getValue().getDayOfMonth()) {
                    if (departure.getValue().getHour() > arrival.getValue().getHour() ||
                            (departure.getValue().getHour()==arrival.getValue().getHour() &&
                                    departure.getValue().getMinute()>=arrival.getValue().getMinute())) {
                        label.setText("Departure time can not be after arrival time!");
                        label.setTextFill(Color.RED);
                        return false;
                    }
                    else {
                        label.setText("Correct!");
                        label.setTextFill(Color.GREEN);
                        return true;
                    }
                }
                else {
                    label.setText("Correct!");
                    label.setTextFill(Color.GREEN);
                    return true;
                }
            } catch (ParseException ex) {
                label.setText("Time format is incorrect!");
                label.setTextFill(Color.RED);
                return false;
            }
        }
    }

    public static boolean validateEmail(TextField field, Label label) {
        // regex accepting emails in format person@mail.pl asd@a.a (they have to contain a letter or digit
        // in first position, digit in lat position, '@' and '.' in the middle)
        if (!Pattern.matches("\\A\\s*[a-zA-Z0-9]+([-._]?[A-z0-9]+)*(\\+[A-z0-9]+([-._]?[A-z0-9]+)*)?@[A-z0-9]+([-.]?[a-z0-9]+)*\\.[A-z]+\\s*", field.getText())) {
            label.setText("Email address incorrect!");
            label.setTextFill(Color.RED);
            return false;
        } else {
            label.setText("Correct!");
            label.setTextFill(Color.GREEN);
        }
        return true;
    }

}
