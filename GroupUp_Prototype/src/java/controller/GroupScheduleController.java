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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
    
    // These variables are used for selection, and holds fields entered by the user
    private GroupupGroup group;
    private List<GroupupGroup> groupList;
    
    // Used for group member movement
    private List<GroupupUser> selectedUserList;
    private GroupupUser selectedUser;
    private String groupName;
    
    private List<GroupupUser> searchUsers;
    
    /**
     * Creates a new instance of GroupScheduleController
     */
    public GroupScheduleController() {
    }

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

    public List<GroupupUser> getSelectedUserList() {
        return selectedUserList;
    }

    public void setSelectedUserList(List<GroupupUser> selectedUserList) {
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
    
    
    
    
    public void addButtonActionListener() {
        System.out.println(selectedUser.getFname());
        if (selectedUser != null && !selectedUserList.contains(selectedUser)) {
            selectedUserList.add(selectedUser);
        }
    }
    
    public String createGroup() {
        if (groupName.length() == 0) {
            System.out.println("Error in string length!");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Group name must not be empty!"));
            return null;
        }
        
        
        System.out.println("Attempting to create new group: " + groupName);
        try {
            utx.begin();
            GroupupGroup newGroup = new GroupupGroup();
            newGroup.setName(groupName);
            Collection<GroupupUser> groupUsers = new ArrayList<GroupupUser>();
            groupUsers.add(getUser());
            newGroup.setGroupupUserCollection(groupUsers);
            
            em.persist(newGroup);
            utx.commit();
        }   catch (Exception e) {
            
            System.out.println(e.getStackTrace().toString());
        }
        return "EditGroup.xhtml";
        
    }
    
    DualListModel<GroupupUser> groupMemberSelectList;
    
    // Code related to group membership
    // Source is the side that is not in the group, shows up as autocomplete
    List<GroupupUser> groupMemberSource = new ArrayList<GroupupUser>();
    // Target belongs in the groups.
    List<GroupupUser> groupMemberTarget = new ArrayList<GroupupUser>();

    public DualListModel<GroupupUser> getGroupMemberSelectList() {
        return groupMemberSelectList;
    }

    public void setGroupMemberSelectList(DualListModel<GroupupUser> groupMemberSelectList) {
        this.groupMemberSelectList = groupMemberSelectList;
    }

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
        loadUsers();
        loadGroupList();   
    }
    
    public void loadGroupList() {
        Query query = em.createNativeQuery("GroupupGroup.findByUserId");
        GroupupUser currentUser = getUser();
        query.setParameter("id", currentUser.getId());
        this.groupList = query.getResultList();
    }
    
    public void loadMemberSelectList() {
        groupMemberTarget = new ArrayList(this.group.getGroupupUserCollection());
        groupMemberSource = new ArrayList();
        groupMemberSelectList = new DualListModel<GroupupUser>(groupMemberSource, groupMemberTarget);
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
        System.out.println("loading users");

        searchUsers = query.getResultList();
        if (searchUsers == null) {
            System.out.println("NULL");
        }
        for ( GroupupUser user : searchUsers) {
            System.out.println(user.getFname());
        }
    }
    
    public List<GroupupUser> searchUsers(String query) {
        List<GroupupUser> suggestions = new ArrayList<GroupupUser>();
        
        for (GroupupUser p : searchUsers) {
            if (p.getFname().startsWith(query) || p.getLname().startsWith(query) || p.getEmail().startsWith(query)) {
                suggestions.add(p);
            }
        }
        return suggestions;
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
