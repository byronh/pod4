/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import javax.faces.context.FacesContext;
import model.UserData;

/**
 *
 * @author MD
 */
@Named(value = "loginManager")
@SessionScoped
public class loginManager implements Serializable {

    
    private UserData user;
    /**
     * Creates a new instance of loginManager
     */
    
    public UserData getUser() {
        if (user == null) {
            Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (principal != null) {
                // find user from db
            }
        }
        return user;
    }
    
    public loginManager() {
    }
}
