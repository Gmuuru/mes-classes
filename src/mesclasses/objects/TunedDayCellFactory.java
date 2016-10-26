/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects;

import java.time.DayOfWeek;
import java.time.LocalDate;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

/**
 *
 * @author rrrt3491
 */
public class TunedDayCellFactory implements Callback<DatePicker, DateCell> {

    private boolean allowSundays;
    private DatePicker previousPicker;
    private DatePicker nextPicker;
    
    @Override
    public DateCell call(final DatePicker datePicker) {
        
        return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if(!allowSundays && item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                }
                if (previousPicker != null && item.isBefore(
                        previousPicker.valueProperty().get())
                    ) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                }  
                if (nextPicker != null && item.isAfter(
                        previousPicker.valueProperty().get())
                    ) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                }  
            }
        };
    }

    public boolean isAllowSundays() {
        return allowSundays;
    }

    public TunedDayCellFactory setAllowSundays(boolean allowSundays) {
        this.allowSundays = allowSundays;
        return this;
    }

    public DatePicker getPreviousPicker() {
        return previousPicker;
    }

    public TunedDayCellFactory setPreviousPicker(DatePicker previousPicker) {
        this.previousPicker = previousPicker;
        return this;
    }

    public DatePicker getNextPicker() {
        return nextPicker;
    }

    public TunedDayCellFactory setNextPicker(DatePicker nextPicker) {
        this.nextPicker = nextPicker;
        return this;
    }

    
}
