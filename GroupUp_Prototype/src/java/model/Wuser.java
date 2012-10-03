/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mduppes
 */
@Entity
@Table(name = "WUSER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Wuser.findAll", query = "SELECT w FROM Wuser w"),
    @NamedQuery(name = "Wuser.findById", query = "SELECT w FROM Wuser w WHERE w.id = :id"),
    @NamedQuery(name = "Wuser.findByFirstname", query = "SELECT w FROM Wuser w WHERE w.firstname = :firstname"),
    @NamedQuery(name = "Wuser.findByLastname", query = "SELECT w FROM Wuser w WHERE w.lastname = :lastname"),
    @NamedQuery(name = "Wuser.findByPassword", query = "SELECT w FROM Wuser w WHERE w.password = :password"),
    @NamedQuery(name = "Wuser.findBySince", query = "SELECT w FROM Wuser w WHERE w.since = :since"),
    @NamedQuery(name = "Wuser.findByUsername", query = "SELECT w FROM Wuser w WHERE w.username = :username")})
public class Wuser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "LASTNAME")
    private String lastname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "SINCE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date since;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "USERNAME")
    private String username;

    public Wuser() {
    }

    public Wuser(Integer id) {
        this.id = id;
    }

    public Wuser(Integer id, String firstname, String lastname, String password, String username) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wuser)) {
            return false;
        }
        Wuser other = (Wuser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Wuser[ id=" + id + " ]";
    }
    
}
