/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author mduppes
 */
@Stateless
public class DataBaseAccessBean {
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

        // Inject persistence unit (data base access JPA2.0 entity)
    @PersistenceUnit
    EntityManagerFactory emf;
    
    

}
