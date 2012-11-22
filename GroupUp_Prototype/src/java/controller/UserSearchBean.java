/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.GroupupUser;

/**
 *
 * @author mduppes
 */
@Stateless(mappedName="userSearchBean")
public class UserSearchBean {

    // Used for database access
    @PersistenceContext()
    private EntityManager em;
    
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<GroupupUser> findByFirstName(String firstName) {
        Query query = em.createNamedQuery("GroupupUser.findByFname");
        query.setParameter("fname", firstName);
        return query.getResultList();
    }
    
    public List<GroupupUser> findByLastName(String lastName) {
        Query query = em.createNamedQuery("GroupupUser.findByLname");
        query.setParameter("lname", lastName);
        return query.getResultList();
    }
    
    public GroupupUser findByEmail(String email) {
        Query query = em.createNamedQuery("GroupupUser.findByEmail");
        query.setParameter("email", email);
        List<GroupupUser> results = query.getResultList();
        // If empty, size  =1 and first object is null
        if (results.size() != 1) {
            System.out.println("Error, unique email matches more than 1 user:" + email + ", " + results);
        }
        return (GroupupUser) query.getResultList().get(0);
    }
    
    public GroupupUser findById(String id) {
        Query query = em.createNamedQuery("GroupupUser.findById");
        query.setParameter("id", id);
        
        List<GroupupUser> results = query.getResultList();
        // If empty, size  =1 and first object is null
        if (results.size() != 1) {
            System.out.println("Error, unique ID matches more than 1 user:" + results);
        }
        return (GroupupUser) query.getResultList().get(0);
    }
    
    public List<GroupupUser> findAll() {
        Query query = em.createNamedQuery("GroupupUser.findAll");
        return query.getResultList();
    }
    
}
