/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author MD
 * This interface is taken and adapted from the guide: http://www.primefaces.org/showcase/ui/schedule.jsf
 */
@Named(value = "scheduleController")
@SessionScoped
public class ScheduleController implements Serializable {

    // Schedule instance
    private ScheduleModel eventModel;
    
    // For every new activity created, the form submitted parameters fill this in
    private ScheduleEvent currentEvent;
    
    /**
     * Creates a new instance of ScheduleController
     */
    public ScheduleController() {
        eventModel = new DefaultScheduleModel();
        currentEvent = new DefaultScheduleEvent();
    }
    
    public void addEvent() {  
        if(currentEvent.getId() == null) {
            eventModel.addEvent(currentEvent);  
        }
        else  {
            eventModel.updateEvent(currentEvent);  
        }
        currentEvent = new DefaultScheduleEvent();
    }
    
    public void deleteEvent() {
        eventModel.deleteEvent(currentEvent);
    }
    
    public void onEventSelect(ScheduleEntrySelectEvent selectEvent) {
        currentEvent = selectEvent.getScheduleEvent();
    }
    
    public void onDateSelect(DateSelectEvent selectEvent) {
        // selected an empty event, populate with defaults.
        currentEvent = new DefaultScheduleEvent("", selectEvent.getDate(), selectEvent.getDate());  
    }
    
    
}
