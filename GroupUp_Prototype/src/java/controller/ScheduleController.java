/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import model.GroupupTimeslot;
import model.GroupupUser;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DualListModel;
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

    // Used for database access
    @PersistenceContext()
    private EntityManager em;
    
    // Used for transaction management
    @Resource
    private UserTransaction utx;
    
    // Schedule instance
    private ScheduleModel eventModel;
    
    // For every new activity created, the form submitted parameters fill this in
    private ScheduleEvent currentEvent;
    
    private HashMap<String, Integer> scheduleToDbIdMap;
    
    private boolean loadedSchedule;
    
    // Duplicate code in various managed bean classes to get the current logged in user
    public GroupupUser getUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Principal principal = request.getUserPrincipal();
        String userEmail = principal.getName();
        
        // Find user using email address given from principal
        Query query = em.createNamedQuery("GroupupUser.findByEmail");
        query.setParameter("email", userEmail);
        Collection<GroupupUser> users = query.getResultList();
        
        if (users.size() != 1) {
            System.out.println("getUser: Error! No user is registered for session!! session: " + userEmail);
            return null;
        }
        
        return users.iterator().next();
    }
        
    /**
     * Creates a new instance of ScheduleController
     */
    public ScheduleController() {
        eventModel = new DefaultScheduleModel();
        currentEvent = new DefaultScheduleEvent();
        scheduleToDbIdMap = new HashMap<String, Integer>();
        loadedSchedule = false;
        System.out.println("Initialized ScheduleController");
    }
    
    public void addEvent(ActionEvent actionEvent) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        // error check
        if (currentEvent.getStartDate().after(currentEvent.getEndDate())) {
            System.out.println("Error: Start date must be before End date!");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error: Start date must be before End date!"));
            return;
        }
        if(currentEvent.getId() == null) {
            eventModel.addEvent(currentEvent);  
        }
        else  {
            eventModel.updateEvent(currentEvent);  
        }
        
        
        GroupupUser user = getUser();
        Integer dbId = scheduleToDbIdMap.get(currentEvent.getId());
        try {
            if (dbId == null) {
                utx.begin();
            
                GroupupTimeslot newSlot = new GroupupTimeslot();
                Collection<GroupupUser> users = new ArrayList<GroupupUser>();
                users.add(user);
                newSlot.setTimeSlotCollection(users);
                newSlot.setStartTime(currentEvent.getStartDate());
                newSlot.setEndTime(currentEvent.getEndDate());
                newSlot.setTitle(currentEvent.getTitle());
                user.getGroupupTimeslotCollection().add(newSlot);
                em.merge(user);
                em.persist(newSlot);
                utx.commit();
                
                scheduleToDbIdMap.put(currentEvent.getId(), newSlot.getId());
                
            } else {
                utx.begin();
                // retrieve existing timeslot from DB and update
                Query query = em.createNamedQuery("GroupupTimeslot.findById");
                query.setParameter("id", dbId);
                Collection<GroupupTimeslot> slots = query.getResultList();
                
                if (slots.size() != 1) {
                    System.out.println("error in # of corresponding events: " + slots.size());
                } else {
                    GroupupTimeslot slot = slots.iterator().next();
                    slot.setStartTime(currentEvent.getStartDate());
                    slot.setEndTime(currentEvent.getEndDate());
                    slot.setTitle(currentEvent.getTitle());
                    em.merge(slot);
                }
                utx.commit();
            }
        } catch (RollbackException e) {
            System.out.println(e.getStackTrace().toString());
        } catch (Exception e) {
            // copy pasted this stuff, do sth about it later
            System.out.println(e.getStackTrace().toString());
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

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public ScheduleEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(ScheduleEvent currentEvent) {
        this.currentEvent = currentEvent;
    }
    
    public void loadSchedule() {

        if (!loadedSchedule) {
            GroupupUser user = getUser();

            // Get this user's timeslots
            if (user == null ) {
                System.out.println("Error loading user schedule");
            } else {
                Iterator<GroupupTimeslot> timeslotIterator = user.getGroupupTimeslotCollection().iterator();


                while(timeslotIterator.hasNext()) {
                    GroupupTimeslot timeSlot = timeslotIterator.next();
                    System.out.println("Populating event: " + timeSlot.getTitle() + ", from: " + timeSlot.getStartTime() + ", to: " + timeSlot.getEndTime());
                    ScheduleEvent event = new DefaultScheduleEvent(timeSlot.getTitle(), timeSlot.getStartTime(), timeSlot.getEndTime());
                    eventModel.addEvent(event);
                    scheduleToDbIdMap.put(event.getId(), timeSlot.getId());
                }
                loadedSchedule = true;
                System.out.println("loaded schedule for user: " + user.getEmail());
            }
        }
    }
    
    
    
    
}