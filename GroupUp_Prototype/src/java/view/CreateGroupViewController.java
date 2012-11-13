/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 * this controller should give the controller team an idea about how to
 * implement the controller of the pick list
 *
 * @author Shuyi
 */
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.GroupupUser;

@ManagedBean(name = "createGroupViewController")
@ViewScoped
public class CreateGroupViewController {

    private List<GroupupUser> selectedUserList;
    private GroupupUser selectedUser;

    public CreateGroupViewController() {
        selectedUserList = new ArrayList<GroupupUser>();
    }

    public GroupupUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(GroupupUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<GroupupUser> getSelectedUserList() {
        return selectedUserList;
    }

    public void setSelectedUserList(List<GroupupUser> selectedUserList) {
        this.selectedUserList = selectedUserList;
    }

    public void addButtonActionListener() {
        System.out.println(selectedUser.getFname());
        if (selectedUser != null && !selectedUserList.contains(selectedUser)) {
            selectedUserList.add(selectedUser);
        }
    }
}
