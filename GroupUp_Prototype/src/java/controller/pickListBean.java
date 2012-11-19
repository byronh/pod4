/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author crysng
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
  
    private DualListModel<GroupupUser> players;  
    private GroupupUser selectedUser;
    
    //autocomplete user list
    private List<GroupupUser> users;
  
    public pickListBean() {  
        //Players  
        List<GroupupUser> source = new ArrayList<GroupupUser>();  
        List<GroupupUser> target = new ArrayList<GroupupUser>();  
        
        GroupupUser a=new GroupupUser();
        GroupupUser b=new GroupupUser();
        GroupupUser c=new GroupupUser();
        GroupupUser d=new GroupupUser();
        a.setFname("Crystal");
        b.setFname("Shuyi");
        c.setFname("Geran");
        d.setFname("Mark");
        
        source.add(a);  
        source.add(b);  
        target.add(c);  
        target.add(d);  
    
          
        players = new DualListModel<GroupupUser>(source, target);  
        
        GroupupUser e=new GroupupUser();
        GroupupUser f=new GroupupUser();
        GroupupUser g=new GroupupUser();
        GroupupUser h=new GroupupUser();
        e.setFname("Byron- AutoComplete1");
        f.setFname("Mina- AutoComplete1");
        g.setFname("Leonard- AutoComplete1");
        h.setFname("Joel- AutoComplete1");
        
        users = new ArrayList<GroupupUser>(); 
        users.add(e);  
        users.add(f);  
        users.add(g);  
        users.add(h); 
    }  
      
    public DualListModel<GroupupUser> getPlayers() {  
        return players;  
    }  
    public void setPlayers(DualListModel<GroupupUser> players) {  
        this.players = players;  
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