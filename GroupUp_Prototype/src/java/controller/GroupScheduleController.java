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
import java.util.Collection;
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
    
    
    // These variables are used for selection, and holds fields entered by the user
    private GroupupGroup group;
    private String selectedGroupString;
    private List<String> groupStringList = new ArrayList();
    private HashMap<String, GroupupGroup> stringToGroupMap = new HashMap();
    private List<GroupupGroup> groupList = new ArrayList();
    private List<GroupupGroup> groupInviteList = new ArrayList();
    
    // Used for group member movement
    private List<String> selectedUserList = new ArrayList();
    private GroupupUser selectedUser;
    private String groupName;
    
    // Used for group member invites
    private Integer selectedGroupId;
    
    private List<GroupupUser> searchUsers;
    
    private boolean loadedFromDb = false;
    
    
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
        if (groupCurrentDiff == null ) {
            System.out.println("Why is group current null");
        }
        
        System.out.println("Creating new group diff schedule: ");
        int memberNumber = 1;
        for ( GroupupUser member : groupCurrentDiff.getGroupupUserCollection() ) {
            System.out.println("  Loading group member: " + member.getEmail());
            for (GroupupTimeslot timeSlot : member.getGroupupTimeslotCollection()) {
                String styleClass = "user" + memberNumber;
                System.out.println("    Timeslot: " + timeSlot.getTitle() + " styleclass: " + styleClass);
                ScheduleEvent event = new DefaultScheduleEvent(timeSlot.getTitle(), timeSlot.getStartTime(), timeSlot.getEndTime(), styleClass);
                eventModel.addEvent(event);
                eventToUserMap.put(event, member);
            }
            ++memberNumber;
        }        
        this.loadValues();
    }
    
    
        public void addGroupEvent(ActionEvent actionEvent) {
            /*
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
        * */
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
        
    }
    
    public void declineEventInvite() {
        
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
        loadGroupList();
        loadGroupInvites();
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
        loadGroupList();
        loadGroupInvites();
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
