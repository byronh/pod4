/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import model.GroupupGroup;
import model.GroupupTimeslot;
import model.GroupupUser;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DualListModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author mduppes
 * 
 * This bloated class controls the group functionality of the group schedule, such as:
 * Creating a new group
 * Adding users to the group
 * Removing users from the group / leaving from the group
 * Adding group events
 * group schedule functions
 * TODO: Refactor  
 * 
 */
@Named(value = "groupScheduleController")
@SessionScoped
public class GroupScheduleController implements Serializable {
    
    // Used for database access
    @PersistenceContext()
    private EntityManager em;
    
    // Used for transaction management
    @Resource
    private UserTransaction utx;
    
    // Searching users from DB
    @EJB
    private UserSearchBean userSearchBean;
    
    // Group schedule storage
    private GroupupGroup groupCurrentDiff = null;
    // Schedule instance
    private ScheduleModel eventModel = new DefaultScheduleModel();
    
    // For every new activity created, the form submitted parameters fill this in
    private ScheduleEvent currentEvent = new DefaultScheduleEvent();
    
    // Easy linking between timeslots and users
    private HashMap<ScheduleEvent, GroupupUser> eventToUserMap = new HashMap();
    private HashMap<ScheduleEvent, GroupupGroup> eventToGroupMap = new HashMap();
    
    // hack..
    private String currentEventUser = "";
    // These variables are used for selection, and holds fields entered by the user
    private GroupupGroup group;
    private String selectedGroupString;
    private List<String> groupStringList = new ArrayList();
    private HashMap<String, GroupupGroup> stringToGroupMap = new HashMap();
    private List<GroupupGroup> groupList = new ArrayList();
    private List<GroupupGroup> groupInviteList = new ArrayList();
    
    private List<GroupupTimeslot> timeslotInviteList = new ArrayList();
    
    // Used for group member movement
    private List<String> selectedUserList = new ArrayList();
    private GroupupUser selectedUser;
    private String groupName;
    
    // Used for group member invites
    private Integer selectedGroupId;
    private Integer selectedEventInviteId;
    
    private List<GroupupUser> searchUsers;
    
    private boolean loadedFromDb = false;
    
    private String groupStyleClass = "group1";
    
    /**
     * Creates a new instance of GroupScheduleController
     */ 
    public GroupScheduleController() {
    }
        
    public String encodeGroup(GroupupGroup group) {
        String encodedGroup = group.getName() + " id=" + group.getId();
        stringToGroupMap.put(encodedGroup, group);
        return encodedGroup;
    }
    
    public GroupupGroup decodeGroup(String groupString) {
        GroupupGroup decodedGroup = stringToGroupMap.get(groupString);
        
        if (decodedGroup == null) {
            System.out.println("Error in decoding group");
        }
        return decodedGroup;
    }
    // Uses group to populate eventmodel
    public void createGroupSchedule() {
        eventModel = new DefaultScheduleModel();
        eventToGroupMap = new HashMap();
        if (groupCurrentDiff == null ) {
            System.out.println("Why is group current null");
        }
        
        System.out.println("Creating new group diff schedule: ");
        int memberNumber = 1;
        for ( GroupupUser member : groupCurrentDiff.getGroupupUserCollection() ) {
            System.out.println("  Loading group member: " + member.getEmail());
            String styleClass = "user" + memberNumber;
            for (GroupupTimeslot timeSlot : member.getGroupupTimeslotCollection()) {
                if (timeSlot.getCourseId() != null ) {
                    this.addCourseToSchedule(timeSlot, styleClass);
                    continue;
                    
                }
                if (timeSlot.getGroupId() != null) {
                    
                    // This is a group timeslot
                    GroupupGroup group = timeSlot.getGroupId();
                    ScheduleEvent event = new DefaultScheduleEvent("Group " + group.getName() + ": " + timeSlot.getTitle(), timeSlot.getStartTime(), timeSlot.getEndTime(), groupStyleClass);
                    eventModel.addEvent(event);
                    eventToGroupMap.put(event, group);
                    
                    System.out.println("Populated timeslot for group: " + group.toString());
                } else {
                    // This is a normal non-group timeslot
                    
                    System.out.println("    Timeslot: " + timeSlot.getTitle() + " styleclass: " + styleClass);
                    ScheduleEvent event = new DefaultScheduleEvent(member.getFname() + ": " + timeSlot.getTitle(), timeSlot.getStartTime(), timeSlot.getEndTime(), styleClass);
                    eventModel.addEvent(event);
                    eventToUserMap.put(event, member);
                }
            }
            ++memberNumber;
        }        
        this.loadValues();
    }

    public String getCurrentEventUser() {
        return currentEventUser;
    }

    public void setCurrentEventUser(String currentEventUser) {
        this.currentEventUser = currentEventUser;
    }

    public Integer getSelectedEventInviteId() {
        return selectedEventInviteId;
    }

    public void setSelectedEventInviteId(Integer selectedEventInviteId) {
        this.selectedEventInviteId = selectedEventInviteId;
    }


    
    
    public void addGroupEvent(ActionEvent actionEvent) {
        System.out.println("Trying to add new group event");
        
        // error check
        if (currentEvent.getStartDate().after(currentEvent.getEndDate())) {
            System.out.println("Error: Start date must be before End date!");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error: Start date must be before End date!"));
            return;
        }
        if(currentEvent.getId() != null) {
            System.out.println("Error creating group event, something wrong");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error: Can only create group events here!"));
             
            return;
        }
        
        GroupupUser user = eventToUserMap.get(currentEvent);
        if (user != null ) {
            System.out.println("Error creating group event, already belongs to user");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error: Something wrong in creating group event!"));
            return;
        }
        
 
        GroupupUser currentUser = getUser();
        try {
            utx.begin();
            GroupupTimeslot newGroupEventSlot = new GroupupTimeslot();
            // Create new group event and set user to current user
            Collection<GroupupUser> users = new ArrayList<GroupupUser>();
            users.add(currentUser);
            newGroupEventSlot.setTimeSlotCollection(users);
            newGroupEventSlot.setStartTime(currentEvent.getStartDate());
            newGroupEventSlot.setEndTime(currentEvent.getEndDate());
            newGroupEventSlot.setTitle(currentEvent.getTitle());
            newGroupEventSlot.setGroupId(groupCurrentDiff);

            Collection<GroupupUser> invitedUsers = groupCurrentDiff.getGroupupUserCollection();
            // Set invited users to other groupmembers
            invitedUsers.remove(currentUser);
            newGroupEventSlot.setTimeSlotInviteCollection(invitedUsers);
            System.out.append("Creating new group event: " + newGroupEventSlot.getTitle());
            em.persist(newGroupEventSlot);
            utx.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error: Failed to add group event."));
            return;
        }
        
        eventModel.addEvent(currentEvent); 
        currentEvent = new DefaultScheduleEvent();
    }
    
    public void deleteEvent() {
        
        eventModel.deleteEvent(currentEvent);

    }
    
    
    public void onEventSelect(ScheduleEntrySelectEvent selectEvent) {

        currentEvent = selectEvent.getScheduleEvent();
        GroupupUser eventOwner = eventToUserMap.get(this.currentEvent);
        if (eventOwner == null) {
            eventOwner = getUser(); //currently logged in user
        }
        if (eventOwner == null) {
            System.out.println("Error in getting owner of event");
            currentEventUser = "Error, no owner!";
        }
        currentEventUser = eventOwner.getFname() + " " + eventOwner.getLname();
    }
    
    public void onDateSelect(DateSelectEvent selectEvent) {
        // selected an empty event, populate with defaults.
        currentEvent = new DefaultScheduleEvent("", selectEvent.getDate(), selectEvent.getDate());  
        GroupupUser user = getUser();
        currentEventUser = user.getFname() + " " + user.getLname();
    }


    // Gets the currently logged in user from session. duplicate code in various controllers for now..
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

    public Integer getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(Integer selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }

    public String getSelectedGroupString() {
        return selectedGroupString;
    }

    public void setSelectedGroupString(String selectedGroupString) {
        this.selectedGroupString = selectedGroupString;
    }

    public List<String> getGroupStringList() {
        return groupStringList;
    }

    public void setGroupStringList(List<String> groupStringList) {
        this.groupStringList = groupStringList;
    }

    public GroupupGroup getGroupCurrentDiff() {
        return groupCurrentDiff;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(ScheduleEvent currentEvent) {
        this.currentEvent = currentEvent;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void setGroupCurrentDiff(GroupupGroup groupCurrentDiff) {
        this.groupCurrentDiff = groupCurrentDiff;
    }
    
    public GroupupGroup getGroup() {
        return group;
    }

    public void setGroup(GroupupGroup group) {
        this.group = group;
    }

    public List<GroupupGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupupGroup> groupList) {
        this.groupList = groupList;
    }

    public List<GroupupGroup> getGroupInviteList() {
        return groupInviteList;
    }

    public void setGroupInviteList(List<GroupupGroup> groupInviteList) {
        this.groupInviteList = groupInviteList;
    }

    public List<GroupupTimeslot> getTimeslotInviteList() {
        return timeslotInviteList;
    }

    public void setTimeslotInviteList(List<GroupupTimeslot> timeslotInviteList) {
        this.timeslotInviteList = timeslotInviteList;
    }

    public List<String> getSelectedUserList() {
        return selectedUserList;
    }

    public void setSelectedUserList(List<String> selectedUserList) {
        this.selectedUserList = selectedUserList;
    }

    public GroupupUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(GroupupUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<GroupupUser> getSearchUsers() {
        return searchUsers;
    }

    public void setSearchUsers(List<GroupupUser> searchUsers) {
        this.searchUsers = searchUsers;
    }
    
    // Bad, duplicate code with schedule controller
    public void addCourseToSchedule(GroupupTimeslot timeslot, String styleclass) {
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
        
        GroupupUser currentUser = getUser();
        String courseName = timeslot.getCourseId().getDept() + " " + timeslot.getCourseId().getCoursenum();
        while (startSlot.before(endDate)) {
            System.out.println("Class: " + courseName);
            System.out.println("Start: " + startSlot.getTime());
            System.out.println("End: " + endSlot.getTime());
            ScheduleEvent event = new DefaultScheduleEvent(courseName, startSlot.getTime(), endSlot.getTime(), styleclass);
            eventModel.addEvent(event);
            eventToUserMap.put(event, currentUser);
            startSlot.add(Calendar.DATE, 7);
            endSlot.add(Calendar.DATE, 7);
        }
    }
    public String onDiffScheduleClick() {
        System.out.println("Diffing group schedule: ");
        GroupupUser user = getUser();
        GroupupGroup selectedGroup = decodeGroup(this.selectedGroupString);
        
        if (selectedGroup == null) {
            System.out.println("No valid group Selected");
            return null;
        } else {
            if (!selectedGroup.containsUser(user)) {
                System.out.println("User selected group they don't exist in");
                return null;
            }
        }
        // set selected group to currently diffed group
        groupCurrentDiff = selectedGroup;
        createGroupSchedule();
        return null;
    }
    
    public String onEditGroupClick() {
        GroupupGroup selectedGroup = decodeGroup(this.selectedGroupString);
        
        if (selectedGroup == null) {
            System.out.println("No valid group Selected");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Must select group!"));
            return null;
        }        
        groupName = selectedGroup.getName();
        
        this.selectedUserList = new ArrayList();
        for (GroupupUser member : selectedGroup.getGroupupUserCollection()) {
            this.selectedUserList.add(encodeUserString(member));
        }
        
        group = selectedGroup;
        
        return "Group Edit";
        
    }
    
    public String getGroupOwner(GroupupGroup group) {
        GroupupUser owner = userSearchBean.findById(group.getOwnerID().toString());
        if (owner == null)
            return "";
        return owner.getFname() + " " + owner.getLname();
    }
    
    public String getGroupInviteMessage() {
        return "You have " + this.groupInviteList.size() + " invites to groups: ";
    }
    
    public String createGroup() {
        System.out.println("Inside creategroup");
        if (groupName.length() == 0) {
            System.out.println("Error in string length!");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Group name must not be empty!"));
            return null;
        }       
        
        System.out.println("Attempting to create new group: " + groupName);
        try {
            
            utx.begin();
            // Create a new group and add the current user to the group
            GroupupGroup newGroup = new GroupupGroup();
            newGroup.setName(groupName);
            GroupupUser currentUser = getUser();
            newGroup.addUser(currentUser);
            
            newGroup.setOwnerID(currentUser.getId());
            newGroup.setOwnerName(currentUser.getFname() + " " + currentUser.getLname());
            
            // Add other members into the invite list.
            for (String userString : selectedUserList ) {
                
                GroupupUser user = decodeUserString(userString);
                if (user == null) {
                    System.out.println("Something wrong with user encoding, should not be here");
                    return null;
                }
                newGroup.inviteUser(user);
                
            }
            
            em.persist(newGroup);
            utx.commit();
        }   catch (Exception e) {
            
            System.out.println(e.getMessage().toString());
        }
        return "Group Event";
        
    }
    
    public void loadValues() {
        System.out.println("Loading values");
        if (!loadedFromDb) {
            loadUsers();
            loadedFromDb = true;
        }
        
        loadGroupList();   
        
        loadGroupInvites();
        loadGroupEventInvites();
    }
    
    public void loadGroupList() {
        TypedQuery<GroupupGroup> query = em.createQuery("SELECT DISTINCT g FROM GroupupUser u LEFT JOIN u.groupupGroupCollection g WHERE u = :user", GroupupGroup.class);
        GroupupUser currentUser = getUser();
        query.setParameter("user", currentUser);
        
        this.groupList = new ArrayList(query.getResultList());
        if (this.groupList.get(0) == null) {
            this.groupList.clear();
        }
        
        this.groupStringList = new ArrayList();
        for( GroupupGroup group : groupList) {
            System.out.println("Repopulating grouplist: " + group.getName());
            this.groupStringList.add(encodeGroup(group));
        }
        System.out.println("Loaded groups: " + this.groupList);
    }
    
    public void loadGroupInvites() {
        TypedQuery<GroupupGroup> query = em.createQuery("SELECT DISTINCT g FROM GroupupUser u LEFT JOIN u.groupupGroupInvites g WHERE u = :user", GroupupGroup.class);
        GroupupUser currentUser = getUser();
        query.setParameter("user", currentUser);
        
        this.groupInviteList = new ArrayList(query.getResultList());
        if (this.groupInviteList.get(0) == null) {
            this.groupInviteList.clear();
        }
        System.out.println("Loaded group invites: " + this.groupInviteList);
        
    }
    
    public void loadGroupEventInvites() {
        GroupupUser currentUser = getUser();
        this.timeslotInviteList = new ArrayList(currentUser.getGroupupTimeslotInvites());
        System.out.println("Loaded event invites: " + this.timeslotInviteList);
    }
    
    public void loadUsers() {
        Query query = em.createNamedQuery("GroupupUser.findAll");
        System.out.println("loading users: ");

        searchUsers = new ArrayList(query.getResultList());
        if (searchUsers == null) {
            System.out.println("NULL");
        }
        for ( GroupupUser user : searchUsers) {
            System.out.println("Users: " + user.getFname());
        }
        System.out.println("Done loading users.");
    }
    
    public void acceptEventInvite() {
        System.out.println("Accepted event invite");
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
	String timeslotId = params.get("timeslotId");
        System.out.println("Accepted invite for group event id    : " + timeslotId);
        for (GroupupTimeslot slot : this.timeslotInviteList) {
            if (slot.getId().toString().equals(timeslotId)) {
                GroupupUser user = getUser();
                
                slot.addUser(user);
                try {
                    utx.begin();
                    em.merge(slot);
                    utx.commit();
                    timeslotInviteList.remove(slot);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        // Inefficiently reload here to be sure
        loadValues();
    }
    
    public void declineEventInvite() {
        System.out.println("Declined event invite");
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
	String timeslotId = params.get("timeslotId");
        System.out.println("Declined invite for group event id    : " + timeslotId);
        for (GroupupTimeslot slot : this.timeslotInviteList) {
            if (slot.getId().toString().equals(timeslotId)) {
                GroupupUser user = getUser();
                
                slot.removeUser(user);
                try {
                    utx.begin();
                    em.merge(slot);
                    utx.commit();
                    timeslotInviteList.remove(slot);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        // Inefficiently reload here to be sure
        loadValues();
    }
    
    public void acceptInvite() {
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
	String groupId = params.get("groupId");
        System.out.println("Accepted invite for group id    : " + groupId);
        for (GroupupGroup g : this.groupInviteList) {
            if (g.getId().toString().equals(groupId)) {
                GroupupUser user = getUser();
                
                g.addUser(user);
                try {
                    utx.begin();
                    em.merge(g);
                    utx.commit();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        // Inefficiently reload here to be sure
        loadValues();
    }
    
    public void declineInvite() {
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
	String groupId = params.get("groupId");
        System.out.println("Declined invite for group id    : " + groupId);
        for (GroupupGroup g : this.groupInviteList) {
            if (g.getId().toString().equals(groupId)) {
                GroupupUser user = getUser();
                
                g.removeUser(user);
                try {
                    utx.begin();
                    em.merge(g);
                    utx.commit();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        // Inefficiently reload here to be sure
        loadValues();
    }
    public String encodeUserString(GroupupUser user) {
        String userEncode = user.getFname() + " <" + user.getEmail() + ">";
        return userEncode;
    }
    
    public GroupupUser decodeUserString(String userString) {
        String[] splitString = userString.split("<");
        if (splitString.length != 2 || splitString[1].length() == 0) {
            System.out.println("Error in userstring: " + userString);
        }
        String userEmail = splitString[1].substring(0, splitString[1].length()-1);
        GroupupUser user = userSearchBean.findByEmail(userEmail);
        return user;
    }
    
    public List<String> completeUsers(String query) {
        List<String> suggestions = new ArrayList<String>();
        System.out.println("Searching for: " + query);
        for (GroupupUser p : searchUsers) {
            if (p.getFname().contains(query) || p.getLname().contains(query) || p.getEmail().contains(query)) {
                suggestions.add(encodeUserString(p));
            }
        }
        return suggestions;
    }
    
    public String navigateToCreateGroup() {
        this.groupName = "";
        this.selectedUserList = new ArrayList();
        return "Group Create";
    }
    
    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for(Object item : event.getItems()) {
            builder.append(((GroupupUser) item).getEmail()).append("<br />");
        }
        
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Items Transferred");
        msg.setDetail(builder.toString());
        
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
