/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author byron
 */
@Entity
@Table(name = "groupup_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GroupupUser.findAll", query = "SELECT g FROM GroupupUser g"),
    @NamedQuery(name = "GroupupUser.findById", query = "SELECT g FROM GroupupUser g WHERE g.id = :id"),
    @NamedQuery(name = "GroupupUser.findByFname", query = "SELECT g FROM GroupupUser g WHERE g.fname = :fname"),
    @NamedQuery(name = "GroupupUser.findByLname", query = "SELECT g FROM GroupupUser g WHERE g.lname = :lname"),
    @NamedQuery(name = "GroupupUser.findByEmail", query = "SELECT g FROM GroupupUser g WHERE g.email = :email"),
    @NamedQuery(name = "GroupupUser.findByPassword", query = "SELECT g FROM GroupupUser g WHERE g.password = :password"),
    @NamedQuery(name = "GroupupUser.findByPublic1", query = "SELECT g FROM GroupupUser g WHERE g.public1 = :public1")})
public class GroupupUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "fname")
    private String fname;
    @Size(max = 255)
    @Column(name = "lname")
    private String lname;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "email")
    private String email;
    @Size(max = 255)
    @Column(name = "password")
    private String password;
    @Column(name = "public")
    private Boolean public1;
    @ManyToMany(mappedBy = "groupupUserCollection")
    private Collection<GroupupTimeslot> groupupTimeslotCollection;
    @ManyToMany(mappedBy = "groupupUserCollection")
    private Collection<GroupupGroup> groupupGroupCollection;

    public GroupupUser() {
    }

    public GroupupUser(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
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

    public Boolean getPublic1() {
        return public1;
    }

    public void setPublic1(Boolean public1) {
        this.public1 = public1;
    }

    @XmlTransient
    public Collection<GroupupTimeslot> getGroupupTimeslotCollection() {
        return groupupTimeslotCollection;
    }

    public void setGroupupTimeslotCollection(Collection<GroupupTimeslot> groupupTimeslotCollection) {
        this.groupupTimeslotCollection = groupupTimeslotCollection;
    }

    @XmlTransient
    public Collection<GroupupGroup> getGroupupGroupCollection() {
        return groupupGroupCollection;
    }

    public void setGroupupGroupCollection(Collection<GroupupGroup> groupupGroupCollection) {
        this.groupupGroupCollection = groupupGroupCollection;
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
        if (!(object instanceof GroupupUser)) {
            return false;
        }
        GroupupUser other = (GroupupUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.GroupupUser[ id=" + id + " ]";
    }
    
}
