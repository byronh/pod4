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
    private GroupupGroup group;
    private List<GroupupUser> selectedUserList;
    private GroupupUser selectedUser;
    private String groupName;
    
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

/*
            GroupupUser user = getUser();
            Collection<GroupupGroup> userGroups = user.getGroupupGroupCollection();
            userGroups.add(newGroup);
            user.setGroupupGroupCollection(userGroups);
*/
        }   catch (Exception e) {
            
            System.out.println(e.getStackTrace().toString());
        }
        return "EditGroup.xhtml";
        
    }
}
