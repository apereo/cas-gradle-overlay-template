package org.apereo.cas.infusionsoft.services;

/**
 * Service for dealing with Service Tickets
 */
public interface ServiceTicketService {
    int deleteOrphanedServiceTicketRecords();
}
