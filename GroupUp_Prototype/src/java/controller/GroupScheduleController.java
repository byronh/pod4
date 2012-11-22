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
import java.util.Map;
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
