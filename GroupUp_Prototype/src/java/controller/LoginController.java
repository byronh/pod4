/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import model.User;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author MD
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    // Used for database access
    @PersistenceContext()
    private EntityManager em;
    
    // Used for transaction management
    @Resource
    private UserTransaction utx;
    
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String confirmPassword;
    
    public List<User> findAll() {
        return em.createQuery("select o from LOGIN_USER").getResultList();
    }
    
    public User findFromEmail(String email) {
        return em.find(User.class, email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String registerNewUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            utx.begin();
            
            User newUser = new User();
            
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            
            // Does a sha256 hash and base64 encodes passwords for now
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            newUser.setPassword(Base64.encodeBase64String(hash));
            
            em.persist(newUser);

            utx.commit();
            // return string of next webpage
            return null;
        } catch (RollbackException e) {
            context.addMessage(null, new FacesMessage("Transaction error in creating new account (Username may be taken)"));
            return null;
        
        } catch (Exception e) {
            // copy pasted this stuff, do sth about it later
            context.addMessage(null, new FacesMessage("Unexpected Error in creating new account"));
            return null;
        }
    }
    
    public String login() {
        // get current JSF context which is in charge of rendering the web page
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        String message = "";
        try {
            //Login
            request.login(email, password);
            
            //Get principal, which is the identifier for this user
            Principal principal = request.getUserPrincipal();
            
            if (request.isUserInRole("USER")) {
                message = "Logged in Username : " + principal.getName();
            } else {
                message = "Invalid Security Role!";
            }
            
            context.addMessage(null, new FacesMessage(message));
            return "/faces/facelets/ScheduleView.xhtml?faces-redirect=true";
            
        } catch (ServletException e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(e.getMessage()));
            return null;
          
        } 
    }
    
    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if(session != null){
            session.invalidate();
        }
        return "/faces/index.xhtml?faces-redirect=true";
    }
    
    public LoginController() {
        /*
        // On construction, deletes any existing sessions.
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if(session != null) {
            session.invalidate();
        }*/
    }
}
