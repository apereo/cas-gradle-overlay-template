package com.infusionsoft.cas.services;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * User: joe.koberstein
 * Date: 6/14/13 * Time: 3:05 PM
 */
@Service("serviceTicketService")
public class ServiceTicketServiceImpl implements ServiceTicketService{
    private static final Logger log = Logger.getLogger(ServiceTicketServiceImpl.class);

    @PersistenceContext
    private javax.persistence.EntityManager entityManager;

    @Transactional
    public void deleteOrphanedServiceTicketRecords(){
        try{
            Query query = entityManager.createNativeQuery("delete from SERVICETICKET where ticketGrantingTicket_ID NOT IN (select ID from TICKETGRANTINGTICKET)");
            int numberDeleted = query.executeUpdate();
            log.info("ServiceTicketService deleted " + numberDeleted + " orphaned service tickets");
        } catch(Exception e) {
            log.error("Error cleaning orphaned service tickets: " + e.getMessage(), e);
        }
    }
}
