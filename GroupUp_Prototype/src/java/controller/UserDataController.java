/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import model.UserData;

/**
 *
 * @author MD
 */
@ManagedBean
@SessionScoped
public class UserDataController implements Serializable {

    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private UserTransaction utx;
    
    private UserData userData = null;
    
    /**
     * Creates a new instance of UserDataController
     */
    public UserDataController() {
    }

    public UserData getUserData() {
        if( userData == null) {
            userData = new UserData();
        }
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
    
    public String createUser() {
        try {
            utx.begin();
            em.persist(this.userData);
            utx.commit();
            // return string of next webpage
            return "userInformation";
        } catch (Exception e) {        
            // copy pasted this stuff, do sth about it later
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                    "Error creating user!",
                                                    "Unexpected error when creating your account.  Please contact the system Administrator");
            Logger.getAnonymousLogger().log(Level.SEVERE,
                                            "Unable to create new user",
                                            e);
            return null;
        }
    }

    public void edit(UserData item) {
        em.merge(item);
    }

    public void remove(UserData item) {
        em.remove(em.merge(item));
    }

    public UserData find(Object id) {
        return em.find(UserData.class, id);
    }

    public List<UserData> findAll() {
        return em.createQuery("select object(o) from UserData as o").getResultList();
    }

    public List<UserData> findRange(int maxResults, int firstResult) {
        Query q = em.createQuery("select object(o) from UserData as o");
        q.setMaxResults(maxResults);
        q.setFirstResult(firstResult);
        return q.getResultList();
    }

    public int getUserDataCount() {
        return ((Long) em.createQuery("select count(o) from UserData as o").getSingleResult()).intValue();
    }
}
