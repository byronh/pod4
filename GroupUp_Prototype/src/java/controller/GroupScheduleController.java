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
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;
import model.GroupupGroup;
import model.GroupupUser;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author mduppes
 * 
 * This class controls the group functionality of the group schedule, such as:
 * Creating a new group
 * Adding users to the group
 * Removing users from the group / leaving from the group
 * Adding group events
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
    
    // These variables are used for selection, and holds fields entered by the user
    private GroupupGroup group;
    private List<GroupupGroup> groupList = new ArrayList();
    
    // Used for group member movement
    private List<String> selectedUserList = new ArrayList();
    private GroupupUser selectedUser;
    private String groupName;
    
    private List<GroupupUser> searchUsers;
    
    private boolean loadedFromDb = false;
    /**
     * Creates a new instance of GroupScheduleController
     */ 
    public GroupScheduleController() {
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
        System.out.println("WTF");
        return searchUsers;
    }

    public void setSearchUsers(List<GroupupUser> searchUsers) {
        this.searchUsers = searchUsers;
    }
    
    
    public String onEditGroupClick() {
        if (group == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Must select group!"));
            return null;
        }
        
        return "Group Edit";
        
    }
    
    public String createGroup() {
        System.out.println("Inside creategroup");
        if (groupName.length() == 0) {
            System.out.println("Error in string length!");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Group name must not be empty!"));
            return null;
        }
        
        System.out.println(this.selectedUser);
        
        
        System.out.println("Attempting to create new group: " + groupName);
        try {
            utx.begin();
            GroupupGroup newGroup = new GroupupGroup();
            newGroup.setName(groupName);
            Collection<GroupupUser> groupUsers = new ArrayList<GroupupUser>();
            groupUsers.add(getUser());
            
            // email to user conversion
            for (String userString : selectedUserList ) {
                
                GroupupUser user = decodeUserString(userString);
                if (user == null) {
                    System.out.println("Something wrong with user encoding, should not be here");
                }
                if (!groupUsers.contains(user)) {
                    groupUsers.add(user);
                }
            }
            
            newGroup.setGroupupUserCollection(groupUsers);
            
            em.persist(newGroup);
            utx.commit();
        }   catch (Exception e) {
            
            System.out.println(e.getMessage().toString());
        }
        return "Group Event";
        
    }
    
    // Code related to group membership
    // Source is the side that is not in the group, shows up as autocomplete
    List<GroupupUser> groupMemberSource = new ArrayList<GroupupUser>();
    // Target belongs in the groups.
    List<GroupupUser> groupMemberTarget = new ArrayList<GroupupUser>();

    public List<GroupupUser> getGroupMemberSource() {
        return groupMemberSource;
    }

    public void setGroupMemberSource(List<GroupupUser> groupMemberSource) {
        this.groupMemberSource = groupMemberSource;
    }

    public List<GroupupUser> getGroupMemberTarget() {
        return groupMemberTarget;
    }

    public void setGroupMemberTarget(List<GroupupUser> groupMemberTarget) {
        this.groupMemberTarget = groupMemberTarget;
    }
    
    public void loadValues() {
        System.out.println("Loading values");
        if (!loadedFromDb) {
            loadUsers();
            loadedFromDb = true;
        }
        
        loadGroupList();   
        
        
    }
    
    public void loadGroupList() {
        TypedQuery<GroupupGroup> query = em.createQuery("SELECT DISTINCT g FROM GroupupUser u LEFT JOIN u.groupupGroupCollection g WHERE u = :user", GroupupGroup.class);
        GroupupUser currentUser = getUser();
        query.setParameter("user", currentUser);
        
        this.groupList = new ArrayList(query.getResultList());
        if (this.groupList.get(0) == null) {
            this.groupList.clear();
        }
        System.out.println("Loaded groups: " + this.groupList);
    }
    
    public void loadMemberSelectList() {
        groupMemberTarget = new ArrayList(this.group.getGroupupUserCollection());
        groupMemberSource = new ArrayList();
    }
    
    public void saveGroupMembers() {
        for ( GroupupUser selectedUser : groupMemberTarget ) {
            group.inviteUser(selectedUser);
        }
                 
        try {
            utx.begin();
            em.merge(group);
            utx.commit();
        } catch (Exception e) {
            System.out.println(e.getStackTrace().toString());
        }
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
            if (p.getFname().startsWith(query) || p.getLname().startsWith(query) || p.getEmail().startsWith(query)) {
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
