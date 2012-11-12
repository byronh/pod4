/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 * this controller should give the controller team an idea about how to
 * implement the controller of the pick list
 * @author Shuyi
 */

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import model.GroupupUser;
import org.primefaces.event.TransferEvent;

import org.primefaces.model.DualListModel;
  
@ManagedBean(name = "pickListControllerOfEditGroup")
@ViewScoped

public class ExampleOfEditGroupPickListControllerBean {

	private DualListModel<GroupupUser> players;
	
	private DualListModel<String> cities;

	public ExampleOfEditGroupPickListControllerBean() {
		//Players
		List<GroupupUser> source = new ArrayList<GroupupUser>();
		List<GroupupUser> target = new ArrayList<GroupupUser>();
		
                GroupupUser user = new GroupupUser();
                user.setFname("Shuyi");
		source.add(user);
                user = new GroupupUser();
                user.setFname("Mark");
		source.add(user);
		user = new GroupupUser();
                user.setFname("Crystal");
		source.add(user);
		user = new GroupupUser();
                user.setFname("Geram");
		source.add(user);
		user = new GroupupUser();
                user.setFname("Leopard");
		source.add(user);
		
		players = new DualListModel<GroupupUser>(source, target);
		
		//Cities
		List<String> citiesSource = new ArrayList<String>();
		List<String> citiesTarget = new ArrayList<String>();
		
		citiesSource.add("Shuyi");
		citiesSource.add("Mark");
		citiesSource.add("Crystal");
		citiesSource.add("Geram");
		citiesSource.add("Leopard");
		
		cities = new DualListModel<String>(citiesSource, citiesTarget);
	}
	
	public DualListModel<GroupupUser> getPlayers() {
		return players;
	}
	public void setPlayers(DualListModel<GroupupUser> players) {
		this.players = players;
	}
	
	public DualListModel<String> getCities() {
		return cities;	
}
	public void setCities(DualListModel<String> cities) {
		this.cities = cities;
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
                    