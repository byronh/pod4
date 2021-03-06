/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author byron
 */
@Entity
@Table(name = "groupup_timeslot")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GroupupTimeslot.findAll", query = "SELECT g FROM GroupupTimeslot g"),
    @NamedQuery(name = "GroupupTimeslot.findById", query = "SELECT g FROM GroupupTimeslot g WHERE g.id = :id"),
    @NamedQuery(name = "GroupupTimeslot.findByReccurance", query = "SELECT g FROM GroupupTimeslot g WHERE g.reccurance = :reccurance"),
    @NamedQuery(name = "GroupupTimeslot.findByDayOfWeek", query = "SELECT g FROM GroupupTimeslot g WHERE g.dayOfWeek = :dayOfWeek"),
    @NamedQuery(name = "GroupupTimeslot.findByStartTime", query = "SELECT g FROM GroupupTimeslot g WHERE g.startTime = :startTime"),
    @NamedQuery(name = "GroupupTimeslot.findByEndTime", query = "SELECT g FROM GroupupTimeslot g WHERE g.endTime = :endTime")})
public class GroupupTimeslot implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @NotNull
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "reccurance")
    private Short reccurance;
    
    @Column(name = "day_of_week")
    private Short dayOfWeek;
    
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    
    @JoinTable(name = "groupup_user_timeslot", joinColumns = {
        @JoinColumn(name = "timeslot_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private Collection<GroupupUser> timeSlotCollection;
    
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private GroupupGroup groupId;
    
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private GroupupCourse courseId;

    @JoinTable(name = "groupup_user_timeslot_invites", joinColumns = {
        @JoinColumn(name = "timeslot_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private Collection<GroupupUser> timeSlotInviteCollection;
    
    public GroupupTimeslot() {
       
    }
    
    public void addUser(GroupupUser user) {
        if (this.timeSlotCollection.contains(user)) {
            return;
        }
        
        if (this.timeSlotInviteCollection.contains(user)) {
            this.timeSlotInviteCollection.remove(user);
        }
        
        this.timeSlotCollection.add(user);
    }
    
    public void removeUser(GroupupUser user) {
        this.timeSlotCollection.remove(user);
        this.timeSlotInviteCollection.remove(user);
    }
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Short getReccurance() {
        return reccurance;
    }

    public void setReccurance(Short reccurance) {
        this.reccurance = reccurance;
    }

    public Short getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Short dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
    
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Collection<GroupupUser> getTimeSlotCollection() {
        return timeSlotCollection;
    }

    public void setTimeSlotCollection(Collection<GroupupUser> timeSlotCollection) {
        this.timeSlotCollection = timeSlotCollection;
    }

    public Collection<GroupupUser> getTimeSlotInviteCollection() {
        return timeSlotInviteCollection;
    }

    public void setTimeSlotInviteCollection(Collection<GroupupUser> timeSlotInviteCollection) {
        this.timeSlotInviteCollection = timeSlotInviteCollection;
    }



    public GroupupGroup getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupupGroup groupId) {
        this.groupId = groupId;
    }

    public GroupupCourse getCourseId() {
        return courseId;
    }

    public void setCourseId(GroupupCourse courseId) {
        this.courseId = courseId;
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
        if (!(object instanceof GroupupTimeslot)) {
            return false;
        }
        GroupupTimeslot other = (GroupupTimeslot) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.GroupupTimeslot[ id=" + id + " ]";
    }
    
    
}
