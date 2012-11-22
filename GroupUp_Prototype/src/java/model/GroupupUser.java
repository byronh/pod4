/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
    @NotNull
    @GeneratedValue
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "fname", nullable=false, length=128)
    private String fname;
    
    @Column(name = "lname", nullable=false, length=128)
    private String lname;
    
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Column(name = "email", unique=true, nullable=false, length=128)
    private String email;
    
    //Sha256 Hash + Base64 encoding
    @Column(name = "password", nullable=false, length=45)
    private String password;
    
    @ElementCollection
    @CollectionTable(name = "LOGIN_GROUPS",
            joinColumns = @JoinColumn(name = "email", referencedColumnName="email", nullable=false),
            uniqueConstraints = { @UniqueConstraint(columnNames={"email","groupname"}) } )
    @Column(nullable=false, name="groupname", length=64)
    @Enumerated(EnumType.STRING)
    private List<LoginGroup> groups;
    
    @Column(name = "public1")
    private Boolean public1;
    
    @ManyToMany(mappedBy = "timeSlotCollection")
    private Collection<GroupupTimeslot> groupupTimeslotCollection;
    
    @ManyToMany(mappedBy = "timeSlotInviteCollection")
    private Collection<GroupupTimeslot> groupupTimeslotInvites;
    
    @ManyToMany(mappedBy = "groupupUserCollection")
    private Collection<GroupupGroup> groupupGroupCollection;
    
    @ManyToMany(mappedBy = "groupupInviteCollection")
    private Collection<GroupupGroup> groupupGroupInvites;

    public Collection<GroupupTimeslot> getGroupupTimeslotInvites() {
        return groupupTimeslotInvites;
    }

    public void setGroupupTimeslotInvites(Collection<GroupupTimeslot> groupupTimeslotInvites) {
        this.groupupTimeslotInvites = groupupTimeslotInvites;
    }

    public GroupupUser() {
        groups = new ArrayList<LoginGroup>();
        groups.add(LoginGroup.USER);
    }

    public Integer getId() {
        return id;
    }

    public List<LoginGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<LoginGroup> groups) {
        this.groups = groups;
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

    public Collection<GroupupGroup> getGroupupGroupInvites() {
        return groupupGroupInvites;
    }

    public void setGroupupGroupInvites(Collection<GroupupGroup> groupupGroupInvites) {
        this.groupupGroupInvites = groupupGroupInvites;
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
