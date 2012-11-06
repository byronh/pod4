/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import model.GroupupGroup;

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

    private GroupupGroup group;
    
    /**
     * Creates a new instance of GroupScheduleController
     */
    public GroupScheduleController() {
    }
}
