/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "groupup_course")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GroupupCourse.findAll", query = "SELECT g FROM GroupupCourse g"),
    @NamedQuery(name = "GroupupCourse.findById", query = "SELECT g FROM GroupupCourse g WHERE g.id = :id"),
    @NamedQuery(name = "GroupupCourse.findByDept", query = "SELECT g FROM GroupupCourse g WHERE g.dept = :dept"),
    @NamedQuery(name = "GroupupCourse.findByCoursenum", query = "SELECT g FROM GroupupCourse g WHERE g.coursenum = :coursenum"),
    @NamedQuery(name = "GroupupCourse.findBySection", query = "SELECT g FROM GroupupCourse g WHERE g.section = :section"),
    @NamedQuery(name = "GroupupCourse.findByTerm", query = "SELECT g FROM GroupupCourse g WHERE g.term = :term")})
public class GroupupCourse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "id")
    private Integer id;
    
    @Size(max = 10)
    @Column(name = "dept")
    private String dept;
    
    @Size(max = 10)
    @Column(name = "coursenum")
    private String coursenum;
    
    @Size(max = 10)
    @Column(name = "section")
    private String section;
    
    @Column(name = "term")
    private Short term;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseId")
    private Collection<GroupupTimeslot> groupupTimeslotCollection;

    public GroupupCourse() {
    }

    public GroupupCourse(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getCoursenum() {
        return coursenum;
    }

    public void setCoursenum(String coursenum) {
        this.coursenum = coursenum;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Short getTerm() {
        return term;
    }

    public void setTerm(Short term) {
        this.term = term;
    }

    @XmlTransient
    public Collection<GroupupTimeslot> getGroupupTimeslotCollection() {
        return groupupTimeslotCollection;
    }

    public void setGroupupTimeslotCollection(Collection<GroupupTimeslot> groupupTimeslotCollection) {
        this.groupupTimeslotCollection = groupupTimeslotCollection;
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
        if (!(object instanceof GroupupCourse)) {
            return false;
        }
        GroupupCourse other = (GroupupCourse) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.GroupupCourse[ id=" + id + " ]";
    }
    
}
