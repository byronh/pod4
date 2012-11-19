/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.GroupupCourse;

/**
 *
 * @author mduppes
 */
@Stateless
public class CourseSearchBean {
    
    // Used for database access
    @PersistenceContext()
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public List<GroupupCourse> searchByDepartment(String department) {
        Query query = em.createNamedQuery("GroupupCourse.findByDept");
        query.setParameter("dept", department);
        return query.getResultList();
    }
    
    public List<GroupupCourse> searchByNumber(String number) {
        Query query = em.createNamedQuery("GroupupCourse.findByCoursenum");
        query.setParameter("coursenum", number);
        return query.getResultList();
    }
    
    public List<GroupupCourse> searchByTerm(String number) {
        Query query = em.createNamedQuery("GroupupCourse.findByTerm");
        query.setParameter("term", number);
        return query.getResultList();
    }
    
}
