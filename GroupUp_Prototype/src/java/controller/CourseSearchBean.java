/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
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
    
    // Used for transaction management
    @Resource
    private UserTransaction utx;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    

    




    
    public List<GroupupCourse> findAll() {
        Query query = em.createNamedQuery("GroupupCourse.findAll");
        List<GroupupCourse> courseList = query.getResultList();
        if(courseList.get(0) == null) {
            System.out.println("Loaded 0 courses. check for DB errors");
            courseList.clear();
        }
        return courseList;
    }
    
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
