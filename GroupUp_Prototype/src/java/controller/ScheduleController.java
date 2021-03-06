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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import model.GroupupCourse;
import model.GroupupTimeslot;
import model.GroupupUser;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.event.SelectEvent;
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
    private ScheduleModel eventModel = new DefaultScheduleModel();
    
    // For every new activity created, the form submitted parameters fill this in
    private ScheduleEvent currentEvent = new DefaultScheduleEvent();
    
    private HashMap<String, Integer> scheduleToDbIdMap = new HashMap();
    
    private boolean loadedSchedule = false;
    
    @EJB
    private CourseSearchBean courseSearchBean;
    
    private List<GroupupCourse> courseList = new ArrayList();
    
    
    // Keeps track of string -> course mapping
    private HashMap<String, GroupupCourse> courseMap = new HashMap();
    
    private String selectedCourseString = "";
    
    private List<GroupupTimeslot> selectedCourseTimeSlots = new ArrayList();
    
    // user selection
    private GroupupCourse selectedCourse = new GroupupCourse();
    
    
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

    public List<GroupupCourse> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<GroupupCourse> courseList) {
        this.courseList = courseList;
    }

    public String getSelectedCourseString() {
        return selectedCourseString;
    }

    public void setSelectedCourseString(String selectedCourseString) {
        this.selectedCourseString = selectedCourseString;
    }

    public List<GroupupTimeslot> getSelectedCourseTimeSlots() {
        return selectedCourseTimeSlots;
    }

    public void setSelectedCourseTimeSlots(List<GroupupTimeslot> selectedCourseTimeSlots) {
        this.selectedCourseTimeSlots = selectedCourseTimeSlots;
    }

    

    public GroupupCourse getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(GroupupCourse selectedCourse) {
        this.selectedCourse = selectedCourse;
    }
        
    
    
    /**
     * Creates a new instance of ScheduleController
     */
    public ScheduleController() {
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
    
    // Load current user schedule form DB
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
                    
                    ScheduleEvent event;
                    if (timeSlot.getCourseId() != null) {
                        addCourseToSchedule(timeSlot);
                        continue;
                    } else
                    if (timeSlot.getGroupId() != null) {
                        event = new DefaultScheduleEvent(timeSlot.getTitle(), timeSlot.getStartTime(), timeSlot.getEndTime(), "group1");
                    } else {
                        event = new DefaultScheduleEvent(timeSlot.getTitle(), timeSlot.getStartTime(), timeSlot.getEndTime());
                    }
                    eventModel.addEvent(event);
                    scheduleToDbIdMap.put(event.getId(), timeSlot.getId());
                }
                
                loadedSchedule = true;
                System.out.println("loaded schedule for user: " + user.getEmail());
            }
            
        }
    }
    
    public void addCourseToSchedule(GroupupTimeslot timeslot) {
        System.out.println("Adding course to schedule");
        Calendar startTerm1 = new GregorianCalendar(2012, 8, 1);
        Calendar endTerm1 = new GregorianCalendar(2012, 11, 5);
        Calendar startTerm2 = new GregorianCalendar(2013, 0, 2);
        Calendar endTerm2 = new GregorianCalendar(2013, 3, 15);


        Calendar startDate, endDate;
        if (timeslot.getCourseId().getTerm() == 1) {
            startDate = startTerm1;
            endDate = endTerm1;
        } else if (timeslot.getCourseId().getTerm() == 2) {
            startDate = startTerm2;
            endDate = endTerm2;
        } else {
            startDate = startTerm1;
            endDate = endTerm2;
        }

        // make sure start < end
        if (startDate.after(endDate)) {
            System.out.println("Something wrong with course time: " + timeslot);
            return;
        }
        int dayOfWeekCalendar = startDate.DAY_OF_WEEK;
        dayOfWeekCalendar = (timeslot.getDayOfWeek() - dayOfWeekCalendar + 8) % 7;
        startDate.add(Calendar.DATE, dayOfWeekCalendar);
        System.out.println("Start date: " + startDate.DAY_OF_WEEK + ", adding: " + dayOfWeekCalendar);
        
        
        Calendar startSlot = (Calendar) startDate.clone();
        startSlot.add(Calendar.HOUR, timeslot.getStartTime().getHours());
        startSlot.add(Calendar.MINUTE, timeslot.getStartTime().getMinutes());
        Calendar endSlot = (Calendar) startDate.clone();
        endSlot.add(Calendar.HOUR, timeslot.getEndTime().getHours());
        endSlot.add(Calendar.MINUTE, timeslot.getEndTime().getMinutes());
        
            String courseName = timeslot.getCourseId().getDept() + " " + timeslot.getCourseId().getCoursenum();
        while (startSlot.before(endDate)) {
            System.out.println("Class: " + courseName);
            System.out.println("Start: " + startSlot.getTime());
            System.out.println("End: " + endSlot.getTime());
            ScheduleEvent event = new DefaultScheduleEvent(courseName, startSlot.getTime(), endSlot.getTime(), "user2");
            eventModel.addEvent(event);
            scheduleToDbIdMap.put(event.getId(), timeslot.getId());
            startSlot.add(Calendar.DATE, 7);
            endSlot.add(Calendar.DATE, 7);
        }
    }
    
    
    
    
    // loads all courses from DB
    public void loadCourses() {
        courseList = courseSearchBean.findAll();
        System.out.println("Loaded all courses from DB. Total: " + courseList.size());
    }
    
    // Course search encode to display on html
    public String encodeCourse(GroupupCourse course) {
        String result = course.getDept() + " " + course.getCoursenum() + " Term: " + course.getTerm() + " Section: " + course.getSection();
        
        courseMap.put(result, course);
        
        return result;
    }
    
    // course search decode from html string -> course
    public GroupupCourse decodeCourse(String courseString) {
        GroupupCourse course = courseMap.get(courseString);
        
        if (course == null) {
            System.out.println("Error in autocomplete, user must have skipped it");
        }
        return course;
    }
    
    public void addCourseToSchedule() {
        System.out.println("Adding course to schedule: " + this.selectedCourse);
        if (this.selectedCourse == null) {
            System.out.println("Selected course is null");
        }
        
        GroupupUser user = getUser();
        try {
            utx.begin();
            for(GroupupTimeslot timeslot : selectedCourseTimeSlots) {
                // Inefficient but declare over and over to set correct datetime

                
                user.addTimeSlot(timeslot);
                this.addCourseToSchedule(timeslot);
                timeslot.addUser(user);
                
                em.merge(timeslot);
            }
            em.merge(user);
            utx.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Finished adding course");
        
    }
    
    public List<String> completeClasses(String query) {
        List<String> suggestions = new ArrayList<String>();
        System.out.println("Searching for: " + query);
        // Linear search for now..
        List<String> goodSuggestions = new ArrayList<String>();
        for (GroupupCourse course : this.courseList) {
            int score = 0;
            // Only search based on department and course number, false positives may occur if other number match in the query
            if (query.contains(course.getDept()) || query.contains(course.getDept().toLowerCase())) {
                score += 1;
            } 
            
            if (query.contains(course.getCoursenum())) {
                score += 1;
            }
            
            if (score == 2) {
                goodSuggestions.add(encodeCourse(course));
            } else if(score == 1) {
                if ( suggestions.size() < 10) {
                    suggestions.add(encodeCourse(course));
                }
            }
        }
        goodSuggestions.addAll(suggestions);
        return goodSuggestions;
    }
    
    public void onSelectedClass(SelectEvent event) {
        System.out.println("User selected class");

        selectedCourse = decodeCourse(selectedCourseString);
        selectedCourseTimeSlots = new ArrayList(selectedCourse.getGroupupTimeslotCollection());
        System.out.println("course: " + selectedCourseString + ", timeslots: " + selectedCourseTimeSlots);
        for (GroupupTimeslot slot : selectedCourseTimeSlots) {
            System.out.println("DOWINT: " + slot.getDayOfWeek() + ", start: " + slot.getStartTime());
        }
        selectedCourseString = "";
        courseMap.clear();
    }
    
}