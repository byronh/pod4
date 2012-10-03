/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author mduppes
 */
@ManagedBean
@SessionScoped
public class UserActionBean {
    private String userName;

    public String getUserName() {
        return "name is stored in managed bean: " + userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
}
