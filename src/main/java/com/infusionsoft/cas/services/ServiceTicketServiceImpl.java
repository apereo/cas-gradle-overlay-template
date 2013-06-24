package com.infusionsoft.cas.services;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Service for dealing with Service Tickets
 */
@Service
@Transactional
public class ServiceTicketServiceImpl implements ServiceTicketService {
    private static final Logger log = Logger.getLogger(ServiceTicketServiceImpl.class);

    @PersistenceContext
    private javax.persistence.EntityManager entityManager;

    public int deleteOrphanedServiceTicketRecords(){
        int deletedCount = 0;
        try{
            Query query = entityManager.createNativeQuery("delete from SERVICETICKET where ticketGrantingTicket_ID NOT IN (select ID from TICKETGRANTINGTICKET)");
            deletedCount = query.executeUpdate();
            log.info("ServiceTicketService deleted " + deletedCount + " orphaned service tickets");
        } catch(Exception e) {
            log.error("Error cleaning orphaned service tickets: " + e.getMessage(), e);
        }
        return deletedCount;
    }
}
