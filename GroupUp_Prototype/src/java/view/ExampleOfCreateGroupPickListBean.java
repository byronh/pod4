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
import model.User;
import org.primefaces.event.TransferEvent;

import org.primefaces.model.DualListModel;
  
@ManagedBean(name = "pickListController")
@ViewScoped

public class ExampleOfCreateGroupPickListBean {

	private DualListModel<User> players;
	
	private DualListModel<String> cities;

	public ExampleOfCreateGroupPickListBean() {
		//Players
		List<User> source = new ArrayList<User>();
		List<User> target = new ArrayList<User>();
		
                User user = new User();
                user.setFirstName("Shuyi");
		source.add(user);
                user = new User();
                user.setFirstName("Mark");
		source.add(user);
		user = new User();
                user.setFirstName("Crystal");
		source.add(user);
		user = new User();
                user.setFirstName("Geram");
		source.add(user);
		user = new User();
                user.setFirstName("Leopard");
		source.add(user);
		
		players = new DualListModel<User>(source, target);
		
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
	
	public DualListModel<User> getPlayers() {
		return players;
	}
	public void setPlayers(DualListModel<User> players) {
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
            builder.append(((User) item).getEmail()).append("<br />");
        }
        
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Items Transferred");
        msg.setDetail(builder.toString());
        
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
                    