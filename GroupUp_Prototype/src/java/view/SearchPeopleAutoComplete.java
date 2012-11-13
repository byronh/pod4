/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.GroupupUser;

/**
 *
 * @author Shuyi
 */
@ManagedBean(name = "autoCompleteForUser")
@ViewScoped
public class SearchPeopleAutoComplete {

    public static List<GroupupUser> userInDatabase;

    public SearchPeopleAutoComplete() {
        userInDatabase = new ArrayList<GroupupUser>();
        GroupupUser user = new GroupupUser();
        user.setFname("Shuyi");
        user.setLname("Wang");
        user.setEmail("sdjksdafji@gmail.com");
        user.setId(1);
        userInDatabase.add(user);
        user = new GroupupUser();
        user.setFname("Mark");
        user.setLname("Du");
        user.setEmail("mark@gmail.com");
        user.setId(2);
        userInDatabase.add(user);
        user = new GroupupUser();
        user.setFname("Crystal");
        user.setLname("Ng");
        user.setEmail("crystal@gmail.com");
        user.setId(3);
        userInDatabase.add(user);
        user = new GroupupUser();
        user.setFname("Geram");
        user.setLname("Tam");
        user.setEmail("geramtam@gmail.com");
        user.setId(4);
        userInDatabase.add(user);
        user = new GroupupUser();
        user.setFname("Leopard");
        user.setLname("Lan");
        user.setEmail("leopard@gmail.com");
        user.setId(5);
        userInDatabase.add(user);
    }

    public List<GroupupUser> completeUser(String query) {
        List<GroupupUser> suggestions = new ArrayList<GroupupUser>();

        for (GroupupUser p : userInDatabase) {
            if (p.getFname().startsWith(query) || p.getLname().startsWith(query) || p.getEmail().startsWith(query)) {
                suggestions.add(p);
            }
        }

        return suggestions;
    }
}
