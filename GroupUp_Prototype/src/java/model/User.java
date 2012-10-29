/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author mduppes
 */
@Entity(name = "LOGIN_USER")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(unique=true, nullable=false, length=128)
    private String email;
    
    @Column(nullable=false, length=128)
    private String firstName;
    
    @Column(nullable=false, length=128)
    private String lastName;
    
    @Column(nullable=false, length=45)
    private String password;
    
    @ElementCollection
    @CollectionTable(name = "LOGIN_GROUPS",
            joinColumns = @JoinColumn(name = "email", nullable=false),
            uniqueConstraints = { @UniqueConstraint(columnNames={"email","groupname"}) } )
    @Column(nullable=false, name="groupname", length=64)
    @Enumerated(EnumType.STRING)
    private List<LoginGroup> groups;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<LoginGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<LoginGroup> groups) {
        this.groups = groups;
    }
   

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (email != null ? email.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.email == null && other.email != null) || (this.email != null && !this.email.equals(other.email))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.User[ email=" + email + " ]";
    }
    
    public User() {
        this.groups = new ArrayList<LoginGroup>();
        this.groups.add(LoginGroup.USER);
    }
    
}
