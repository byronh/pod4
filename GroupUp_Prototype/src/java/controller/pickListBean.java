/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author crysng
 * 
 * A test controller file containing dummy group/user data for groupEvent and
 * createGroup views. Autocomplete methods are also here.
 * 
 */

import java.util.ArrayList;  
import java.util.List;  
import javax.faces.application.FacesMessage;  
import javax.faces.context.FacesContext;  
import javax.inject.Named;
import org.primefaces.event.TransferEvent;  
  
import org.primefaces.model.DualListModel;
import model.GroupupGroup;
import model.GroupupUser;
import org.primefaces.event.SelectEvent;

@Named(value = "pickListBean")
public class pickListBean {  
  
    private DualListModel<GroupupGroup> groups;  
    private GroupupUser selectedUser;
    
    //autocomplete user list
    private List<GroupupUser> users;
    
    //group list
  
    public pickListBean() {  
        //Users  
        List<GroupupGroup> source = new ArrayList<GroupupGroup>();  
        List<GroupupGroup> target = new ArrayList<GroupupGroup>();  
        
        GroupupGroup a=new GroupupGroup();
        GroupupGroup b=new GroupupGroup();
        GroupupGroup c=new GroupupGroup();
        GroupupGroup d=new GroupupGroup();
        a.setName("Crystal's Grup");
        b.setName("Shuyi's Group");
        c.setName("Geran's Grup");
        d.setName("Mark's Group");
        
        source.add(a);  
        source.add(b);  
        source.add(c);  
        source.add(d);  
    
          
        groups = new DualListModel<GroupupGroup>(source, target);  
        
        GroupupUser e=new GroupupUser();
        GroupupUser f=new GroupupUser();
        GroupupUser g=new GroupupUser();
        GroupupUser h=new GroupupUser();
        GroupupUser i=new GroupupUser();
        e.setFname("Byron");
        f.setFname("Brian");
        g.setFname("James");
        h.setFname("Joel");
        i.setFname("Jason");
        
        e.setLname("H");
        f.setLname("A");
        g.setLname("L");
        h.setLname("C");
        i.setLname("J");
        
        e.setEmail("email@email.com");
        f.setEmail("email@email.com");
        g.setEmail("email@email.com");
        h.setEmail("e@email.com");
        i.setEmail("z@email.com");
        
        users = new ArrayList<GroupupUser>(); 
        users.add(e);  
        users.add(f);  
        users.add(g);  
        users.add(h); 
        

    }  
      
    public DualListModel<GroupupGroup> getGroups() {  
        return groups;  
    }  
    public void setGroups(DualListModel<GroupupGroup> groups) {  
        this.groups = groups;  
    }  
      
    public void onTransfer(TransferEvent event) {  
        StringBuilder builder = new StringBuilder();  
        for(Object item : event.getItems()) {  
            builder.append(((GroupupUser) item).getFname()).append("<br />");  
        }  
          
        FacesMessage msg = new FacesMessage();  
        msg.setSeverity(FacesMessage.SEVERITY_INFO);  
        msg.setSummary("Items Transferred");  
        msg.setDetail(builder.toString());  
          
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  
    
    
    //auto complete methods
     public GroupupUser getSelectedUser() {  
        return selectedUser;  
    }  
  
    public void setSelectedUser(GroupupUser selectedUser) {  
        this.selectedUser = selectedUser;  
    }  
    
    public List<GroupupUser> searchUsers(String query) {  
        List<GroupupUser> suggestions = new ArrayList<GroupupUser>();  
          
        for(GroupupUser u : users) {  
            if(u.getFname().startsWith(query))  
                suggestions.add(u);  
        }  
          
        return suggestions;  
    }  

  
}  